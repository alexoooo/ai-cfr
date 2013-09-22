package ao.learn.mst.gen3.strategy.impl

import ao.learn.mst.gen3.strategy.{CfrStrategyConsumer, CfrStrategyBuilder, ExtensiveStrategyProfile}

/**
 * 22/09/13 4:39 PM
 */
class ArrayCfrAverageStrategyBuilder
  extends CfrStrategyConsumer
  with    CfrStrategyBuilder
{
  //--------------------------------------------------------------------------------------------------------------------
  private var actionProbabilitySums = Array[Array[Double]]()
  private var reachProbabilitySum   = Array[Double       ]()


  //--------------------------------------------------------------------------------------------------------------------
  def update(
      informationSetIndex: Int,
      currentPositiveRegretStrategy: Seq[Double],
      opponentReachProbability: Double) : Unit =
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
        opponentReachProbability * currentPositiveRegretStrategy( action )
    }

    // technically not necessary because we can weigh
    //  by sum of parent's child (i.e. sibling + self)
    reachProbabilitySum( informationSetIndex ) += opponentReachProbability
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
    reachProbabilitySum.length

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
  private def initializeInformationSetIfRequired(informationSetIndex: Int, requiredActionCount : Int)
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


  def informationSetInitialized(informationSetIndex: Int) =
    informationSetIndex < informationSetCount

  def initializeInformationSet(informationSetIndex: Int) {
    val count = informationSetIndex + 1

    actionProbabilitySums = actionProbabilitySums.padTo(count, null      )
    reachProbabilitySum   = reachProbabilitySum  .padTo(count, 0.toDouble)
  }


  private def actionsInitialized(informationSetIndex: Int) : Boolean =
    actionProbabilitySums.length > informationSetIndex &&
      actionProbabilitySums( informationSetIndex ) != null

  private def initializeActions(informationSetIndex: Int)
  {
    actionProbabilitySums(informationSetIndex) = new Array[Double](0)
  }


  def actionCountInitialized(informationSetIndex: Int, requiredActionCount : Int) =
    requiredActionCount <= actionCount(informationSetIndex)

  private def initializeActionCount(informationSetIndex: Int, requiredActionCount : Int)
  {
    actionProbabilitySums(informationSetIndex) =
      actionProbabilitySums(informationSetIndex)
        .padTo(requiredActionCount, 0.toDouble)
  }
}
