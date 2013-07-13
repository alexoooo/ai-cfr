package ao.learn.mst.example.rps.decision

import ao.learn.mst.example.rps.act.RockPaperScissorsAction
import ao.learn.mst.gen2.player.model.RationalPlayer


case object RockPaperScissorsNodeFirst extends RockPaperScissorsNodeDecision {
  override def player =
    new RationalPlayer(0)

  def child(action: RockPaperScissorsAction) =
    new RockPaperScissorsNodeSecond(action)
}
