package ao.learn.mst.gen2.player.model

/**
 * Date: 22/11/11
 * Time: 5:20 AM
 */

class FiniteAction(val index : Int) extends Ordered[FiniteAction] {
  def compare(that: FiniteAction) =
    this.index compare that.index
}