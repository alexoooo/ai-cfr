package ao.learn.mst.example.kuhn.state

/**
 * Date: 10/11/11
 * Time: 3:46 AM
 */

case class KuhnOutcome(
    firstPlayerDelta : Int,
    lastPlayerDelta  : Int)
{
  def toSeq: Seq[Double] =
    Seq(firstPlayerDelta, lastPlayerDelta)
}