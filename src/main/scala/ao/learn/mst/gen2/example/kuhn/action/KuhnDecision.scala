package ao.learn.mst.gen2.example.kuhn.action

/**
 * 05/08/13 7:24 PM
 */
sealed trait KuhnDecision
  extends KuhnGenAction
{
  def id : Int
}

object KuhnDecision {
  val values = Seq(CheckFold, CallRaise)
}

case object CheckFold extends KuhnDecision {
  val id: Int = 0
}

case object CallRaise extends KuhnDecision {
  val id: Int = 1
}
