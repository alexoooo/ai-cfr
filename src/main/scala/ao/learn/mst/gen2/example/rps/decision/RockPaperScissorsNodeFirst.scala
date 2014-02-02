package ao.learn.mst.gen2.example.rps.decision

import ao.learn.mst.gen2.example.rps.act.RockPaperScissorsAction
import ao.learn.mst.gen2.player.model.DeliberatePlayer


case object RockPaperScissorsNodeFirst extends RockPaperScissorsNodeDecision {
  override def player =
    new DeliberatePlayer(0)

  def child(action: RockPaperScissorsAction) =
    new RockPaperScissorsNodeSecond(action)
}
