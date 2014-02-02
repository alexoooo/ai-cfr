package ao.learn.mst.gen5.example.abstraction

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Decision, ExtensiveNode}
import scala.collection.immutable.ListSet
import scala.collection


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

    val decisions : Traversable[Decision[InformationSet, Action]] =
      nodes.flatMap(nodeInformationSet)

    withoutDuplicates(decisions)
  }

  private def withoutDuplicates[InformationSet, Action](
    nodes : Traversable[Decision[InformationSet, Action]])
    : Set[Decision[InformationSet, Action]] =
  {
    val decisionSet =
      collection.mutable.Set[Decision[InformationSet, Action]]()

    var decisionSeq =
      collection.mutable.Buffer[Decision[InformationSet, Action]]()

    for (n <- nodes) {
      if (! decisionSet.contains(n)) {
        decisionSet += n
        decisionSeq += n
      }
    }

    ListSet() ++ decisionSeq.reverse
  }

  def informationSets[State, InformationSet, Action](
    game: ExtensiveGame[State, InformationSet, Action]): Set[InformationSet] =
  {
    val seenInfoSets =
      collection.mutable.Set[InformationSet]()

    var seenInfoSeq =
      collection.mutable.Buffer[InformationSet]()

    for (d <- decisions(game)) {
      val info = d.informationSet
      if (! seenInfoSets.contains(info)) {
        seenInfoSets += info
        seenInfoSeq  += info
      }
    }

    ListSet() ++ seenInfoSeq.reverse
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
