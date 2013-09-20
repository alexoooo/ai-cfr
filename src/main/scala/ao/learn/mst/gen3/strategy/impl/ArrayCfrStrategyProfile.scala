package ao.learn.mst.gen3.strategy.impl

import ao.learn.mst.gen3.strategy.{ExtensiveStrategyProfile, CfrStrategyProfile}
import ao.learn.mst.gen.chance.ProbabilityMass
import scala.Array
import java.util


class ArrayCfrStrategyProfile
  extends CfrStrategyProfile
{
  //--------------------------------------------------------------------------------------------------------------------
  private val epsilon: Double = 1e-7


  //--------------------------------------------------------------------------------------------------------------------
  private var visitCount            = Array[Long         ]()
  private var regretSums            = Array[Array[Double]]()
  private var actionProbabilitySums = Array[Array[Double]]()
  private var reachProbabilitySum   = Array[Double       ]()



  //--------------------------------------------------------------------------------------------------------------------
  def update(
    informationSetIndex : Int,
    actionRegret        : Seq[Double],
    reachProbability    : Double)
  {
    val actionCount = actionRegret.length
    initializeInformationSetIfRequired(informationSetIndex, actionCount)

    val currentPositiveRegretStrategy : Seq[Double] =
      positiveRegretStrategy(informationSetIndex, actionCount)

    // See train.cpp (average the strategy for the player):
    // 651: for(int i=0; i<3; ++i) {
    // 652:
    // 653:   average_probability[i] += reach[player]*probability[i];
    // 654: }
    for (action <- 0 until actionCount)
    {
      actionProbabilitySums( informationSetIndex )( action ) +=
        reachProbability * currentPositiveRegretStrategy( action )
    }

    // technically not necessary because we can weigh
    //  by sum of parent's child (i.e. sibling + self)
    reachProbabilitySum( informationSetIndex ) += reachProbability

    val counterfactualRegret =
      actionRegret.map(_ * reachProbability)

    for (action <- 0 until actionCount) {
      // Corresponds to line 682 in train.cpp,
      //  over there the addend is called "delta_regret".
      regretSums( informationSetIndex )( action ) +=
        counterfactualRegret( action )

      // note that the explanation on:
      //  http://pokerai.org/pf3/viewtopic.php?f=3&t=2662
      // suggests that it should be:
      //   math.max(0, actionRegret( action ))
      // which appears to be incorrect.
    }

    visitCount( informationSetIndex ) += 1
  }



  //--------------------------------------------------------------------------------------------------------------------
  def positiveRegretStrategy(
      informationSetIndex: Int
      ): Seq[Double] =
  {
    if (! informationSetInitialized(informationSetIndex)) {
      return Seq.empty
    }

    // Corresponds to train.cpp:
    // 465: /* compute sum of positive regret */
    // 466: double sum = 0;
    // 467: for(int i=0; i<leduc::NUM_ACTIONS; ++i) {
    // 469:   sum += max(0., regret[u.get_id()][bucket][i]);
    // 470: }
    val positiveCounterfactualRegret: Seq[Double] =
      regretSums(informationSetIndex).map(math.max(0, _))

    val positiveRegretSum =
      positiveCounterfactualRegret.sum

    // compute probability as the proportion of positive regret
    val positiveRegretStrategy : Seq[Double] = {
      if (positiveRegretSum > epsilon)
        positiveCounterfactualRegret.map(_ / positiveRegretSum)
      else
        Seq.empty
    }


    positiveRegretStrategy
  }

  // verified against Leduc CFR train.get_probability (line 450)
  def positiveRegretStrategy(
    informationSetIndex: Int,
    actionCount: Int
    ): Seq[Double] =
  {
    initializeInformationSetIfRequired(informationSetIndex, actionCount)

    val strategy : Seq[Double] =
      positiveRegretStrategy(informationSetIndex)

    if (strategy.isEmpty) {
      Seq.fill(actionCount)(1.0 / actionCount)
    } else if (strategy.length == actionCount) {
      strategy
    } else {
      strategy.padTo(actionCount, 0.0)
    }
  }



  //--------------------------------------------------------------------------------------------------------------------
  def toExtensiveStrategyProfile: ExtensiveStrategyProfile = {
    val averageStrategies : Seq[Seq[Double]] =
      (0 until informationSetCount)
        .map(averageStrategy)

    SeqExtensiveStrategyProfile(averageStrategies)
  }

  // Corresponds to train.cpp get_normalized_average_probability line 489
  private def averageStrategy(
    informationSetIndex : Int
    ): Seq[Double] =
  {
    // In train.cpp, see the following relevant (lines 504 .. 509):
    //  /* compute sum */
    //  double sum = 0;
    //  for(int i=0; i<leduc::NUM_ACTIONS; ++i) {
    //    sum += average_probability[u.get_id()][bucket][i];
    //  }
    //
    // Where average_probability is the equivalent of actionProbabilitySums.
    //
    // Then this sum is used to normalize relative to siblings (train.cpp line 516):
    //  probability[i] = average_probability[u.get_id()][bucket][i]/sum;
    //
    // Here we are dividing by sum of reach probabilities for the information set.
    // Note: should it be normalized in relation to siblings instead? (would that make a difference?)

    val averageStrategy : Seq[Double] =
      actionProbabilitySums(informationSetIndex)
        .map(_ / reachProbabilitySum(informationSetIndex))
//        .map(_ / visitCount(informationSetIndex))

    averageStrategy
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def informationSetCount: Int =
    visitCount.length

  private def actionCount(informationSetIndex: Int) : Int = {
    val actions : Array[Double] =
      regretSums(informationSetIndex)

    if (actions == null) {
      0
    } else {
      actions.length
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def initializeInformationSetIfRequired(informationSetIndex: Int, requiredActionCount : Int)
  {
    if (! informationSetInitialized(informationSetIndex)) {
      initializeInformationSet(informationSetIndex)
    }

    if (! actionsInitialized(informationSetIndex)) {
      initializeActions(informationSetIndex)
    }

    if (actionCountInitialized(informationSetIndex, requiredActionCount)) {
      initializeActionCount(informationSetIndex, requiredActionCount)
    }
  }


  def informationSetInitialized(informationSetIndex: Int) =
    informationSetIndex < informationSetCount

  def initializeInformationSet(informationSetIndex: Int) {
    val count = informationSetIndex + 1

    visitCount            = visitCount           .padTo(count, 0.toLong  )
    regretSums            = regretSums           .padTo(count, null      )
    actionProbabilitySums = actionProbabilitySums.padTo(count, null      )
    reachProbabilitySum   = reachProbabilitySum  .padTo(count, 0.toDouble)
  }


  private def actionsInitialized(informationSetIndex: Int) : Boolean =
    regretSums.length > informationSetIndex &&
      regretSums( informationSetIndex ) != null

  private def initializeActions(informationSetIndex: Int)
  {
    regretSums           (informationSetIndex) = new Array[Double](0)
    actionProbabilitySums(informationSetIndex) = new Array[Double](0)
  }


  def actionCountInitialized(informationSetIndex: Int, requiredActionCount : Int) =
    actionCount(informationSetIndex) < requiredActionCount

  private def initializeActionCount(informationSetIndex: Int, requiredActionCount : Int)
  {
    regretSums(informationSetIndex) =
      regretSums(informationSetIndex)
        .padTo(requiredActionCount, 0.toDouble)

    actionProbabilitySums(informationSetIndex) =
      actionProbabilitySums(informationSetIndex)
        .padTo(requiredActionCount, 0.toDouble)
  }


  //--------------------------------------------------------------------------------------------------------------------
  override def toString : String = {
    "\n\n" +
      util.Arrays.toString(visitCount) + "\n" +
      util.Arrays.toString(reachProbabilitySum)
  }
}
