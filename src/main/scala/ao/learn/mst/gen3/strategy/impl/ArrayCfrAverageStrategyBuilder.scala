package ao.learn.mst.gen3.strategy.impl

import ao.learn.mst.gen3.strategy.{CfrAverageStrategyBuilder, ExtensiveStrategyProfile}

/**
 *
 */
class ArrayCfrAverageStrategyBuilder
    extends CfrAverageStrategyBuilder
{
  //--------------------------------------------------------------------------------------------------------------------
  private var actionProbabilitySums = Array[Array[Double]]()


  //--------------------------------------------------------------------------------------------------------------------
  def update(
      informationSetIndex           : Int,
      currentPositiveRegretStrategy : Seq[Double],
      externalReachProbability      : Double) : Unit =
  {
    val actionCount = currentPositiveRegretStrategy.length
    initializeInformationSetIfRequired(informationSetIndex, actionCount)

    // See train.cpp (average the strategy for the player):
    // 651: for(int i=0; i<3; ++i) {
    // 652:
    // 653:   average_probability[i] += reach[player]*probability[i];
    // 654: }
    for (action <- 0 until actionCount)
    {
      actionProbabilitySums( informationSetIndex )( action ) +=
        externalReachProbability * currentPositiveRegretStrategy( action )
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  def toExtensiveStrategyProfile: ExtensiveStrategyProfile = {
    val averageStrategies : Seq[Seq[Double]] =
      (0 until informationSetCount)
            .map(averageStrategy)

    SeqExtensiveStrategyProfile(averageStrategies)
  }



  //--------------------------------------------------------------------------------------------------------------------
  private def informationSetCount: Int =
    actionProbabilitySums.length

  private def actionCount(informationSetIndex: Int) : Int = {
    val actions : Array[Double] =
      actionProbabilitySums(informationSetIndex)

    if (actions == null) {
      0
    } else {
      actions.length
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  // Corresponds to train.cpp get_normalized_average_probability line 489
  private def averageStrategy(
    informationSetIndex : Int
    ): Seq[Double] =
  {
    if (! actionsInitialized(informationSetIndex)) {
      return Seq.empty
    }

    // In train.cpp, see the following relevant (lines 504 .. 509):
    //  /* compute sum */
    //  double sum = 0;
    //  for(int i=0; i<leduc::NUM_ACTIONS; ++i) {
    //    sum += average_probability[u.get_id()][bucket][i];
    //  }
    //
    // Where average_probability is the equivalent of actionProbabilitySums.
    //
    // This sum is used to normalize relative to siblings (train.cpp line 516):
    //  probability[i] = average_probability[u.get_id()][bucket][i]/sum;

    val intoStrategyTotal: Double =
      actionProbabilitySums(informationSetIndex).sum

    val averageStrategy : Seq[Double] =
      actionProbabilitySums(informationSetIndex)
        .map(_ / intoStrategyTotal)

    averageStrategy
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def initializeInformationSetIfRequired(informationSetIndex: Int, requiredActionCount : Int): Unit =
  {
    if (! informationSetInitialized(informationSetIndex)) {
      initializeInformationSet(informationSetIndex)
    }

    if (! actionsInitialized(informationSetIndex)) {
      initializeActions(informationSetIndex)
    }

    if (! actionCountInitialized(informationSetIndex, requiredActionCount)) {
      initializeActionCount(informationSetIndex, requiredActionCount)
    }
  }


  private def informationSetInitialized(informationSetIndex: Int) =
    informationSetIndex < informationSetCount

  private def initializeInformationSet(informationSetIndex: Int): Unit = {
    val count = informationSetIndex + 1

    actionProbabilitySums = actionProbabilitySums.padTo(count, null)
  }


  private def actionsInitialized(informationSetIndex: Int): Boolean =
    actionProbabilitySums.length > informationSetIndex &&
      actionProbabilitySums( informationSetIndex ) != null

  private def initializeActions(informationSetIndex: Int): Unit = {
    actionProbabilitySums(informationSetIndex) = new Array[Double](0)
  }


  private def actionCountInitialized(informationSetIndex: Int, requiredActionCount : Int) =
    requiredActionCount <= actionCount(informationSetIndex)

  private def initializeActionCount(informationSetIndex: Int, requiredActionCount : Int): Unit = {
    actionProbabilitySums(informationSetIndex) =
      actionProbabilitySums(informationSetIndex)
        .padTo(requiredActionCount, 0.toDouble)
  }
}
