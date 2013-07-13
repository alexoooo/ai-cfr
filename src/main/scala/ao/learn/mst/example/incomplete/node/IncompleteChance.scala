package ao.learn.mst.example.incomplete.node

import ao.learn.mst.gen2.game.ExtensiveGameChance
import ao.learn.mst.example.incomplete.{IncompleteTypeTwo, IncompleteTypeOne}
import ao.learn.mst.gen2.prob.ActionProbabilityMass
import collection.immutable.{SortedMap, SortedSet}
import ao.learn.mst.gen2.player.model.FiniteAction


//----------------------------------------------------------------------------------------------------------------------
case object IncompleteChance extends ExtensiveGameChance {
  def actions =
    SortedSet[FiniteAction](
      IncompleteTypeOne, IncompleteTypeTwo)

  def probabilityMass =
    ActionProbabilityMass(
      SortedMap[FiniteAction, Double]() ++
        actions.map((_ -> 1.0/actions.size)))

  override def child(action: FiniteAction) =
    action.index match {
      case IncompleteTypeOne.index => IncompleteDecisionPlayerOneTypeOne
      case IncompleteTypeTwo.index => IncompleteDecisionPlayerOneTypeTwo
    }
}