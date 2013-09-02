package ao.learn.mst.gen5.example.abstraction

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen5.node.{Decision, ExtensiveNode}


//----------------------------------------------------------------------------------------------------------------------
/**
 * All information sets are indistinguishable, but actions are retained exactly.
 */
class SingleStateLosslessDecisionAbstractionBuilder
  extends OpaqueAbstractionBuilder
{
  //--------------------------------------------------------------------------------------------------------------------
  def generate[State, InformationSet, Action](
    game: ExtensiveGame[State, InformationSet, Action]
    ): ExtensiveAbstraction[InformationSet, Action] =
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

    val nonTrivialChoiceIndex : Map[Action, Int] =
      uniqueNonTrivialChoices.toSeq.zipWithIndex.toMap

    new SingleStateLosslessActionAbstraction(
      nonTrivialChoiceIndex)
  }


  //--------------------------------------------------------------------------------------------------------------------
  private class SingleStateLosslessActionAbstraction[InformationSet, Action](
    nonTrivialChoiceIndex : Map[Action, Int])
    extends ExtensiveAbstraction[InformationSet, Action]
  {
    private val trivialChoiceIndex : Int =
      nonTrivialChoiceIndex.size

    def informationSetIndex(informationSet: InformationSet): Int =
      0

    def actionSubIndex(informationSet: InformationSet, action: Action): Int =
      actionIndex(action)

    def actionIndex(action: Action): Int = {
      if (nonTrivialChoiceIndex.contains(action)) {
        nonTrivialChoiceIndex(action)
      } else {
        trivialChoiceIndex
      }
    }
  }
}
