package ao.learn.mst.lib

/**
 * Numeric utilities
 */
object NumUtils
{
  //--------------------------------------------------------------------------------------------------------------------
  def normalizeToOne(weights: Seq[Double]): Seq[Double] = {
    assert(weights.min >= 0)
    assert(weights.max > 0)

    val sum = weights.sum

    weights.map(_ / sum)
  }
}
