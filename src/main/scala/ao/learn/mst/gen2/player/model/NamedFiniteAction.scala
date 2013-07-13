package ao.learn.mst.gen2.player.model

import collection.immutable.SortedSet

/**
 *
 */

class NamedFiniteAction(
    index : Int,
    name  : String)
      extends IndexedFiniteAction(index)
{
  override def toString = name
}


object NamedFiniteAction
{
  def sequence(names : String*) = SortedSet[FiniteAction]() ++
    (for ((name, i) <- names.zipWithIndex)
      yield new NamedFiniteAction(i, name))
}