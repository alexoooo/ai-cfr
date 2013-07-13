package ao.learn.mst.example.kuhn.state.terminal

import ao.learn.mst.example.kuhn.state.KuhnPosition._
import ao.learn.mst.example.kuhn.state.KuhnPosition


//----------------------------------------------------------------------------------------------------------------------
object KuhnTerminalStatus
    extends EnumWithWinner[KuhnPosition]
{
  //--------------------------------------------------------------------------------------------------------------------
  case class EnumVal private[KuhnTerminalStatus](
    name: String, preShowdownWinner : Option[KuhnPosition])
    extends Value with NamedWinnerIndicator[KuhnPosition]
  type KuhnTerminalStatus = EnumVal

  val SmallShowdown = EnumVal("SmallShowdown", None)
  val BigShowdown   = EnumVal("BigShowdown"  , None)

  val FirstToActWins = EnumVal("FirstToAct Wins", Some(KuhnPosition.FirstToAct))
  val LastToActWins  = EnumVal("LastToAct Wins" , Some(KuhnPosition.LastToAct ))


//  type KuhnTerminalStatus = Value
//
//  val SmallShowdown = Value("SmallShowdown")
//  val BigShowdown   = Value("BigShowdown")
//
//  val FirstToActWins = Value("FirstToAct Wins")
//  val LastToActWins  = Value("LastToAct Wins")
}