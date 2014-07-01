package ao.learn.mst.gen5.cfr2

import ao.learn.mst.gen5.solve2.{RegretStrategySample, AbstractGame, RegretSampler}
import ao.learn.mst.gen5.state.regret.{RegretAccumulator, RegretBuffer, RegretStore, RegretMatcher}
import scala.util.Random
import ao.learn.mst.lib.NumUtils
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.state.regret.impl.{UniformDefaultRegretMatcher, MapRegretBuffer, ArrayRegretStore}
import ao.learn.mst.gen5.state.strategy.{StrategyBuffer, StrategyStore}
import ao.learn.mst.gen5.state.strategy.impl.{MapStrategyBuffer, ArrayStrategyStore}
import ao.learn.mst.gen5.node.StateTerminal
import ao.learn.mst.gen5.solve2.AbstractGame
import ao.learn.mst.gen5.solve2.RegretStrategySample
import scala.Some
import ao.learn.mst.gen5.node.StateChance
import scalaz._
import Scalaz._
import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}


/**
 *
 */
class OutcomeRegretSampler[State, InformationSet, Action](
    regretMatcher: RegretMatcher = UniformDefaultRegretMatcher,
    explorationProbability: Double = 0.6,
    allowSupersetAbstraction: Boolean = false,
    randomness: Random = new Random)
  extends RegretSampler[State, InformationSet, Action]
{
  //--------------------------------------------------------------------------------------------------------------------
  override def sampleRegret(
      game              : ExtensiveGame[State, InformationSet, Action],
      abstraction       : ExtensiveAbstraction[InformationSet, Action],
      regretAccumulator : RegretAccumulator,
      player            : Int)
      : Map[Long, IndexedSeq[Double]] =
  {
    val regretBuffer = new MapRegretBuffer

    walkTreeFromRoot(Context(
      game, abstraction, regretAccumulator, player,
      regretBuffer, None))

    regretBuffer.accumulated
  }


  override def sampleRegretAndStrategy(
      game              : ExtensiveGame[State, InformationSet, Action],
      abstraction       : ExtensiveAbstraction[InformationSet, Action],
      regretAccumulator : RegretAccumulator,
      player            : Int)
      : RegretStrategySample =
  {
    val regretBuffer = new MapRegretBuffer
    val strategyBuffer = new MapStrategyBuffer

    walkTreeFromRoot(Context(
      game, abstraction, regretAccumulator, player,
      regretBuffer, Some(strategyBuffer)))

    RegretStrategySample(
      regretBuffer.accumulated,
      strategyBuffer.accumulated)
  }


  private def walkTreeFromRoot(context: Context): Unit = {
    val prefixProbabilities: IndexedSeq[Double] =
      IndexedSeq.fill(context.game.playerCount)(1.0)

    walkTree(
      context,
      context.game.initialStateNode,
      prefixProbabilities,
      prefixProbabilities)
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def walkTree(
      context             : Context,
      stateNode           : ExtensiveStateNode[State, InformationSet, Action],
      reachProbabilities  : IndexedSeq[Double],
      sampleProbabilities : IndexedSeq[Double]
      ): SampleOutcome =
  {
    stateNode match {
      case StateTerminal(_, Terminal(payoffs)) =>
        SampleOutcome(
          payoffs(context.player),
          sampleProbabilities.product,
          1.0)

      case StateChance(_, Chance(outcomes)) =>
        walkTree(
          context,
          sampleChance(context, stateNode, outcomes),
          reachProbabilities,
          sampleProbabilities)

      case decision: StateDecision[State, InformationSet, Action] =>
        walkDecision(
          context,
          decision,
          reachProbabilities,
          sampleProbabilities)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  def sampleChance(
      context   : Context,
      stateNode : ExtensiveStateNode[State, InformationSet, Action],
      outcomes  : OutcomeSet[Action])
      : ExtensiveStateNode[State, InformationSet, Action] =
  {
    val sampledOutcome: Action =
      outcomes.sample(randomness)

    val sampledChance: ExtensiveStateNode[State, InformationSet, Action] =
      context.game.transitionStateNode(stateNode, sampledOutcome)

    sampledChance
  }


  //----------------------------------------------------------------------------------------------------------------
  private def walkDecision(
      context             : Context,
      decision            : StateDecision[State, InformationSet, Action],
      reachProbabilities  : IndexedSeq[Double],
      sampleProbabilities : IndexedSeq[Double])
      : SampleOutcome =
  {
    val nextToAct: Int =
      decision.node.nextToAct.index

    val nextToActIsUpdatePlayer: Boolean =
      nextToAct == context.player

    val infoSet: InformationSet =
      decision.node.informationSet

    val abstractActionCount: Int =
      context.abstraction.actionCount(infoSet)

    val infoIndex: Long =
      context.abstraction.informationSetIndex(infoSet)

    val optionalMissingActions: Option[Set[Int]] =
      allowSupersetAbstraction.option({
        val availableActions: Set[Int] =
          decision.node.choices
            .map(c => context.abstraction.actionSubIndex(infoSet, c))
            .toSet

        (0 until abstractActionCount)
          .filterNot(availableActions.contains)
          .toSet
      })


    val strategy: IndexedSeq[Double] = {
      val fullStrategy: IndexedSeq[Double] =
        regretMatcher.positiveRegretStrategy(
          context.regretAccumulator.cumulativeRegret(infoIndex),
          abstractActionCount)

      omitMissingProbabilitiesIfNeeded(
        fullStrategy, optionalMissingActions)
    }

    val sampledAction: ActionSample =
      sampleAction(strategy, nextToActIsUpdatePlayer, optionalMissingActions)

    val nextSampleProbabilities: IndexedSeq[Double] =
      sampleProbabilities
        .updated(nextToAct, sampledAction.probability * sampleProbabilities(nextToAct))

    val sampledActionStrategyProbability: Double =
      strategy(sampledAction.index)

    val sampledGameState: ExtensiveStateNode[State, InformationSet, Action] = {
      val concreteSampledAction: Action =
        translateFromAbstraction(
          context, infoSet, decision.node.choices, sampledAction.index)

      context.game.transitionStateNode(decision, concreteSampledAction)
    }

    val nextReachProbabilities: IndexedSeq[Double] =
      reachProbabilities
        .updated(nextToAct, sampledActionStrategyProbability * reachProbabilities(nextToAct))

    val outcome: SampleOutcome =
      walkTree(
        context,
        sampledGameState,
        nextReachProbabilities,
        nextSampleProbabilities)

    if (nextToActIsUpdatePlayer) {
      val regret: IndexedSeq[Double] =
        counterfactualRegret(
          nextToAct,
          abstractActionCount,
          sampledAction.index,
          sampledActionStrategyProbability,
          reachProbabilities,
          outcome,
          optionalMissingActions)

      context.regretBuffer.add(infoIndex, regret)
    }

    if (! nextToActIsUpdatePlayer) {
      context.strategyBuffer.foreach(sb => {
          val externalReachProbability: Double =
            stochasticWeight(nextToAct, reachProbabilities, sampleProbabilities)

          sb.add(infoIndex, strategy, externalReachProbability)
      })
    }

    outcome
      .scaleSuffixReach(sampledActionStrategyProbability)
  }


  //----------------------------------------------------------------------------------------------------------------
  def translateFromAbstraction(
      context: Context,
      infoSet: InformationSet,
      choices: Traversable[Action],
      abstractAction: Int)
      : Action =
  {
    def indexOf(choice: Action): Int =
      context.abstraction.actionSubIndex(infoSet, choice)

    val realActionCandidates: Traversable[Action] =
      choices.filter(indexOf(_) == abstractAction)

    realActionCandidates
      .map(a => (a, randomness.nextDouble()))
      .maxBy(_._2)._1
  }



  //----------------------------------------------------------------------------------------------------------------
  def stochasticWeight(
      nextToAct           : Int,
      reachProbabilities  : IndexedSeq[Double],
      sampleProbabilities : IndexedSeq[Double])
      : Double =
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
      reachProbabilities               : IndexedSeq[Double],
      outcome                          : SampleOutcome,
      optionalMissingActions           : Option[Set[Int]])
      : IndexedSeq[Double] =
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

    val counterfactualRegret: IndexedSeq[Double] =
      optionalMissingActions match {
        case None =>
          for (action <- 0 until actionCount)
          yield
            if (action == sampledActionIndex) {
              sampledCounterfactualUtility * (ctlReach - itlReach)
            } else {
              -sampledCounterfactualUtility * itlReach
            }

        case Some(missingActions) =>
          for (action <- 0 until actionCount)
          yield
            if (action == sampledActionIndex) {
              sampledCounterfactualUtility * (ctlReach - itlReach)
            } else if (missingActions.contains(action)) {
              0
            } else {
              -sampledCounterfactualUtility * itlReach
            }
      }

    counterfactualRegret
  }


  //----------------------------------------------------------------------------------------------------------------
  private def sampleAction(
      strategy               : IndexedSeq[Double],
      updatingPlayer         : Boolean,
      optionalMissingActions : Option[Set[Int]])
      : ActionSample =
  {
    // todo: use exploratory strategy as a separate condition?

    val sampleProbabilities =
      if (updatingPlayer) {
        omitMissingProbabilitiesIfNeeded(
          exploratoryActionProbabilities(strategy),
          optionalMissingActions)
      } else {
        strategy
      }

    sampleAction(sampleProbabilities)
  }

  def exploratoryActionProbabilities(strategy: IndexedSeq[Double]): IndexedSeq[Double] =
  {
    val minProbability: Double =
      explorationProbability * (1.0 / strategy.length)

    val strategyFactor: Double =
      1.0 - explorationProbability

    strategy
      .map(p => minProbability + strategyFactor * p)
  }

  private def sampleAction(actionProbabilities: IndexedSeq[Double]): ActionSample =
  {
    val roll: Double =
      randomness.nextDouble()

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


  //--------------------------------------------------------------------------------------------------------------------
  private def checkProbNotZero(probability: Double): Unit =
    assert(0 < probability && probability <= 1.0)

  private def omitMissingProbabilitiesIfNeeded(
      probabilities                : IndexedSeq[Double],
      optionalMissingProbabilities : Option[Set[Int]])
      : IndexedSeq[Double] =
  {
    optionalMissingProbabilities match {
      case None => probabilities
      case Some(missingProbabilities) =>
        omitMissingProbabilities(probabilities, missingProbabilities)
    }
  }

  private def omitMissingProbabilities(
      probabilities        : IndexedSeq[Double],
      missingProbabilities : Set[Int])
      : IndexedSeq[Double] =
  {
    if (missingProbabilities.isEmpty) {
      probabilities
    } else {
      val denormalized: Seq[Double] =
        probabilities.zipWithIndex
          .map(si => if (missingProbabilities.contains(si._2)) 0 else si._1)

      if (denormalized.exists(_ > 0)) {
        NumUtils.normalizeToOne(denormalized).toIndexedSeq
      } else {
        val equalProbability: Double =
          1.0 / (probabilities.size - missingProbabilities.size)

        (0 until probabilities.size)
          .map(i => if (missingProbabilities.contains(i)) 0 else equalProbability)
      }
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private case class Context(
    game              : ExtensiveGame[State, InformationSet, Action],
    abstraction       : ExtensiveAbstraction[InformationSet, Action],
    regretAccumulator : RegretAccumulator,
    player            : Int,
    regretBuffer      : RegretBuffer,
    strategyBuffer    : Option[StrategyBuffer])


  private case class ActionSample(
      index: Int, probability: Double)
  {
    checkProbNotZero(probability)
  }

  private case class SampleOutcome(
      payoff                 : Double,
      sampleProbability      : Double,
      suffixReachProbability : Double)
  {
    def scaleSuffixReach(moveProb: Double): SampleOutcome =
      copy(suffixReachProbability = suffixReachProbability * moveProb)
  }
}
