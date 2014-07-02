package ao.learn.mst.gen5.example.kuhn.action

/**
 * 05/08/13 7:24 PM
 */
sealed trait KuhnDecision
  extends KuhnGenAction
{
  def id : Int
}

object KuhnDecision {
  val values = Seq(KuhnCheckFold, KuhnCallRaise)
}

case object KuhnCheckFold extends KuhnDecision {
  val id: Int = 0
}

case object KuhnCallRaise extends KuhnDecision {
  val id: Int = 1
}
