package ao.learn.mst.gen5.state.regret.impl

import ao.learn.mst.gen5.state.regret.RegretMatcher

/**
 *
 */
case object UniformDefaultRegretMatcher
    extends RegretMatcher
{
  override def positiveRegretStrategy(cumulativeRegret: Seq[Double], actionCount: Int): IndexedSeq[Double] = {
    val strategy: Array[Double] = {
      require(cumulativeRegret.size <= actionCount)

      val actionRegret = new Array[Double](actionCount)
      for (i <- 0 until cumulativeRegret.length) {
        actionRegret(i) = cumulativeRegret(i)
      }
      actionRegret
    }

    var positiveRegretSum: Double = 0

    for (i <- 0 until actionCount) {
      val positiveRegret = math.max(0, strategy(i))
      strategy(i) = positiveRegret
      positiveRegretSum += positiveRegret
    }

    if (positiveRegretSum == 0) {
      val uniformRegret = 1.0 / actionCount
      for (i <- 0 until actionCount) {
        strategy(i) = uniformRegret
      }
    } else {
      for (i <- 0 until actionCount) {
        strategy(i) = strategy(i) / positiveRegretSum
      }
    }

    strategy
  }
}
