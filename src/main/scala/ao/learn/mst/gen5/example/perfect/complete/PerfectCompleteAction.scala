package ao.learn.mst.gen5.example.perfect.complete

/**
 *
 */
sealed trait PerfectCompleteAction {
  def decision : Boolean
}

case class PlayerOneAction(decision : Boolean) extends PerfectCompleteAction
case class PlayerTwoAction(decision : Boolean) extends PerfectCompleteAction

