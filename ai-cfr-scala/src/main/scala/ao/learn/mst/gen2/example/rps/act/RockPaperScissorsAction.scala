package ao.learn.mst.gen2.example.rps.act

import ao.learn.mst.gen2.player.model.FiniteAction

//----------------------------------------------------------------------------------------------------------------------
sealed abstract class RockPaperScissorsAction(
      index: Int,
      val value: Double)
    extends FiniteAction(index)

case object Rock extends RockPaperScissorsAction(0, 1)
case object Paper extends RockPaperScissorsAction(1, 1)
case object Scissors extends RockPaperScissorsAction(2, 1)


