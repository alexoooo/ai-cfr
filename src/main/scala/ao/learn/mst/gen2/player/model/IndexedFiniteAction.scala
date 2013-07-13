package ao.learn.mst.gen2.player.model

import collection.immutable.SortedSet

/**
 * User: ao
 * Date: 01/05/12
 * Time: 11:10 PM
 */
class IndexedFiniteAction(index : Int) extends FiniteAction(index)
{
  override def toString =
    "[" + index + "]"
}

object IndexedFiniteAction
{
  def sequence(numberOfActions: Int) = SortedSet[FiniteAction]() ++
      (for (i <- 0 until numberOfActions)
       yield new IndexedFiniteAction(i))
}