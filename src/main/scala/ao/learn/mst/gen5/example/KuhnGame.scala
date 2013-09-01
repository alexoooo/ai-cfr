package ao.learn.mst.gen5.example

import ao.learn.mst.example.kuhn.action.{KuhnDecision, KuhnActionSequence, KuhnGenAction, KuhnCardSequence}
import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen4.sp.{DecisionPartition, TerminalPartition, ChancePartition, StatePartition}
import ao.learn.mst.gen5.node._
import ao.learn.mst.example.kuhn.card.KuhnDeck
import scala._
import ao.learn.mst.example.kuhn.view.KuhnObservation
import ao.learn.mst.gen5.node.Chance
import ao.learn.mst.example.kuhn.state.KuhnStake
import ao.learn.mst.example.kuhn.state.KuhnState
import ao.learn.mst.gen4.Rational


//----------------------------------------------------------------------------------------------------------------------
case object KuhnGame
  extends ExtensiveGame[KuhnState, KuhnGenAction, KuhnObservation]
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
  def node(state: KuhnState): ExtensiveNode[KuhnGenAction, KuhnObservation] =
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
        Decision(nextToAct, choices, infoSet)
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
