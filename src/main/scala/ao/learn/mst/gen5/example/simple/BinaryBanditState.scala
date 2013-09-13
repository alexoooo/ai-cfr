package ao.learn.mst.gen5.example.simple

//----------------------------------------------------------------------------------------------------------------------
sealed trait BinaryBanditState


case object BinaryDecisionState extends BinaryBanditState
case class BinaryTerminalState(action: Boolean) extends BinaryBanditState

