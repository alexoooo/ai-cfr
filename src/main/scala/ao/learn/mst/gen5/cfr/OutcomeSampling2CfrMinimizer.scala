package ao.learn.mst.gen5.cfr

import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.ExtensiveGame
import scala._
import ao.learn.mst.lib.NumUtils
import ao.learn.mst.gen5.state._
import ao.learn.mst.gen5.state.impl._
import ao.learn.mst.gen5.node.StateTerminal
import ao.learn.mst.gen5.node.StateChance
import ao.learn.mst.gen5.node.StateDecision
import ao.learn.mst.gen5.node.Chance
import scalaz._
import Scalaz._
import ao.learn.mst.gen5.state.regret.{RegretMatcher, RegretStore, RegretBuffer}
import ao.learn.mst.gen5.state.strategy.{StrategyStore, StrategyBuffer}
import ao.learn.mst.gen5.state.regret.impl.{MapRegretBuffer, UniformDefaultRegretMatcher, ArrayRegretStore}
import ao.learn.mst.gen5.state.strategy.impl.{MapStrategyBuffer, ArrayStrategyStore}


//----------------------------------------------------------------------------------------------------------------------
/**
 * http://mlanctot.info/files/papers/generalized-mccfr-tr.pdf
 *
 * @param averageStrategy true if strategy should be averaged (e.g. for two-player zero-sum)
 * @param explorationProbability probability of action being explored (as opposed to exploited) must be in (0, 1)
 * @tparam State extensive game state
 * @tparam InformationSet player view
 * @tparam Action finite action
 */
case class OutcomeSampling2CfrMinimizer[State, InformationSet, Action](
    averageStrategy: Boolean = false,
    explorationProbability: Double = 0.6)
  extends ExtensiveSolver[State, InformationSet, Action]
{
  //--------------------------------------------------------------------------------------------------------------------
  def initialSolution(
      game: ExtensiveGame[State, InformationSet, Action])
      : SolutionApproximation[InformationSet, Action] =
  {
    new Solution(game)
  }


  //--------------------------------------------------------------------------------------------------------------------
  private class Solution(
      game: ExtensiveGame[State, InformationSet, Action])
      extends SolutionApproximation[InformationSet, Action]
  {
    private val regretStore: RegretStore =
      new ArrayRegretStore

    private val strategyStore: Option[StrategyStore] =
      averageStrategy.option(new ArrayStrategyStore)


    private val regretBuffer: RegretBuffer =
      new MapRegretBuffer

    private val regretMatcher: RegretMatcher =
      UniformDefaultRegretMatcher

    private val strategyBuffer: Option[StrategyBuffer] =
      averageStrategy.option(new MapStrategyBuffer)

    private val mixedStrategyView: MixedStrategyView =
      new MixedStrategyView(regretMatcher, regretStore, strategyStore)


    //------------------------------------------------------------------------------------------------------------------
    def optimize(extensiveAbstraction: ExtensiveAbstraction[InformationSet, Action]): Unit = {
      new Optimization(extensiveAbstraction).walkTreeFromRoot()
    }


    //------------------------------------------------------------------------------------------------------------------
    private class Optimization(
        abstraction: ExtensiveAbstraction[InformationSet, Action])
    {
      //----------------------------------------------------------------------------------------------------------------
      def walkTreeFromRoot(): Unit = {
        for (playerIndex <- 0 until game.playerCount) {
          walkTree(
            playerIndex,
            game.initialStateNode,
            Seq.fill(game.playerCount)(1.0),
            Seq.fill(game.playerCount)(1.0))

          regretBuffer.flush(regretStore)
        }

//        regretBuffer.flush(regretStore)

        for (sb <- strategyBuffer; ss <- strategyStore) {
          sb.flush(ss)
        }

        //println(s"buffer: ${buffer}")
        //println(s"strategyProfile: $strategyProfile")
      }


      //----------------------------------------------------------------------------------------------------------------
      private def walkTree(
          updatePlayer        : Int,
          stateNode           : ExtensiveStateNode[State, InformationSet, Action],
          reachProbabilities  : Seq[Double],
          sampleProbabilities : Seq[Double]
          ): SampleOutcome =
      {
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
        // todo: generate single random number and select max action in single pass
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

        val availableActions: Set[Int] =
          decision.node.choices
            .map(c => abstraction.actionSubIndex(infoSet, c))
            .toSet

        val missingActions: Set[Int] =
          (0 until abstractActionCount).filterNot(availableActions.contains).toSet

        val strategy: Seq[Double] =
          omitMissingProbabilities(
            regretMatcher.positiveRegretStrategy(
              regretStore.cumulativeRegret(infoIndex),
              abstractActionCount),
            missingActions)

        val sampledAction: ActionSample =
          sampleAction(strategy, nextToActIsUpdatePlayer, missingActions)

        val nextSampleProbabilities: Seq[Double] =
          sampleProbabilities
            .updated(nextToAct, sampledAction.probability * sampleProbabilities(nextToAct))

        val sampledActionStrategyProbability: Double =
          strategy(sampledAction.index)

        val newGameState: ExtensiveStateNode[State, InformationSet, Action] = {
          val concreteSampledAction: Action =
            translateFromAbstraction(
              infoSet, decision.node.choices, sampledAction.index)
          
          game.transitionStateNode(decision, concreteSampledAction)
        }

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
          val regret: Seq[Double] =
            counterfactualRegret(
              nextToAct,
              abstractActionCount,
              sampledAction.index,
              sampledActionStrategyProbability,
              reachProbabilities,
              outcome,
              missingActions)

          regretBuffer.add(infoIndex, regret)
        }

        if (! nextToActIsUpdatePlayer) {
          val externalReachProbability: Double =
            stochasticWeight(nextToAct, reachProbabilities, sampleProbabilities)

          strategyBuffer.foreach(_.add(
            infoIndex, strategy, externalReachProbability))
        }

        outcome
          .scaleSuffixReach(sampledActionStrategyProbability)
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
      def stochasticWeight(
          nextToAct           : Int,
          reachProbabilities  : Seq[Double],
          sampleProbabilities : Seq[Double]
          ): Double =
      {
        val nextToActReachProbability: Double =
          reachProbabilities(nextToAct)

        (1.0 / sampleProbabilities.product) * nextToActReachProbability
      }


      private def counterfactualRegret(
          nextToAct                        : Int,
          actionCount                      : Int,
          sampledActionIndex               : Int,
          sampledActionStrategyProbability : Double,
          reachProbabilities               : Seq[Double],
          outcome                          : SampleOutcome,
          missingActions                   : Set[Int]
          ): Seq[Double] =
      {
        val updatePlayerPayoff: Double =
          outcome.payoff

        val ctlReach: Double =
          outcome.suffixReachProbability

        val itlReach: Double =
          outcome.suffixReachProbability * sampledActionStrategyProbability

        val opponentReachProbability: Double =
          reachProbabilities
            .zipWithIndex
            .filterNot(_._2 == nextToAct)
            .map(_._1)
            .product

        val sampledCounterfactualUtility =
          updatePlayerPayoff * opponentReachProbability / outcome.sampleProbability

        val counterfactualRegret: Seq[Double] =
          for (a <- 0 until actionCount)
          yield
            if (a == sampledActionIndex) {
              sampledCounterfactualUtility * (ctlReach - itlReach)
            } else if (missingActions.contains(a)) {
              0
            } else {
              -sampledCounterfactualUtility * itlReach
            }

        counterfactualRegret
      }

      
      //----------------------------------------------------------------------------------------------------------------
      private def sampleAction(
          strategy       : Seq[Double],
          updatingPlayer : Boolean,
          missingActions : Set[Int]
          ): ActionSample =
      {
        if (updatingPlayer) {
          sampleAction(exploratoryActionProbabilities(strategy, missingActions))
        } else {
          sampleAction(strategy)
        }
      }

      def exploratoryActionProbabilities(
          strategy       : Seq[Double],
          missingActions : Set[Int]
          ): Seq[Double] =
      {
        val minProbability: Double =
          explorationProbability * (1.0 / strategy.length)

        val strategyFactor: Double =
          1.0 - explorationProbability

        omitMissingProbabilities(
          strategy
            .map(p => minProbability + strategyFactor * p),
          missingActions)
      }

      private def sampleAction(
          actionProbabilities : Seq[Double]
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
    override def strategyView: MixedStrategy =
      mixedStrategyView
  }


  //---------------------------------------------------------------------------------------------------------------------
  private def checkProbNotZero(probability: Double): Unit =
    assert(0 < probability && probability <= 1.0)

  private def omitMissingProbabilities(
      probabilities        : Seq[Double],
      missingProbabilities : Set[Int]): Seq[Double] =
  {
    if (missingProbabilities.isEmpty) {
      probabilities
    } else {
      val denormalized: Seq[Double] =
        probabilities.zipWithIndex
          .map(si => if (missingProbabilities.contains(si._2)) 0 else si._1)

      if (denormalized.exists(_ > 0)) {
        NumUtils.normalizeToOne(denormalized)
      } else {
        val equalProbability: Double =
          1.0 / (probabilities.size - missingProbabilities.size)

        (0 until probabilities.size)
          .map(i => if (missingProbabilities.contains(i)) 0 else equalProbability)
      }
    }
  }


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
    def scaleSuffixReach(moveProb: Double): SampleOutcome =
      copy(suffixReachProbability = suffixReachProbability * moveProb)
  }
}
