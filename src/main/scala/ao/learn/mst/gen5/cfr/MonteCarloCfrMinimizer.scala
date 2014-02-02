package ao.learn.mst.gen5.cfr

import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.learn.mst.gen3.strategy._
import ao.learn.mst.gen3.strategy.impl.{ArrayCfrAverageStrategyBuilder, AveragingCfrStrategyProfile, MapCfrRegretBuffer, ArrayCfrStrategyProfile}
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.ExtensiveGame
import scala._
import scala.Function
import scala.collection.immutable.SortedMap
import ao.learn.mst.gen4.sp.TerminalPartition
import scala.annotation.tailrec
import ao.learn.mst.gen4.Rational


//----------------------------------------------------------------------------------------------------------------------
/**
 * http://mlanctot.info/files/papers/generalized-mccfr-tr.pdf
 *
 * todo: explicitly handle case for abstract actions
 *
 * @param averageStrategy true if strategy should be averaged (e.g. for two-player zero-sum)
 * @tparam State extensive game state
 * @tparam InformationSet player view
 * @tparam Action finite action
 */
case class MonteCarloCfrMinimizer[State, InformationSet, Action](
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
    val epsilonOnPolicy: Double =
      0.6

    val strategyProfile: CfrStrategyProfile =
      new ArrayCfrStrategyProfile

    val averageStrategyBuilder: Option[CfrAverageStrategyBuilder] =
      if (averageStrategy)
        Some(new ArrayCfrAverageStrategyBuilder)
      else
        None


    //------------------------------------------------------------------------------------------------------------------Z
    def optimize(extensiveAbstraction: ExtensiveAbstraction[InformationSet, Action]) : Unit = {
      new Optimization(extensiveAbstraction).walkTreeFromRoot()
    }


    //------------------------------------------------------------------------------------------------------------------Z
    private class Optimization(
      abstraction: ExtensiveAbstraction[InformationSet, Action])
    {
//      val buffer : CfrRegretBuffer =
//        new MapCfrRegretBuffer

      //----------------------------------------------------------------------------------------------------------------
      private def sampleOutcome(outcomes : Traversable[Outcome[Action]]): Action =
        outcomes
          .map(o => (o.action, o.probability * math.random))
          .maxBy(_._2)._1


      private def sampleOutcomeState(
          from     : ExtensiveStateNode[State, InformationSet, Action],
          outcomes : Traversable[Outcome[Action]])
          : ExtensiveStateNode[State, InformationSet, Action] = {
        val sampledAction: Action =
          sampleOutcome(outcomes)

        game.transitionStateNode(from, sampledAction)
      }


      //----------------------------------------------------------------------------------------------------------------
      def walkTreeFromRoot() {
        for (playerIndex <- 0 until game.playerCount) {
          walkTree(
            playerIndex,
            game.initialStateNode,
            Seq.fill(game.playerCount)(1.0),
            Seq.fill(game.playerCount)(1.0))

//          buffer.commit(strategyProfile)
        }
      }


      //----------------------------------------------------------------------------------------------------------------
      private def walkTree(
          updatePlayer        : Int,
          stateNode           : ExtensiveStateNode[State, InformationSet, Action],
          reachProbabilities  : Seq[Double],
          sampleProbabilities : Seq[Double])
          : TerminalSample =
      {
        stateNode match {
          case StateTerminal(_, Terminal(payoffs)) =>
            TerminalSample(
              1.0,
              sampleProbabilities.product,
              payoffs(updatePlayer))

          case StateChance(_, Chance(outcomes)) =>
            walkTree(
              updatePlayer,
              sampleOutcomeState(stateNode, outcomes),
              reachProbabilities,
              sampleProbabilities)

          case decision: StateDecision[State, InformationSet, Action] =>
            if (decision.node.nextToAct.index == updatePlayer) {
              walkProponent(
                updatePlayer,
                decision,
                reachProbabilities,
                sampleProbabilities)
            } else {
              walkOpponent(
                updatePlayer,
                decision,
                reachProbabilities,
                sampleProbabilities)
            }
        }
      }


      //----------------------------------------------------------------------------------------------------------------
      private def walkOpponent(
          updatePlayer        : Int,
          decision            : StateDecision[State, InformationSet, Action],
          reachProbabilities  : Seq[Double],
          sampleProbabilities : Seq[Double])
          : TerminalSample =
      {
        val infoIndex: Int =
          abstraction.informationSetIndex(decision.node.informationSet)

        val strategy: Seq[Double] =
          strategyProfile.positiveRegretMatchingStrategy(
            infoIndex, abstraction.actionCount(decision.node.informationSet))

        def choiceStrategy(choice: Action): Double = {
          val choiceSubIndex: Int =
            abstraction.actionSubIndex(decision.node.informationSet, choice)

          strategy(choiceSubIndex)
        }

        val actionWeight: Map[Action, Double] =
          decision.node.choices
            .map(c => (c, choiceStrategy(c)))
            .toMap

        val sampledAction: Action =
          actionWeight
          .map(aw => (aw._1, aw._2 * math.random))
          .maxBy(_._2)._1

        val nextToAct: Int =
          decision.node.nextToAct.index

        val nextSampleProbabilities: Seq[Double] = {
          val sampleProbability: Double =
            actionWeight(sampledAction)

          sampleProbabilities
            .updated(nextToAct, sampleProbability * sampleProbabilities(nextToAct))
        }

        // on-policy sampling for opponent nodes
        val nextReachProbabilities: Seq[Double] =
          nextSampleProbabilities

        val nextToActReachProbability: Double =
          reachProbabilities(nextToAct)

        if (averageStrategy) {
          // 26: for a ∈ A(I) do
          // 27:   sI[a] ← sI[a] + (σ[a]/q)
          // 28: end for

          // update average strategy

          // stochastically-weighted averaging cfros.cpp line 163
          // double inc = (1.0 / (sprob1*sprob2))*myreach*is.curMoveProbs[a];
          val stochasticWeight: Double =
            (1.0 / sampleProbabilities.product) * nextToActReachProbability

          averageStrategyBuilder.get.update(
            infoIndex, strategy, stochasticWeight)
        }

        val sample: TerminalSample =
          walkTree(
            updatePlayer,
            game.transitionStateNode(
              decision, sampledAction),
            nextReachProbabilities,
            nextSampleProbabilities)

        val actionProbability: Double =
          choiceStrategy(sampledAction)

        val ctlReach: Double = // what is "ctl"?
          sample.suffixReachProbability

        val itlReach: Double = // what is "itl"
          ctlReach * actionProbability

        TerminalSample(
          itlReach,
          sample.sampleProbability,
          sample.updatePlayerPayoff)
      }


      //----------------------------------------------------------------------------------------------------------------
      private def walkProponent(
          updatePlayer        : Int,
          decision            : StateDecision[State, InformationSet, Action],
          reachProbabilities  : Seq[Double],
          sampleProbabilities : Seq[Double])
          : TerminalSample =
      {
        val infoIndex: Int =
          abstraction.informationSetIndex(decision.node.informationSet)

        val strategy: Seq[Double] =
          strategyProfile.positiveRegretMatchingStrategy(
            infoIndex, abstraction.actionCount(decision.node.informationSet))

        def choiceStrategy(choice: Action): Double = {
          val choiceSubIndex: Int =
            abstraction.actionSubIndex(decision.node.informationSet, choice)

          strategy(choiceSubIndex)
        }

        val actionWeight: Map[Action, Double] = {
          val minProbability: Double =
            epsilonOnPolicy * (1.0 / decision.node.choices.size)

          def skewTowardUniformity(actionProbability: Double): Double =
            minProbability + (1.0 - epsilonOnPolicy) * actionProbability

          decision.node.choices
            .map(c => (c, skewTowardUniformity(choiceStrategy(c))))
            .toMap
        }

        val sampledAction: Action =
          actionWeight
            .map(aw => (aw._1, aw._2 * math.random))
            .maxBy(_._2)._1

        val nextToAct: Int =
          decision.node.nextToAct.index

        val nextSampleProbabilities: Seq[Double] = {
          val sampleProbability: Double =
            actionWeight(sampledAction)

          sampleProbabilities
            .updated(nextToAct, sampleProbability * sampleProbabilities(nextToAct))
        }

        val actionProbability: Double =
          choiceStrategy(sampledAction)

        val nextReachProbabilities: Seq[Double] =
          reachProbabilities
            .updated(nextToAct, actionProbability * reachProbabilities(nextToAct))

        val sample: TerminalSample =
          walkTree(
            updatePlayer,
            game.transitionStateNode(
              decision, sampledAction),
            nextReachProbabilities,
            nextSampleProbabilities)

        val ctlReach: Double = // what is "ctl"?
          sample.suffixReachProbability

        val itlReach: Double = // what is "itl"
          ctlReach * actionProbability

        val nonActingPlayerReachProbability: Double =
          reachProbabilities
            .zipWithIndex
            .filterNot(_._2 == nextToAct)
            .map(_._1)
            .product

        val counterfactualRegret: Seq[Double] = {
          // cfros.cpp lines 142 .. 155
          //
          //  for (int a = 0; a < actionshere; a++)
          //  {
          //    // oppreach and sprob2 cancel in the case of stochastically-weighted averaging
          //    //is.cfr[a] += (moveEVs[a] - stratEV);
          //
          //    double U = updatePlayerPayoff * oppreach / rtlSampleProb;
          //    double r = 0.0;
          //    if (a == takeAction)
          //      r = U * (ctlReach - itlReach);
          //    else
          //      r = -U * itlReach;
          //
          //    is.cfr[a] += r;
          //  }

          val U: Double = // what does "U" mean?
            sample.updatePlayerPayoff * nonActingPlayerReachProbability / sample.sampleProbability

          val sampledActionIndex: Int =
            abstraction.actionSubIndex(decision.node.informationSet, sampledAction)

          for (action <- 0 until abstraction.actionCount(decision.node.informationSet)) yield
            if (action == sampledActionIndex) {
              U * (ctlReach - itlReach)
            } else {
              -U * itlReach
            }
        }

        strategyProfile.update(
          infoIndex, counterfactualRegret)

        TerminalSample(
          itlReach,
          sample.sampleProbability,
          sample.updatePlayerPayoff)
      }


      //----------------------------------------------------------------------------------------------------------------
      @tailrec
      private def probe(stateNode: ExtensiveStateNode[State, InformationSet, Action]): Seq[Double] =
      {
        stateNode.node match {
          case Terminal(payoffs) => payoffs

          case Chance(outcomes) =>
            probe(sampleOutcomeState(stateNode, outcomes))

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

  case class TerminalSample(
      suffixReachProbability : Double,
      sampleProbability      : Double,
      updatePlayerPayoff     : Double)

//  class ProbRef(var probability)
}
