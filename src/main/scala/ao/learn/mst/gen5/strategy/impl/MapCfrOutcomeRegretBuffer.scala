package ao.learn.mst.gen5.strategy.impl

import ao.learn.mst.gen5.strategy.{CfrOutcomeRegretBuffer, CfrRegretConsumer, CfrChanceRegretBuffer}

/**
 * Uses mutable Map representation
 */
class MapCfrOutcomeRegretBuffer
  extends CfrOutcomeRegretBuffer
{
  //--------------------------------------------------------------------------------------------------------------------
  private val counterfactualRegretBuffer = collection.mutable.Map[Int, Array[Double]]()


  //--------------------------------------------------------------------------------------------------------------------
  def bufferRegret(informationSetIndex: Int, counterfactualRegret: Seq[Double]): Unit = {
    val actionCount = counterfactualRegret.length
    initializeInformationSetIfRequired(informationSetIndex, actionCount)

    for (action <- 0 until actionCount) {
      counterfactualRegretBuffer(informationSetIndex)(action) +=
        counterfactualRegret(action)
    }
  }

  private def initializeInformationSetIfRequired(informationSetIndex: Int, actionCount: Int)
  {
    if (informationSetIndex < 0) {
      throw new IllegalArgumentException(
        "Index " + informationSetIndex + " must be non-negative.")
    }

    if (! isInformationSetInitialized(informationSetIndex)) {
      counterfactualRegretBuffer(informationSetIndex) = new Array[Double](actionCount)
    } else {
      val currentActionCount = counterfactualRegretBuffer(informationSetIndex).length
      if (currentActionCount != actionCount) {
        throw new IllegalArgumentException("Mismatched action count: " +
          informationSetIndex + " | " + currentActionCount + " vs " + actionCount)
      }
    }
  }

  private def isInformationSetInitialized(informationSetIndex:Int):Boolean =
    counterfactualRegretBuffer.contains(informationSetIndex)


  //--------------------------------------------------------------------------------------------------------------------
  def commit(cfrStrategyProfileBuilder: CfrRegretConsumer): Unit = {
    for (informationSetIndex:Int <- counterfactualRegretBuffer.keys) {
      cfrStrategyProfileBuilder.update(
        informationSetIndex,
        counterfactualRegretBuffer(informationSetIndex))
    }

    counterfactualRegretBuffer.clear()
  }
}
