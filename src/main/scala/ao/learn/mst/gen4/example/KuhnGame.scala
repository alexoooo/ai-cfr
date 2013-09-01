package ao.learn.mst.gen4.example

import ao.learn.mst.gen4._
import ao.learn.mst.example.kuhn.action._
import ao.learn.mst.example.kuhn.state.{KuhnStake, KuhnState}
import ao.learn.mst.example.kuhn.view.KuhnObservation
import ao.learn.mst.gen2.player.model.PlayerIdentity
import ao.learn.mst.example.kuhn.view.KuhnObservation
import ao.learn.mst.example.kuhn.action.KuhnPlayerAction._
import ao.learn.mst.example.kuhn.state.KuhnState
import ao.learn.mst.example.kuhn.card.KuhnDeck
import ao.learn.mst.example.kuhn.view.KuhnObservation
import ao.learn.mst.gen4.Rational
import scala.Some
import ao.learn.mst.example.kuhn.state.KuhnState
import ao.learn.mst.example.kuhn.action.KuhnCardSequence
import scala.languageFeature.experimental.macros
import ao.learn.mst.gen4.sp.{TerminalPartition, ChancePartition, DecisionPartition, StatePartition}

/**
 * 28/07/13 7:11 PM
 */
case object KuhnGame
    extends ExtensiveGame[KuhnState, KuhnGenAction, KuhnObservation]
{
  def playerCount: Int = 2


  def initialState: KuhnState =
    null


  def identify(state: KuhnState): StatePartition =
    if (state == initialState) {
      ChancePartition
    } else if (state.winner.isDefined) {
      TerminalPartition
    } else {
      DecisionPartition
    }


  def payoff(state: KuhnState): Option[Seq[Double]] =
    identify(state) match {
      case TerminalPartition => {
        val outcome =
          state.stake.toOutcome(state.winner.get)
        Some(outcome.toSeq)
      }

      case _ => None
    }


  def nextToAct(state: KuhnState): Option[Player] =
    identify(state) match {
      case TerminalPartition => None
      case ChancePartition => Some(Nature)
      case DecisionPartition => Some(Rational(state.nextToAct.get.id))
    }


  def actions(state: KuhnState): Option[Traversable[KuhnGenAction]] =
    identify(state) match {
      case TerminalPartition => None
      case ChancePartition => {
        Some(KuhnDeck.permutations)
      }
      case DecisionPartition => {
        Some(KuhnDecision.values)
      }
    }


  def transition(state: KuhnState, action: KuhnGenAction): Option[KuhnState] =
    (identify(state), action) match {
      case (ChancePartition, s : KuhnCardSequence) => {
        Some(KuhnState(s, KuhnActionSequence.Empty, new KuhnStake()))
      }
      case (DecisionPartition, d : KuhnDecision) => {
        Some(state.act(d))
      }
      case _ => None
    }


  def probability(state: KuhnState, action: KuhnGenAction): Option[Double] =
    (identify(state), action) match {
      case (ChancePartition, s : KuhnCardSequence) => {
        if (KuhnDeck.permutations.contains(s)) {
          Some(1.0 / KuhnDeck.permutations.size)
        } else {
          None
        }
      }
      case _ => None
    }


  def informationSet(state: KuhnState): Option[KuhnObservation] =
    identify(state) match {
      case DecisionPartition => Some(state.playerView)
      case _ => None
    }
}
