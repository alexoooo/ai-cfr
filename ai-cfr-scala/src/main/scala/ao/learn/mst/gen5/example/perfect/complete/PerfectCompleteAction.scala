package ao.learn.mst.gen5.example.perfect.complete

/**
 *
 */
sealed trait PerfectCompleteAction {
  def decision : Boolean
}

case class PerfectPlayerOneAction(decision : Boolean) extends PerfectCompleteAction
case class PerfectPlayerTwoAction(decision : Boolean) extends PerfectCompleteAction


