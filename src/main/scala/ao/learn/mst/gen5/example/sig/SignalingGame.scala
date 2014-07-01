package ao.learn.mst.gen5.example.sig

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.node.Chance

/**
 * http://en.wikipedia.org/wiki/Signaling_game
 */
case object SignalingGame
  extends ExtensiveGame[SigState, SigInfo, SigAction]
{
  def playerCount: Int =
    2


  def initialState: SigState =
    SigState(Seq.empty)

  private val binaryPossibilities : Seq[Boolean] =
    Seq(false, true)


  def node(state: SigState): ExtensiveNode[SigInfo, SigAction] =
  {
    val actions : Int =
      state.actionSequence.length

    if (actions == 0) {
      Chance(chanceOutcomes)
    } else if (actions == 3) {
      Terminal(payoffs(state))
    } else {
      val isSender =
        actions == 1

      if (isSender) {
        senderDecision(state)
      } else {
        receiverDecision(state)
      }
    }
  }


  private val chanceOutcomes: OutcomeSet[SigAction] = {
    val possibilities = binaryPossibilities.map(SigIdentify)
    UniformOutcomeSet(possibilities)
  }

  private def payoffs(state: SigState) : Seq[Double] = {
    state.actionSequence.toList match {
      case List(SigIdentify(senderType), _, SigReceive(guess)) =>
        val success : Boolean =
          senderType == guess

        val payoff : Double =
          if (success) 1.0 else 0.0

        Seq(payoff, payoff)

      case _ => throw new Error
    }
  }

  private def senderDecision(state : SigState) : Decision[SigInfo, SigAction] = {
    val choices : Seq[SigAction] =
      binaryPossibilities.map(SigSend)

    state.actionSequence(0) match {
      case identity : SigIdentify =>
        Decision(0, SigSender(identity), choices)

      case _ => throw new Error
    }
  }

  private def receiverDecision(state : SigState) : Decision[SigInfo, SigAction] = {
    val choices : Seq[SigAction] =
      binaryPossibilities.map(SigReceive)

    state.actionSequence(1) match {
      case message : SigSend =>
        Decision(1, SigReceiver(message), choices)

      case _ => throw new Error
    }
  }


  def transition(nonTerminal: SigState, action: SigAction): SigState =
    SigState(nonTerminal.actionSequence :+ action)
}
