package ao.learn.mst.gen5.example.simple.rps

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Terminal, Decision, ExtensiveNode}
import scala.Option


/**
 * http://en.wikipedia.org/wiki/Rock-paper-scissors
 */
case object RockPaperScissorsGame
  extends ExtensiveGame[RpsState, Unit, RpsAction]
{
  def playerCount: Int =
    2


  def initialState: RpsState =
    RpsState(Nil)


  def node(state: RpsState): ExtensiveNode[Unit, RpsAction] = {
    val decisionCount = state.choices.length

    if (decisionCount == playerCount)
    {
      Terminal(
        terminalPayoff(state))
    }
    else
    {
      val choices : Traversable[RpsAction] =
        Seq(Rock, Paper, Scissors)

      Decision(decisionCount, (), choices)
    }
  }

  private def terminalPayoff(state: RpsState) : Seq[Double] = {
    assert(playerCount == 2, "Only two-player game supported")

    payoffsForWinner(
      state,
      winningAction(
        state.choices(0),
        state.choices(1)))
  }

  private def payoffsForWinner(state: RpsState, winner : Option[RpsAction]) : Seq[Double] = {
    val zeroForAll : Seq[Double] =
      Seq.fill(playerCount)(0.0)

    winner match {
      case None => zeroForAll

      case Some(choice) => {
        val winningPlayer =
          state.choices.indexOf(choice)

        zeroForAll.updated(winningPlayer, 1.0)
      }
    }
  }

  private def winningAction(a : RpsAction, b : RpsAction): Option[RpsAction] =
    if (a == b)
    {
      None // if both players throw the same shape, the game is tied
    }
    else
    {
      def distinctWinner(a : RpsAction, b : RpsAction) : RpsAction =
        (a, b) match {
          case (Rock    , Scissors) => Rock     // rock beats scissors
          case (Scissors, Paper   ) => Scissors // scissors beat paper
          case (Rock    , Paper   ) => Paper    // paper beats rock

          case _ => distinctWinner(b, a) // match in reverse order
        }

      Some(distinctWinner(a, b))
    }



  def transition(nonTerminal: RpsState, action: RpsAction): RpsState =
    RpsState(nonTerminal.choices :+ action)
}
