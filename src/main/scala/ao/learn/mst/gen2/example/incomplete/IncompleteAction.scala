package ao.learn.mst.gen2.example.incomplete

import ao.learn.mst.gen2.player.model.FiniteAction


//----------------------------------------------------------------------------------------------------------------------
sealed abstract class IncompleteAction(index : Int)
  extends FiniteAction(index)

case object IncompleteActionUp extends IncompleteAction(0)
case object IncompleteActionDown extends IncompleteAction(1)
