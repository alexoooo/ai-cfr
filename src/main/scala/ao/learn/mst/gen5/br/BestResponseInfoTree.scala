package ao.learn.mst.gen5.br

/**
 * Assumption: perfect recall.
 */
trait BestResponseInfoTree[State, InformationSet, Action]
{
  def root(player: Int): InformationSet

  def nonTerminalInformationSets(player : Int) : Set[InformationSet]

  def actionTransitions(informationSet : InformationSet, player : Int) : Map[Action, InformationSet]
  

//  def states(
//    informationSet : InformationSet,
//    player         : Int)
//    : Traversable[State]


//  def actions(
//    informationSet : InformationSet,
//    player         : Int)
//    : Traversable[Action]

  
//  def successors(
//    informationSet : InformationSet,
//    player         : Int)
//    : Traversable[InformationSet]


//  def leafs(player: Int): Traversable[InformationSet]
//
//
//  def reachProbabilities(state: State): Seq[Double]
}
