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
            walkTree(
              updatePlayer,
              sampleChance(stateNode, outcomes),
              reachProbabilities,
              sampleProbabilities)


          case decision: StateDecision[State, InformationSet, Action] =>
            walkDecision(
              updatePlayer,
              decision,
              reachProbabilities,
              sampleProbabilities)
        }
      }


      //----------------------------------------------------------------------------------------------------------------
      def sampleChance(
          stateNode : ExtensiveStateNode[State, InformationSet, Action],
          outcomes  : Traversable[Outcome[Action]]
          ): ExtensiveStateNode[State, InformationSet, Action] =
      {
        val sampledOutcome: Action =
          outcomes
            .map(o => (o.action, o.probability * math.random))
            .maxBy(_._2)._1

        val sampledChance: ExtensiveStateNode[State, InformationSet, Action] =
          game.transitionStateNode(stateNode, sampledOutcome)

        sampledChance
      }


      //----------------------------------------------------------------------------------------------------------------
      def walkDecision(
          updatePlayer        : Int,
          decision            : StateDecision[State, InformationSet, Action],
          reachProbabilities  : Seq[Double],
          sampleProbabilities : Seq[Double]
          ): SampleOutcome =
      {
        val nextToAct: Int =
          decision.node.nextToAct.index

        val nextToActIsUpdatePlayer: Boolean =
          nextToAct == updatePlayer
        
        val infoSet: InformationSet =
          decision.node.informationSet

        val abstractActionCount: Int =
          abstraction.actionCount(infoSet)

        val infoIndex: Int =
          abstraction.informationSetIndex(infoSet)

        val strategy: Seq[Double] =
          strategyProfile.positiveRegretMatchingStrategy(
            infoIndex, abstractActionCount)

        val sampledAction: ActionSample =
          sampleAction(strategy, nextToActIsUpdatePlayer)

        val nextSampleProbabilities: Seq[Double] =
          sampleProbabilities
            .updated(nextToAct, sampledAction.probability * sampleProbabilities(nextToAct))

        val sampledActionStrategyProbability: Double =
          strategy(sampledAction.index)

        val realAction: Action =
          translateFromAbstraction(
            infoSet, decision.node.choices, sampledAction.index)

        val newGameState: ExtensiveStateNode[State, InformationSet, Action] =
          game.transitionStateNode(decision, realAction)

        val nextReachProbabilities: Seq[Double] =
          reachProbabilities
            .updated(nextToAct, sampledActionStrategyProbability * reachProbabilities(nextToAct))

        val outcome: SampleOutcome =
          walkTree(
            updatePlayer,
            newGameState,
            nextReachProbabilities,
            nextSampleProbabilities)

        if (nextToActIsUpdatePlayer) {
          buffer.bufferRegret(
            infoIndex,
            counterfactualRegret(
              nextToAct, abstractActionCount, sampledAction.index, sampledActionStrategyProbability, reachProbabilities, outcome))
        }

        if (averageStrategy && ! nextToActIsUpdatePlayer) {
          averageStrategyBuilder.get.update(
            infoIndex, strategy,
            stochasticWeight(nextToAct, reachProbabilities, sampleProbabilities))
        }

        outcome
          .scakeSuffixReach(sampledActionStrategyProbability)
      }


      //----------------------------------------------------------------------------------------------------------------
      def translateFromAbstraction(
        infoSet: InformationSet, choices: Traversable[Action], abstractAction: Int): Action =
      {
        def indexOf(choice: Action): Int =
          abstraction.actionSubIndex(infoSet, choice)

        val realActionCandidates: Traversable[Action] =
          choices.filter(indexOf(_) == abstractAction)

        realActionCandidates
          .map(a => (a, math.random))
          .maxBy(_._2)._1
      }



      //----------------------------------------------------------------------------------------------------------------
      def stochasticWeight(nextToAct: Int, reachProbabilities: Seq[Double], sampleProbabilities: Seq[Double]): Double = {
        val nextToActReachProbability: Double =
          reachProbabilities(nextToAct)

        (1.0 / sampleProbabilities.product) * nextToActReachProbability
      }


      private def counterfactualRegret(
          nextToAct: Int,
          actionCount: Int,
          sampledActionIndex: Int,
          moveProb: Double,
          reachProbabilities: Seq[Double],
          outcome: SampleOutcome
          ): Seq[Double] =
      {
        val updatePlayerPayoff: Double =
          outcome.payoff

        val ctlReach: Double =
          outcome.suffixReachProbability

        val itlReach: Double =
          outcome.suffixReachProbability * moveProb

        val opponentReachProbability: Double =
          reachProbabilities
            .zipWithIndex
            .filterNot(_._2 == nextToAct)
            .map(_._1)
            .product

        val U = updatePlayerPayoff * opponentReachProbability / outcome.sampleProbability

        val counterfactualRegret: Seq[Double] =
          for (a <- 0 until actionCount)
          yield
            if (a == sampledActionIndex) {
              U * (ctlReach - itlReach)
            } else {
              -U * itlReach
            }

        counterfactualRegret
      }

      
      //----------------------------------------------------------------------------------------------------------------
      private def sampleAction(
          strategy       : Seq[Double],
          updatingPlayer : Boolean
          ): ActionSample =
      {
        if (updatingPlayer) {
          sampleAction(exploratoryActionProbabilities(strategy))
        } else {
          sampleAction(strategy)
        }
      }

      def exploratoryActionProbabilities(strategy: Seq[Double]): Seq[Double] = {
        val minProbability: Double =
          explorationProbability * (1.0 / strategy.length)

        val strategyFactor: Double =
          1.0 - explorationProbability

        strategy
          .map(p => minProbability + strategyFactor * p)
      }

      private def sampleAction(
          actionProbabilities: Seq[Double]
          ): ActionSample =
      {
        val roll: Double =
          math.random

        var sum: Double =
          0.0

        for (a <- 0 until actionProbabilities.length) {
          if (roll >= sum && roll < sum + actionProbabilities(a)) {
            return ActionSample(a, actionProbabilities(a))
          }

          sum += actionProbabilities(a)
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


  //---------------------------------------------------------------------------------------------------------------------
  private def checkProb(probability: Double): Unit =
    assert(0 <= probability && probability <= 1.0)

  private def checkProbNotZero(probability: Double): Unit =
    assert(0 < probability && probability <= 1.0)


  //--------------------------------------------------------------------------------------------------------------------
  private case class ActionSample(
      index: Int,
      probability: Double)
  {
    checkProbNotZero(probability)
  }
  
  private case class SampleOutcome(
    payoff: Double,
    sampleProbability: Double,
    suffixReachProbability: Double)
  {
    def scakeSuffixReach(moveProb: Double): SampleOutcome =
      copy(suffixReachProbability = suffixReachProbability * moveProb)
  }
}
