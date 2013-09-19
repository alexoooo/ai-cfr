package ao.learn.mst.gen5.example.abstraction

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen5.node.{Decision, ExtensiveNode}


//----------------------------------------------------------------------------------------------------------------------
object AbstractionUtils
{
  //--------------------------------------------------------------------------------------------------------------------
  def decisions[State, InformationSet, Action](
    game: ExtensiveGame[State, InformationSet, Action])
    : Set[Decision[InformationSet, Action]] =
  {
    val nodes : Traversable[ExtensiveNode[InformationSet, Action]] =
      GameTraversal.traverseTreeBreadthFirst(game)

    def nodeInformationSet(
        node : ExtensiveNode[InformationSet, Action])
        : Option[Decision[InformationSet, Action]] =
      node match {
        case d : Decision[InformationSet, Action] =>
          Some(d)
        case _ =>
          None
      }

    val informationSets : Traversable[Decision[InformationSet, Action]] =
      nodes.flatMap(nodeInformationSet)

    informationSets.toSet
  }

  def informationSets[State, InformationSet, Action](
    game: ExtensiveGame[State, InformationSet, Action]): Set[InformationSet] =
  {
    decisions(game)
      .map(_.informationSet)
  }


  //--------------------------------------------------------------------------------------------------------------------
  def uniqueNonTrivialChoices[State, InformationSet, Action](
      game: ExtensiveGame[State, InformationSet, Action]): Set[Action] =
  {
    val nodes : Traversable[ExtensiveNode[InformationSet, Action]] =
      GameTraversal.traverseTreeBreadthFirst(game)

    def nodeChoices(node : ExtensiveNode[InformationSet, Action]) : Traversable[Action] =
      node match {
        case Decision(_, _, choices : Traversable[Action]) =>
          choices
        case _ =>
          None
      }

    val nonTrivialChoices : Traversable[Action] =
      new Traversable[Action]() {
        def foreach[U](traverser: (Action) => U) {
          nodes.foreach(node => {
            val choices = nodeChoices(node)

            val isTrivial : Boolean =
              choices.toSeq.lengthCompare(1) <= 0

            if (! isTrivial) {
              choices.foreach(traverser)
            }
          })
        }
      }

    val uniqueNonTrivialChoices : Set[Action] =
      nonTrivialChoices.toSet

    uniqueNonTrivialChoices
  }
}
