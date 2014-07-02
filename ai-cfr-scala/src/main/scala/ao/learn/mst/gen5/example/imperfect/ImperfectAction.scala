package ao.learn.mst.gen5.example.imperfect


sealed trait ImperfectAction {
  def value : Boolean
}


case class ImperfectPlayerOneAction(value : Boolean) extends ImperfectAction
case class ImperfectPlayerTwoAction(value : Boolean) extends ImperfectAction



