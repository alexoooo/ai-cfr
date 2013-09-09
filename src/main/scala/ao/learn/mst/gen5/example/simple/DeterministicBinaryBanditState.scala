package ao.learn.mst.gen5.example.simple


//----------------------------------------------------------------------------------------------------------------------
sealed trait DeterministicBinaryBanditState


case object DecisionState extends DeterministicBinaryBanditState
case class TerminalState(action: Boolean) extends DeterministicBinaryBanditState

