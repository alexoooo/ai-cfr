package ao.learn.mst.gen5.strategy.impl

import ao.learn.mst.gen5.strategy.{CfrRegretConsumer, CfrStrategyBuilder, CfrChanceRegretBuffer, CfrStrategyProfile}
import java.util
import scala.collection.immutable.TreeSet
import scala.collection.SortedSet

/**
 * Uses mutable Map representation
 */
class MapCfrChanceRegretBuffer
  extends CfrChanceRegretBuffer
{
  //--------------------------------------------------------------------------------------------------------------------
//  private val reachProbabilityBuffer     = collection.mutable.Map[Int, Double]()
  private val counterfactualRegretBuffer = collection.mutable.Map[Int, Array[Double]]()


  //--------------------------------------------------------------------------------------------------------------------
  def bufferRegret(informationSetIndex: Int, actionRegret: Seq[Double], reachProbability: Double): Unit = {
    val actionCount = actionRegret.length
    initializeInformationSetIfRequired(informationSetIndex, actionCount)

    //    val newReachProbability = reachProbabilityBuffer(informationSetIndex) + reachProbability
    //    reachProbabilityBuffer.update(informationSetIndex, newReachProbability)
//    reachProbabilityBuffer(informationSetIndex) += reachProbability // same as above?

    for (action <- 0 until actionCount) {
      counterfactualRegretBuffer(informationSetIndex)(action) +=
        actionRegret(action) * reachProbability
    }
  }

  private def initializeInformationSetIfRequired(informationSetIndex: Int, actionCount: Int): Unit =
  {
    if (informationSetIndex < 0) {
      throw new IllegalArgumentException(
        "Index " + informationSetIndex + " must be non-negative.")
    }

    if (! isInformationSetInitialized(informationSetIndex)) {
//      reachProbabilityBuffer(informationSetIndex) = 0.0
      counterfactualRegretBuffer(informationSetIndex) = new Array[Double](actionCount)
    } else {
      val currentActionCount = counterfactualRegretBuffer(informationSetIndex).length
      if (currentActionCount != actionCount) {
        throw new IllegalArgumentException("Mismatched action count: " +
          informationSetIndex + " | " + currentActionCount + " vs " + actionCount)
      }
    }
  }

  private def isInformationSetInitialized(informationSetIndex: Int): Boolean =
    counterfactualRegretBuffer.contains(informationSetIndex)


  //--------------------------------------------------------------------------------------------------------------------
  def commit(cfrStrategyProfileBuilder: CfrRegretConsumer): Unit = {
    for (informationSetIndex:Int <- counterfactualRegretBuffer.keys) {
      cfrStrategyProfileBuilder.update(
        informationSetIndex,
        counterfactualRegretBuffer(informationSetIndex))
    }

    counterfactualRegretBuffer.clear()
//    reachProbabilityBuffer.clear()
  }


  //--------------------------------------------------------------------------------------------------------------------
  override def toString: String = {
    val keysInOrder: Traversable[Int] =
      counterfactualRegretBuffer.keys.to[SortedSet]

    keysInOrder.map(i =>
      i + " - " + util.Arrays.toString(counterfactualRegretBuffer(i))
    ).mkString(" | ")
  }
}
