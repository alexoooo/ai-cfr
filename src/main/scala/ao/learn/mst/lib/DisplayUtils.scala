package ao.learn.mst.lib

import java.text.DecimalFormat

/**
 * Common display utilities for use within project.
 */
object DisplayUtils
{
  //--------------------------------------------------------------------------------------------------------------------
  def displayDelimiter() : Unit =
    println(s"\n${"-" * 80}")

  def displayProbabilities(probabilities: Seq[Double]) : String = {
    probabilities
      .map((p: Double) => (p * 1000).toInt)
      .mkString("\t")
  }


  //--------------------------------------------------------------------------------------------------------------------
  private val outcomeFormat = new DecimalFormat("0.0000")

  def formatGameValue(expectedOutcomes : Seq[Double]) : String = {
    val formattedMeanOutcomes : Seq[String] =
      expectedOutcomes.map(outcomeFormat.format)

    formattedMeanOutcomes.mkString("\t")
  }


}

