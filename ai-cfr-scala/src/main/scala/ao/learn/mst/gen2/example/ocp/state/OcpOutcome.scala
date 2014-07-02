package ao.learn.mst.gen2.example.ocp.state

/**
 * Date: 10/11/11
 * Time: 3:46 AM
 */

case class OcpOutcome(
    firstPlayerDelta: Int,
    lastPlayerDelta: Int)
{
  def toSeq: Seq[Double] =
    Seq(firstPlayerDelta, lastPlayerDelta)
}