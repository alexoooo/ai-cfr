package ao.learn.mst.gen5.br

/**
 * Assumption: perfect recall.
 */
trait BestResponseInfoTree[State, InformationSet, Action]
{
  def root(player: Int): InformationSet

  def actionTransitions(informationSet : InformationSet, player : Int) : Map[Action, InformationSet]
}
