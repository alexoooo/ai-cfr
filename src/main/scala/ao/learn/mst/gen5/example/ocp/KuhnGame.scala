package ao.learn.mst.gen5.example.ocp

import ao.learn.mst.gen2.example.kuhn.action.{KuhnDecision, KuhnActionSequence, KuhnGenAction, KuhnCardSequence}
import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen2.example.kuhn.card.KuhnDeck
import scala._
import ao.learn.mst.gen2.example.kuhn.view.KuhnObservation
import ao.learn.mst.gen5.node.Chance
import ao.learn.mst.gen2.example.kuhn.state.KuhnStake
import ao.learn.mst.gen2.example.kuhn.state.KuhnState


//----------------------------------------------------------------------------------------------------------------------
case object KuhnGame
  extends ExtensiveGame[Option[KuhnState], KuhnObservation, KuhnGenAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def playerCount = 2

  def initialState = None


  //--------------------------------------------------------------------------------------------------------------------
  private def identify(state: Option[KuhnState]): StatePartition =
    if (state.isEmpty) {
      ChancePartition
    } else if (state.get.winner.isDefined) {
      TerminalPartition
    } else {
      DecisionPartition
    }


  //--------------------------------------------------------------------------------------------------------------------
  def node(state: Option[KuhnState]): ExtensiveNode[KuhnObservation, KuhnGenAction] =
    identify(state) match {
      case TerminalPartition =>
        Terminal(terminalPayoffs(state.get))

      case ChancePartition =>
        val outcomes : Traversable[Outcome[KuhnGenAction]] =
          Outcome.equalProbability(KuhnDeck.permutations)
        Chance(outcomes)

      case DecisionPartition =>
        val nextToAct = Rational(state.get.nextToAct.get.id)
        val choices : Traversable[KuhnGenAction] = KuhnDecision.values
        val infoSet = state.get.playerView
        Decision(nextToAct, infoSet, choices)
    }


  //--------------------------------------------------------------------------------------------------------------------
  private def terminalPayoffs(state: KuhnState) : Seq[Double] = {
    val stake   = state.stake
    val winner  = state.winner.get
    val outcome = stake.toOutcome(winner)
    outcome.toSeq
  }


  //--------------------------------------------------------------------------------------------------------------------
  def transition(state: Option[KuhnState], action: KuhnGenAction): Option[KuhnState] =
    Some((identify(state), action) match {
      case (ChancePartition, chance : KuhnCardSequence) =>
        new KuhnState(chance)

      case (DecisionPartition, decision : KuhnDecision) =>
        state.get.act(decision)

      case _ => throw new IllegalArgumentException(
        "Unexpected state or action type: " + state + " | " + action)
    })
}
