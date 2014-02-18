package ao.learn.mst.gen5.cfr

import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.learn.mst.gen5.strategy._
import ao.learn.mst.gen5.strategy.impl.{MapCfrOutcomeRegretBuffer, ArrayCfrAverageStrategyBuilder, ArrayCfrStrategyProfile}
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.ExtensiveGame
import scala._


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
case class OutcomeSampling2CfrMinimizer[State, InformationSet, Action](
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
    val explorationProbability: Double =
      0.6

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
      private val buffer: CfrOutcomeRegretBuffer =
        new MapCfrOutcomeRegretBuffer


      //----------------------------------------------------------------------------------------------------------------
      def walkTreeFromRoot(): Unit = {
        for (playerIndex <- 0 until game.playerCount) {
          walkTree(
            playerIndex,
            game.initialStateNode,
            Seq.fill(game.playerCount)(1.0),
            Seq.fill(game.playerCount)(1.0))

          buffer.commit(strategyProfile)
        }

        //println(s"buffer: ${buffer}")
        //println(s"strategyProfile: $strategyProfile")
//        buffer.commit(strategyProfile)
      }


      //----------------------------------------------------------------------------------------------------------------
      private def walkTree(
          updatePlayer        : Int,
          stateNode           : ExtensiveStateNode[State, InformationSet, Action],
          reachProbabilities  : Seq[Double],
          sampleProbabilities : Seq[Double]
          ): SampleOutcome =
      {
        reachProbabilities.foreach(checkProb)

        stateNode match {
          case StateTerminal(_, Terminal(payoffs)) =>
            SampleOutcome(
              payoffs(updatePlayer),
              sampleProbabilities.product,
              1.0)


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
              sampleProbabilities)


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
                sampleAction(curMoveProbs, sampleProb, explorationProbability)
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

              val realActionCandidates: Traversable[Action] =
                decision.node.choices
                  .filter(indexOf(_) == takeAction)

              realActionCandidates
                .map(a => (a, math.random))
                .maxBy(_._2)._1
            }

            val newGameState: ExtensiveStateNode[State, InformationSet, Action] =
              game.transitionStateNode(stateNode, realAction)

            checkProb(moveProb)
            val nextReachProbabilities: Seq[Double] =
              reachProbabilities
                .updated(nextToAct, moveProb * reachProbabilities(nextToAct))

            val outcome: SampleOutcome =
              walkTree(
                updatePlayer,
                newGameState,
                nextReachProbabilities,
                nextSampleProbabilities)

            val updatePlayerPayoff: Double =
              outcome.payoff

            val ctlReach: Double =
              outcome.suffixReachProbability

            val itlReach: Double =
              outcome.suffixReachProbability * moveProb

            val nextToActReachProbability: Double =
              reachProbabilities(nextToAct)

            val opponentReachProbability: Double =
              reachProbabilities
                .zipWithIndex
                .filterNot(_._2 == nextToAct)
                .map(_._1)
                .product

            if (nextToAct == updatePlayer)
            {
              val U = updatePlayerPayoff * opponentReachProbability / outcome.sampleProbability

              val counterfactualRegret: Seq[Double] =
                for (a <- 0 until actionsHere)
                yield
                    if (a == takeAction) {
                      U * (ctlReach - itlReach)
                    } else {
                      -U * itlReach
                    }

              buffer.bufferRegret(
                infoIndex, counterfactualRegret)
            }

            if (averageStrategy) {
              if (nextToAct != updatePlayer)
              {
                val stochasticWeight: Double =
                  (1.0 / sampleProbabilities.product) * nextToActReachProbability

                averageStrategyBuilder.get.update(
                  infoIndex, curMoveProbs, stochasticWeight)
              }
            }

            outcome
              .scakeSuffixReach(moveProb)
        }
      }


      def checkProb(probability: Double): Unit =
        assert(0 <= probability && probability <= 1.0)

      def checkProbNotZero(probability: Double): Unit =
        assert(0 < probability && probability <= 1.0)


      def sampleAction(
          curMoveProbs: Seq[Double],
          sampleProb: ProbRef,
          epsilon: Double)
          : Int =
      {
        val actionsHere: Int =
          curMoveProbs.length

        val dist: Seq[Double] =
            curMoveProbs
              .map(p => epsilon * (1.0 / actionsHere) + (1.0 - epsilon) * p)

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


  //------------------------------------------------------------------------------------------------------------------
  private class ProbRef(var probability: Double)

  private case class SampleOutcome(
    payoff: Double,
    sampleProbability: Double,
    suffixReachProbability: Double)
  {
    def scakeSuffixReach(moveProb: Double): SampleOutcome =
      copy(suffixReachProbability = suffixReachProbability * moveProb)
  }
}
