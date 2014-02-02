package ao.learn.mst.gen2.example.kuhn.state.terminal


/**
 * Date: 10/11/11
 * Time: 4:23 AM
 */
trait NamedWinnerIndicator[T] {
  def preShowdownWinner : Option[T]
  def name              : String
}