package ao.learn.mst.gen2.example.ocp.adapt

import ao.learn.mst.gen2.game.ExtensiveGameChance
import collection.immutable.{SortedSet, SortedMap}
import ao.learn.mst.gen2.example.ocp.card.OcpCard
import ao.learn.mst.gen2.example.ocp.card.OcpCard.OcpCard
import ao.learn.mst.gen2.player.model.FiniteAction
import ao.learn.mst.gen2.prob.ActionProbabilityMass
import ao.learn.mst.gen2.example.ocp.state.OcpState
import ao.learn.mst.gen2.example.ocp.card.OcpCardSequence

/**
 *
 */
object OcpGameChance
  extends ExtensiveGameChance
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions: SortedSet[FiniteAction] = {
    val cardPermutations : Set[(OcpCard, OcpCard)]=
      for {
        firstCard <- OcpCard.values
        secondCard <- OcpCard.values
        if secondCard != firstCard
      } yield (firstCard, secondCard)

    val possibilities : Set[FiniteAction] =
      cardPermutations.zipWithIndex.map(
        p => Possibility(p._2, p._1._1, p._1._2))

    SortedSet[FiniteAction]() ++ possibilities

//    var possibilities = SortedSet[FiniteAction]()
//    for (
//      ((firstCard, secondCard), index) <- cardPermutations.zipWithIndex
//    ) {
//      possibilities += Possibility(index, firstCard, secondCard)
//    }
//    possibilities
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