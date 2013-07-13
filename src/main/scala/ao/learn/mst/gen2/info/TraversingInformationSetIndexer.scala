package ao.learn.mst.gen2.info

import annotation.tailrec
import ao.learn.mst.gen2.game._


/**
 * Date: 07/04/12
 * Time: 7:54 PM
 */
object TraversingInformationSetIndexer
{
  //--------------------------------------------------------------------------------------------------------------------
  def preciseIndex(extensiveGame: ExtensiveGame): InformationSetIndex =
  {
//    var informationSets = Map[InformationSet, Int]()
//
//    traverse(extensiveGame.gameTreeRoot, (informationSet: InformationSet) => {
//      if (! informationSets.contains( informationSet )) {
//        informationSets = informationSets +
//          (informationSet -> informationSets.size)
//      }
//    })

    new MappedInformationSetIndex(
      informationSetDecisionNodeIndex(
        extensiveGame.treeRoot))
  }

  private def informationSetDecisionNodeIndex(
      node: ExtensiveGameNode): Map[InformationSet, (ExtensiveGameDecision, Int)] =
  {
    @tailrec def accumulateChildren(
        stack: List[ExtensiveGameNode],
        acc: Map[InformationSet, (ExtensiveGameDecision, Int)]
        ): Map[InformationSet, (ExtensiveGameDecision, Int)] =
    {
      stack match {
        case Nil => acc

        case (_: ExtensiveGameTerminal) :: rest =>
          accumulateChildren(rest, acc)

        case (nonTerminal: ExtensiveGameNonTerminal) :: rest => {

          val nextAcc:Map[InformationSet, (ExtensiveGameDecision, Int)] = nonTerminal match {
            case decision: ExtensiveGameDecision => {

              val informationSetNotEncounteredBefore =
                ! acc.contains( decision.informationSet )

              acc ++ (if (informationSetNotEncounteredBefore) {
                Some(decision.informationSet -> (decision, acc.size))
              } else {
                None
              })
            }
            case _ => acc
          }

          val children:List[ExtensiveGameNode] =
            for (action <- nonTerminal.actions.toList)
              yield nonTerminal.child( action )

          accumulateChildren(
            children ::: rest,
            nextAcc)
        }
      }
    }

    accumulateChildren(List(node), Map())
  }
  
  
  //--------------------------------------------------------------------------------------------------------------------
  private class MappedInformationSetIndex(
      val index: Map[InformationSet, (ExtensiveGameDecision, Int)]
      ) extends InformationSetIndex
  {
    def informationSetCount = index.size

    def indexOf(informationSet: InformationSet) =
      index(informationSet)._2

    def informationSets = index.keys

    def actionsOf(informationSet: InformationSet) =
      index(informationSet)._1.actions

    def nextToAct(informationSet: InformationSet) =
      index(informationSet)._1.player
  }
}
