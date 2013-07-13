package ao.learn.mst.gen2.info

import ao.learn.mst.gen2.game._
import scala.annotation.tailrec
import scala.collection.immutable.SortedSet
import ao.learn.mst.gen2.player.model.FiniteAction

//----------------------------------------------------------------------------------------------------------------------
/**
 *
 */
object SingleInformationSetIndexer
{
  //--------------------------------------------------------------------------------------------------------------------
  def single(extensiveGame: ExtensiveGame): InformationSetIndex =
  {
    new MappedInformationSetIndex(
      actionDecisionNodeIndex(
        extensiveGame.treeRoot))
  }

  private def actionDecisionNodeIndex(
      node: ExtensiveGameNode): Map[Int, FiniteAction] =
  {
    @tailrec def accumulateActions(
        stack: List[ExtensiveGameNode],
        acc: Map[Int, FiniteAction]
        ): Map[Int, FiniteAction] =
    {
      stack match {
        case Nil => acc

        case (_: ExtensiveGameTerminal) :: rest =>
          accumulateActions(rest, acc)

        case (nonTerminal: ExtensiveGameNonTerminal) :: rest => {

          val nextAcc:Map[Int, FiniteAction] =
            nonTerminal match {
              case decision: ExtensiveGameDecision =>
                acc ++ decision.actions.map(a => (a.index, a))

              case _ => acc
            }

          val children:List[ExtensiveGameNode] =
            for (action <- nonTerminal.actions.toList)
            yield nonTerminal.child( action )

          accumulateActions(
            children ::: rest,
            nextAcc)
        }

        case (unknown) => throw new IllegalStateException(unknown.toString())
      }
    }

    accumulateActions(List(node), Map())
  }


  //--------------------------------------------------------------------------------------------------------------------
  private class MappedInformationSetIndex(
      val index: Map[Int, FiniteAction]
      ) extends InformationSetIndex
  {
    def informationSetCount = 1

    def indexOf(informationSet: InformationSet) = 0

    def informationSets = List(new ValueInformationSet("singleton"))

    def actionsOf(informationSet: InformationSet) =
      index.values.toSet
  }
}