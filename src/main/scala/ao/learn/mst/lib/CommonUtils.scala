package ao.learn.mst.lib

/**
 * Common generic utilities for use within project.
 */
object CommonUtils
{
  def displayDelimiter() : Unit =
    println(s"\n${"-" * 80}")

  def displayProbabilities(probabilities: Seq[Double]) : String = {
    probabilities
      .map((p: Double) => (p * 1000).toInt)
      .mkString("\t")
  }

}
