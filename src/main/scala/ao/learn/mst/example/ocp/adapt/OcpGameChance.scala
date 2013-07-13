package ao.learn.mst.example.ocp.adapt

import ao.learn.mst.gen2.prob.ActionProbabilityMass
import ao.learn.mst.gen2.game.ExtensiveGameChance
import collection.immutable.{SortedSet, SortedMap}
import ao.learn.mst.example.ocp.card.{OcpCard, OcpCardSequence}
import ao.learn.mst.example.ocp.card.OcpCard._
import ao.learn.mst.example.ocp.state.OcpState
import ao.learn.mst.example.ocp.card.OcpCard.OcpCard
import ao.learn.mst.gen2.player.model.FiniteAction

/**
 *
 */
object OcpGameChance
  extends ExtensiveGameChance
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions: SortedSet[FiniteAction] = {
    val cardPermutations =
      for {
        firstCard <- OcpCard.values
        secondCard <- OcpCard.values
        if secondCard != firstCard
      } yield (firstCard, secondCard)

    val possibilities: Set[Possibility] =
      for {
        ((firstCard, secondCard), index)
        <- cardPermutations.zipWithIndex
      } yield
        Possibility(index, firstCard, secondCard)

    SortedSet[FiniteAction]() ++
      possibilities
  }


  //--------------------------------------------------------------------------------------------------------------------
  def probabilityMass: ActionProbabilityMass = {
    val possibilities = actions

    val uniformProbability: Double =
      1.0 / possibilities.size

    val possibilityProbabilities = SortedMap[FiniteAction, Double]() ++
      possibilities.map(_ -> uniformProbability)

    ActionProbabilityMass(
      possibilityProbabilities)
  }


  //--------------------------------------------------------------------------------------------------------------------
  def child(outcome: FiniteAction) = {
    val instance: Possibility =
      actions.toSeq(outcome.index).asInstanceOf[Possibility]

    val cardSequence =
      OcpCardSequence(
        instance.firstPlayerCard,
        instance.secondPlayerCard)

    OcpGameDecision(
      new OcpState(cardSequence))
  }


  //--------------------------------------------------------------------------------------------------------------------
  case class Possibility(
      override val index: Int,

      firstPlayerCard: OcpCard,
      secondPlayerCard: OcpCard)
    extends FiniteAction(index)
  {
    override def toString =
      firstPlayerCard + " / " + secondPlayerCard
  }

}