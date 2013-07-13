package ao.learn.mst.example.rps.decision

import ao.learn.mst.example.rps.act.RockPaperScissorsAction
import ao.learn.mst.example.rps.RockPaperScissorsTerminal
import ao.learn.mst.gen2.player.model.RationalPlayer

case class RockPaperScissorsNodeSecond(
      firstPlayerAction: RockPaperScissorsAction)
    extends RockPaperScissorsNodeDecision
{
  override def player =
    new RationalPlayer(1)

  //  def informationSet = SecondPlayerPov

  def child(action: RockPaperScissorsAction) =
    new RockPaperScissorsTerminal(firstPlayerAction, action)
}
