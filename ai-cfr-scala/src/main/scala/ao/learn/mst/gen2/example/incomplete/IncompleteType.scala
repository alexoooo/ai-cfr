package ao.learn.mst.gen2.example.incomplete

import ao.learn.mst.gen2.player.model.NamedFiniteAction


//----------------------------------------------------------------------------------------------------------------------
sealed abstract class IncompleteType(index : Int, name : String)
  extends NamedFiniteAction(index, name)

case object IncompleteTypeOne extends IncompleteType(0, "t1")
case object IncompleteTypeTwo extends IncompleteType(1, "t2")