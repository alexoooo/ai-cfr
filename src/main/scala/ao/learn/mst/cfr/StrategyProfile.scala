package ao.learn.mst.cfr

import ao.learn.mst.gen2.info.{InformationSet, InformationSetIndex}
import scala.{Int, Double}
import ao.learn.mst.gen2.player.model.FiniteAction
import scala.Predef._
import scala.collection.immutable.SortedSet


/**
 * Date: 07/04/12
 * Time: 10:36 PM
 */
class StrategyProfile(
    private val informationSetIndex : InformationSetIndex)
{
  //--------------------------------------------------------------------------------------------------------------------
  private val epsilon: Double = 1e-7


  //--------------------------------------------------------------------------------------------------------------------
  private val visitCount            = new Array[Long         ]( informationSetIndex.informationSetCount )
  private val regretSums            = new Array[Array[Double]]( informationSetIndex.informationSetCount )
  private val actionProbabilitySums = new Array[Array[Double]]( informationSetIndex.informationSetCount )
  private val reachProbabilitySum   = new Array[Double       ]( informationSetIndex.informationSetCount )

  private val reachProbabilityBuffer     = new Array[Double       ]( informationSetIndex.informationSetCount )
  private val counterfactualRegretBuffer = new Array[Array[Double]]( informationSetIndex.informationSetCount )
  
  
  //--------------------------------------------------------------------------------------------------------------------
//  var visitCount            = 0
//  val regretSums            = new Array[Double]( kids.length )
//  val actionProbabilitySums = new Array[Double]( kids.length )
//  var reachProbabilitySum   = 0.0


  def visitCount(informationSet: InformationSet): Long =
    visitCount(informationSetIndex.indexOf( informationSet ))

  def reachProbabilitySum(informationSet: InformationSet): Double =
    reachProbabilitySum(informationSetIndex.indexOf( informationSet ))

  private def getRegretSums(informationSet: InformationSet): Seq[Double] =
    regretSums(informationSetIndex.indexOf( informationSet ))

  private def getActionProbabilitySums(informationSet: InformationSet): Seq[Double] =
    actionProbabilitySums(informationSetIndex.indexOf( informationSet ))

  //private def applyIndex()



  //--------------------------------------------------------------------------------------------------------------------
  private def isInformationSetInitialized(informationSet: InformationSet) : Boolean =
    isInformationSetInitialized( informationSetIndex.indexOf(informationSet) )

  private def isInformationSetInitialized(informationSet: Int) : Boolean =
    regretSums( informationSet ) != null


  private def childCount(informationSet: InformationSet): Int =
    childCount( informationSetIndex.indexOf(informationSet) )

  private def childCount(informationSet: Int): Int =
    regretSums( informationSet ).length


  private def initializeInformationSetIfRequired(informationSet: Int, childCount: Int)
  {
    if (! isInformationSetInitialized( informationSet )) {
      regretSums( informationSet ) = new Array[Double](childCount)
      actionProbabilitySums( informationSet ) = new Array[Double](childCount)

      counterfactualRegretBuffer( informationSet ) = new Array[Double](childCount)
    }
  }
  
  
  //--------------------------------------------------------------------------------------------------------------------
  def positiveRegretStrategy(informationSet: InformationSet, childCount: Int) : Seq[Double] =
    positiveRegretStrategy( informationSetIndex.indexOf(informationSet), childCount )

  // verified against Leduc CFR train.get_probability (line 450)
  private def positiveRegretStrategy(informationSet: Int, childCount: Int) : Seq[Double] =
  {
    initializeInformationSetIfRequired(informationSet, childCount)

    // Corresponds to train.cpp:
    // 465: /* compute sum of positive regret */
    // 466: double sum = 0;
    // 467: for(int i=0; i<leduc::NUM_ACTIONS; ++i) {
    // 468:
    // 469:   sum += max(0., regret[u.get_id()][bucket][i]);
    // 470: }
    val positiveCounterfactualRegret: Seq[Double] =
      regretSums(informationSet).map(math.max(0, _))

    val positiveRegretSum =
      positiveCounterfactualRegret.sum

    // compute probability as the proportion of positive regret
    if (positiveRegretSum > epsilon)
    {
      positiveCounterfactualRegret.map(_ / positiveRegretSum)
    }
    else
    {
      Seq.fill(childCount)(1.0 / childCount)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  def bufferUpdate(
      informationSet   : InformationSet,
      actionRegret     : Seq[Double],
      reachProbability : Double)
  {
    bufferUpdate(
      informationSetIndex.indexOf(informationSet),
      actionRegret,
      reachProbability)
  }

  private def bufferUpdate(
      informationSet   : Int,
      actionRegret     : Seq[Double],
      reachProbability : Double)
  {
    val childCount = actionRegret.length
    initializeInformationSetIfRequired(informationSet, childCount)

    reachProbabilityBuffer(informationSet) += reachProbability

    for (action <- 0 until childCount) {
      counterfactualRegretBuffer(informationSet)(action) +=
        actionRegret(action) * reachProbability
    }

    visitCount( informationSet ) += 1
  }


  def commitBuffers() {
    for (informationSet <- 0 until regretSums.length) {
      commitBuffer(informationSet)
    }
  }
  private def commitBuffer(informationSet: Int) {
    val regretSum = regretSums(informationSet)
    if (regretSum == null) {
      return
    }

    val childCount = regretSum.length

    val currentPositiveRegretStrategy =
      positiveRegretStrategy(informationSet, childCount)

    val reachProbability = reachProbabilityBuffer( informationSet )

    // See train.cpp (average the strategy for the player):
    // 651: for(int i=0; i<3; ++i) {
    // 652:
    // 653:   average_probability[i] += reach[player]*probability[i];
    // 654: }
    for (action <- 0 until childCount)
    {
      actionProbabilitySums( informationSet )( action ) +=
        reachProbability * currentPositiveRegretStrategy( action )
    }

    // technically not necessary because we can weigh
    //  by sum of parent's child (i.e. sibling + self)
    reachProbabilitySum( informationSet ) += reachProbability

    for (action <- 0 until childCount) {
      // Corresponds to line 682 in train.cpp,
      //  over there the addend is called "delta_regret".
      regretSums( informationSet )( action ) +=
        counterfactualRegretBuffer( informationSet )( action )

      // note that the explanation on:
      //  http://pokerai.org/pf3/viewtopic.php?f=3&t=2662
      // suggests that it should be:
      //   math.max(0, actionRegret( action ))
      // which appears to be incorrect.
    }

    // clear buffers
    reachProbabilityBuffer(informationSet) = 0
    for (action <- 0 until childCount) {
      counterfactualRegretBuffer( informationSet )( action ) = 0
    }
  }



  //--------------------------------------------------------------------------------------------------------------------
  def update(
      informationSet   : InformationSet,
      actionRegret     : Seq[Double],
      reachProbability : Double)
  {
    update(informationSetIndex.indexOf(informationSet),
           actionRegret,
           reachProbability)
  }
  
  private def update(
      informationSet   : Int,
      actionRegret     : Seq[Double],
      reachProbability : Double)
  {
    val childCount = actionRegret.length

    val currentPositiveRegretStrategy =
      positiveRegretStrategy(informationSet, childCount)

    // See train.cpp (average the strategy for the player):
    // 651: for(int i=0; i<3; ++i) {
    // 652:
    // 653:   average_probability[i] += reach[player]*probability[i];
    // 654: }
    for (action <- 0 until childCount)
    {
      actionProbabilitySums( informationSet )( action ) +=
        reachProbability * currentPositiveRegretStrategy( action )
    }

    // technically not necessary because we can weigh
    //  by sum of parent's child (i.e. sibling + self)
    reachProbabilitySum( informationSet ) += reachProbability

    val counterfactualRegret =
      actionRegret.map(_ * reachProbability)

    for (action <- 0 until childCount) {
      // Corresponds to line 682 in train.cpp,
      //  over there the addend is called "delta_regret".
      regretSums( informationSet )( action ) +=
        counterfactualRegret( action )

      // note that the explanation on:
      //  http://pokerai.org/pf3/viewtopic.php?f=3&t=2662
      // suggests that it should be:
      //   math.max(0, actionRegret( action ))
      // which appears to be incorrect.
    }

    visitCount( informationSet ) += 1
  }


  //--------------------------------------------------------------------------------------------------------------------
  def averageStrategy(informationSet : InformationSet): Map[FiniteAction, Double] = {
    val actions:Set[FiniteAction] =
      informationSetIndex.actionsOf(informationSet)

    val actionsOrderedByIndex:Seq[FiniteAction] =
      (SortedSet[FiniteAction]() ++ actions).toSeq

    val actionWithGreatestIndex:FiniteAction =
      actionsOrderedByIndex.last

    val averageStrategyPerAction:Seq[Double] =
      averageStrategy(informationSet, actionWithGreatestIndex.index + 1)

    val nonZeroAverageStrategiesPerAction:Seq[(Double, Int)] =
      averageStrategyPerAction.zipWithIndex.filter(_._1 != 0)

    val actionIndexToAverageStrategy:Seq[(Int, Double)] =
      nonZeroAverageStrategiesPerAction.map(_.swap)

    val actionIndexToAction:Map[Int, FiniteAction] =
      Map() ++ actions.map(
        (action: FiniteAction) =>
          (action.index, action))

    val actionToAverageStrategy:Map[FiniteAction, Double] =
      Map() ++ actionIndexToAverageStrategy.map(
        (e: (Int, Double)) =>
          actionIndexToAction(e._1) -> e._2)

    actionToAverageStrategy
  }

  def averageStrategy(informationSet : InformationSet, childCount: Int): Seq[Double] =
    averageStrategy(
      informationSetIndex.indexOf(informationSet),
      childCount)

  // Corresponds to train.cpp get_normalized_average_probability line 489
  private def averageStrategy(informationSet : Int, childCount: Int): Seq[Double] = {
    initializeInformationSetIfRequired(informationSet, childCount)

    // In train.cpp, see the following relevant (lines 504 .. 509):
    //  /* compute sum */
    //  double sum = 0;
    //  for(int i=0; i<leduc::NUM_ACTIONS; ++i) {
    //
    //    sum += average_probability[u.get_id()][bucket][i];
    //  }
    //
    // Where average_probability is the equivalent of actionProbabilitySums.
    //
    // Then this sum is used to normalize relative to siblings (train.cpp line 516):
    //  probability[i] = average_probability[u.get_id()][bucket][i]/sum;
    //
    // Here we are dividing by sum of reach probabilities for the information set.
    // note: should it be normalized in relation to siblings instead? (would that make a difference?)

    actionProbabilitySums(informationSet)
      .map(_ / reachProbabilitySum(informationSet))
//      .map(_ / visitCount(informationSet))
  }


  //--------------------------------------------------------------------------------------------------------------------
  override def toString: String =
  {
    var buffer = ""
    for (informationSet <- informationSetIndex.informationSets)
    {
      buffer += informationSet
      
      if (isInformationSetInitialized( informationSet ))
      {
        val informationSetStrategy =
          averageStrategy(
            informationSet,
            childCount(informationSet))

        val strategyDescription =
          (for (action <- informationSetIndex.actionsOf(informationSet).toList)
            yield {
              val probabilityPercentage =
                100 * informationSetStrategy( action.index )

              action + " %" + probabilityPercentage.formatted("%.3f")
            }
          ).mkString(", ")

        val informationSetRegretSums = getRegretSums( informationSet )
        val positiveRegretProbabilities : Seq[Double] =
          positiveRegretStrategy(informationSet, childCount(informationSet))
        val informationSetActionProbabilitySums = getActionProbabilitySums( informationSet )

        buffer +=
          ":\t" + strategyDescription +
          " | regret sums = " + informationSetRegretSums.mkString("/") +
          " | pos reg probs = " + positiveRegretProbabilities.mkString("/") +
          " | act pob sums = " + informationSetActionProbabilitySums.mkString("/")
      }
      else
      {
        buffer += ":\tNot calculated"
      }

      buffer += " | visits = " + visitCount( informationSet ) +
                " / reach sum = " + reachProbabilitySum( informationSet )

      buffer += "\n"
    }

    buffer.trim
  }
}
