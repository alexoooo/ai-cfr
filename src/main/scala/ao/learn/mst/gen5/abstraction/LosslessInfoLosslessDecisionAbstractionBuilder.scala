package ao.learn.mst.gen5.abstraction

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen5.node.Decision
import scala.collection.immutable.SortedSet
import ao.learn.mst.gen5.example.abstraction.{AbstractionUtils, OpaqueAbstractionBuilder}


//----------------------------------------------------------------------------------------------------------------------
case object LosslessInfoLosslessDecisionAbstractionBuilder
  extends OpaqueAbstractionBuilder
{
  //--------------------------------------------------------------------------------------------------------------------
  def generate[State, InformationSet, Action](
      game: ExtensiveGame[State, InformationSet, Action]
      ): ExtensiveAbstraction[InformationSet, Action] =
  {
    val actionToIndex : Map[Action, Int] = {
      val nonTrivialChoices : Set[Action] =
        AbstractionUtils.uniqueNonTrivialChoices(game)

      nonTrivialChoices.zipWithIndex.toMap
    }

    val informationSetToIndex : Map[InformationSet, Int] = {
      val informationSets : Set[InformationSet] =
        AbstractionUtils.informationSets(game)

      informationSets.zipWithIndex.toMap
    }

    val indexToInformationSet : Map[Int, InformationSet] =
      informationSetToIndex map {_.swap}

    val decisions : Traversable[Decision[InformationSet, Action]] =
      AbstractionUtils.decisions(game)

    val informationSetToDecision : Map[InformationSet, Traversable[Decision[InformationSet, Action]]] =
      decisions.groupBy(_.informationSet)

    def actionIndexes(informationSetIndex : Int) : Seq[Int] = {
      val informationSetMaybe : Option[InformationSet] =
        indexToInformationSet.get(informationSetIndex)

      informationSetMaybe match {
        case None => Seq.empty
        case Some(infoSet) =>
          val informationSetDecisions : Traversable[Decision[InformationSet, Action]] =
            informationSetToDecision(infoSet)

          val informationSetAction : Set[Action] =
            informationSetDecisions.flatMap(_.choices).toSet

          informationSetAction
            .map(actionToIndex)
            .toSeq
            .sorted
      }
    }

    val informationSetIndexToActionIndex : Seq[Seq[Int]] =
      (0 until informationSetToIndex.size)
        .map(actionIndexes)

    new LosslessInfoLosslessDecisionAbstractionAbstraction(
      informationSetToIndex,
      actionToIndex,
      informationSetIndexToActionIndex)
  }


  //--------------------------------------------------------------------------------------------------------------------
  private class LosslessInfoLosslessDecisionAbstractionAbstraction[InformationSet, Action](
      informationSetToIndex : Map[InformationSet, Int],
      actionToIndex         : Map[Action, Int],
      informationSetIndexToActionIndex : Seq[Seq[Int]])
    extends ExtensiveAbstraction[InformationSet, Action]
  {
    def informationSetIndex(informationSet: InformationSet): Int =
      informationSetToIndex(informationSet)

    def actionIndex(action: Action): Int =
      actionToIndex(action)

    def actionCount(informationSet: InformationSet): Int = {
      val index : Int =
        informationSetIndex(informationSet)
      
      val actionToIndex : Seq[Int] = 
        informationSetIndexToActionIndex(index)

      actionToIndex.length
    }

    def actionSubIndex(informationSet: InformationSet, action: Action): Int = {
      val actionToGlobalActionIndex : Seq[Int] =
        informationSetIndexToActionIndex(
          informationSetIndex(
            informationSet))
      
      val globalActionIndexes = SortedSet[Int]() ++
        actionToGlobalActionIndex

      val localActionIndex : Int = {
        val orderedGlobalActionIndex : Seq[Int] =
          globalActionIndexes.toSeq
        
        val globalActionIndex : Int =
          actionIndex(action)
        
        orderedGlobalActionIndex.indexOf(globalActionIndex)
      }

      localActionIndex
    }
  }
}
