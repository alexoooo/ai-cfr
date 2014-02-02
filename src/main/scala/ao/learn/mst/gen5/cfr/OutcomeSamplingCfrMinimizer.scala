package ao.learn.mst.gen5.cfr

import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.learn.mst.gen3.strategy._
import ao.learn.mst.gen3.strategy.impl.{ArrayCfrAverageStrategyBuilder, ArrayCfrStrategyProfile}
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.ExtensiveGame
import scala._
import scala.annotation.tailrec


//----------------------------------------------------------------------------------------------------------------------
/**
 * itl = internal?
 *
 * http://mlanctot.info/files/papers/generalized-mccfr-tr.pdf
 *
 * Port of cfros.cpp from Bluff at http://mlanctot.info/downloads.php
 *
 * @param averageStrategy true if strategy should be averaged (e.g. for two-player zero-sum)
 * @tparam State extensive game state
 * @tparam InformationSet player view
 * @tparam Action finite action
 */
case class OutcomeSamplingCfrMinimizer[State, InformationSet, Action](
    averageStrategy : Boolean = false)
  extends ExtensiveSolver[State, InformationSet, Action]
{
  //--------------------------------------------------------------------------------------------------------------------
  def initialSolution(
    game: ExtensiveGame[State, InformationSet, Action]
    ): SolutionApproximation[InformationSet, Action] =
  {
    new Solution(game)
  }


  //--------------------------------------------------------------------------------------------------------------------
  private class Solution(
      game: ExtensiveGame[State, InformationSet, Action])
      extends SolutionApproximation[InformationSet, Action]
  {
    val strategyProfile: CfrStrategyProfile =
      new ArrayCfrStrategyProfile

    val averageStrategyBuilder: Option[CfrAverageStrategyBuilder] =
      if (averageStrategy)
        Some(new ArrayCfrAverageStrategyBuilder)
      else
        None


    //------------------------------------------------------------------------------------------------------------------
    def optimize(extensiveAbstraction: ExtensiveAbstraction[InformationSet, Action]) : Unit = {
      new Optimization(extensiveAbstraction).walkTreeFromRoot()
    }


    //------------------------------------------------------------------------------------------------------------------
    private class Optimization(
      abstraction: ExtensiveAbstraction[InformationSet, Action])
    {
      //----------------------------------------------------------------------------------------------------------------
      def walkTreeFromRoot() {
        for (playerIndex <- 0 until game.playerCount) {
          walkTree(
            playerIndex,
            game.initialStateNode,
            Seq.fill(game.playerCount)(1.0),
            Seq.fill(game.playerCount)(1.0),
            new ProbRef(1.0),
            new ProbRef(1.0))
        }
      }


      //----------------------------------------------------------------------------------------------------------------
      private def walkTree(
          updatePlayer        : Int,
          stateNode           : ExtensiveStateNode[State, InformationSet, Action],
          reachProbabilities  : Seq[Double],
          sampleProbabilities : Seq[Double],
          suffixReach         : ProbRef,
          rtlSampleProb       : ProbRef)
          : Double =
      {
        reachProbabilities.foreach(checkProb)

        stateNode match {
          case StateTerminal(_, Terminal(payoffs)) =>
            suffixReach.probability = 1.0
            rtlSampleProb.probability = sampleProbabilities.product
            payoffs(updatePlayer)


          case StateChance(_, Chance(outcomes)) =>
            val sampledAction: Action =
              outcomes
                .map(o => (o.action, o.probability * math.random))
                .maxBy(_._2)._1

            val nextState =
              game.transitionStateNode(stateNode, sampledAction)

            walkTree(
              updatePlayer,
              nextState,
              reachProbabilities,
              sampleProbabilities,
              suffixReach,
              rtlSampleProb)


          case decision: StateDecision[State, InformationSet, Action] =>
            val nextToAct: Int =
              decision.node.nextToAct.index

            val infoSet: InformationSet =
              decision.node.informationSet

            val actionsHere: Int =
              abstraction.actionCount(infoSet)

            val infoIndex: Int =
              abstraction.informationSetIndex(infoSet)

            val curMoveProbs: Seq[Double] =
              strategyProfile.positiveRegretMatchingStrategy(
                infoIndex, actionsHere)

            val sampleProb: ProbRef =
              new ProbRef(-1.0)

            val takeAction: Int =
              if (nextToAct == updatePlayer) {
                sampleAction(curMoveProbs, sampleProb, 0.6)
              } else {
                sampleAction(curMoveProbs, sampleProb, 0.0)
              }

            checkProbNotZero(sampleProb.probability)

            val nextSampleProbabilities: Seq[Double] =
              sampleProbabilities
                .updated(nextToAct, sampleProb.probability * sampleProbabilities(nextToAct))

            val moveProb: Double =
              curMoveProbs(takeAction)

            val realAction: Action = {
              def indexOf(choice: Action): Int =
                abstraction.actionSubIndex(infoSet, choice)

              val candidates: Seq[Action] =
                decision.node.choices
                  .filter(indexOf(_) == takeAction)
                  .toSeq

              candidates((candidates.length * math.random).toInt)
            }

            val newGameState: ExtensiveStateNode[State, InformationSet, Action] =
              game.transitionStateNode(stateNode, realAction)

            checkProb(moveProb)
            val nextReachProbabilities: Seq[Double] =
              reachProbabilities
                .updated(nextToAct, moveProb * reachProbabilities(nextToAct))

            val newSuffixReach: ProbRef =
              new ProbRef(1.0)

            val updatePlayerPayoff: Double =
              walkTree(
                updatePlayer,
                newGameState,
                nextReachProbabilities,
                nextSampleProbabilities,
                newSuffixReach,
                rtlSampleProb)

            val ctlReach: Double =
              newSuffixReach.probability

            val itlReach: Double =
              newSuffixReach.probability * moveProb

            suffixReach.probability = itlReach

            val nextToActReachProbability: Double =
              reachProbabilities(nextToAct)

            val opponentReachProbability: Double =
              reachProbabilities.product / nextToActReachProbability

            if (nextToAct == updatePlayer)
            {
              val U = updatePlayerPayoff * opponentReachProbability / rtlSampleProb.probability

              val counterfactualRegret: Seq[Double] =
                for (a <- 0 until actionsHere)
                yield
                    if (a == takeAction) {
                      U * (ctlReach - itlReach)
                    } else {
                      -U * itlReach
                    }

              strategyProfile.update(
                infoIndex, counterfactualRegret)
            }

            if (averageStrategyBuilder.isDefined) {
              if (nextToAct != updatePlayer)
              {
                val stochasticWeight: Double =
                  (1.0 / sampleProbabilities.product) * nextToActReachProbability

                averageStrategyBuilder.get.update(
                  infoIndex, curMoveProbs, stochasticWeight)
              }
            }

            updatePlayerPayoff
        }
      }

      def checkProb(probability: Double): Unit =
        assert(0 <= probability && probability <= 1.0)

      def checkProbNotZero(probability: Double): Unit =
        assert(0 < probability && probability <= 1.0)

      def sampleAction(curMoveProbs: Seq[Double], sampleProb: ProbRef, epsilon: Double): Int = {
        val actionsHere: Int =
          curMoveProbs.length

        val dist: Seq[Double] =
          curMoveProbs.map(p =>
            epsilon * (1.0 / actionsHere) + (1.0 - epsilon) * p)

        val roll: Double =
          math.random

        var sum: Double =
          0.0

        for (a <- 0 until actionsHere) {
          if (roll >= sum && roll < sum + dist(a)) {
            sampleProb.probability = dist(a)
            return a
          }

          sum += dist(a)
        }

        throw new IllegalStateException()
      }


      @tailrec
      private def probe(stateNode: ExtensiveStateNode[State, InformationSet, Action]): Seq[Double] =
      {
        stateNode.node match {
          case Terminal(payoffs) => payoffs

          case Chance(outcomes) =>
            val sampledAction: Action =
              outcomes
                .map(o => (o.action, o.probability * math.random))
                .maxBy(_._2)._1

            val sampledOutcomeState: ExtensiveStateNode[State, InformationSet, Action] =
              game.transitionStateNode(stateNode, sampledAction)

            probe(sampledOutcomeState)

          case Decision(_, info, choices) =>
            val strategy: Seq[Double] =
              strategyProfile.positiveRegretMatchingStrategy(
                abstraction.informationSetIndex(info), abstraction.actionCount(info))

            val actionWeight: Map[Action, Double] =
              choices
                .map(c => (c, strategy(abstraction.actionSubIndex(info, c))))
                .toMap

            val sampledAction: Action =
              actionWeight
                .map(aw => (aw._1, aw._2 * math.random))
                .maxBy(_._2)._1

            probe(game.transitionStateNode(stateNode, sampledAction))
        }
      }
    }


    //------------------------------------------------------------------------------------------------------------------
    def strategy: ExtensiveStrategyProfile = {
      if (averageStrategy) {
        averageStrategyBuilder.get.toExtensiveStrategyProfile
      } else {
        strategyProfile.toExtensiveStrategyProfile
      }
    }
  }

  class ProbRef(var probability: Double)
}
