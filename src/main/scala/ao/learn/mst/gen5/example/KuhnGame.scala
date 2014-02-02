package ao.learn.mst.gen5.example

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
  extends ExtensiveGame[KuhnState, KuhnObservation, KuhnGenAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def playerCount = 2

  def initialState = null


  //--------------------------------------------------------------------------------------------------------------------
  private def identify(state: KuhnState): StatePartition =
    if (state == initialState) {
      ChancePartition
    } else if (state.winner.isDefined) {
      TerminalPartition
    } else {
      DecisionPartition
    }


  //--------------------------------------------------------------------------------------------------------------------
  def node(state: KuhnState): ExtensiveNode[KuhnObservation, KuhnGenAction] =
    identify(state) match {
      case TerminalPartition =>
        Terminal(terminalPayoffs(state))

      case ChancePartition => {
        val outcomes : Traversable[Outcome[KuhnGenAction]] =
          Outcome.equalProbability(KuhnDeck.permutations)
        Chance(outcomes)
      }

      case DecisionPartition => {
        val nextToAct = Rational(state.nextToAct.get.id)
        val choices : Traversable[KuhnGenAction] = KuhnDecision.values
        val infoSet = state.playerView
        Decision(nextToAct, infoSet, choices)
      }
    }


  //--------------------------------------------------------------------------------------------------------------------
  private def terminalPayoffs(state: KuhnState) : Seq[Double] = {
    val stake   = state.stake
    val winner  = state.winner.get
    val outcome = stake.toOutcome(winner)
    outcome.toSeq
  }


  //--------------------------------------------------------------------------------------------------------------------
  def transition(state: KuhnState, action: KuhnGenAction): KuhnState =
    (identify(state), action) match {
      case (ChancePartition, chance : KuhnCardSequence) =>
        KuhnState(chance, KuhnActionSequence.Empty, new KuhnStake())

      case (DecisionPartition, decision : KuhnDecision) =>
        state.act(decision)

      case _ => throw new IllegalArgumentException(
        "Unexpected state or action type: " + state + " | " + action)
    }
}
