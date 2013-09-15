package ao.learn.mst.gen5.example.simple.rpsw

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Decision, Terminal, ExtensiveNode}
import scala.Some

/**
 * http://math.stackexchange.com/questions/410558/rock-paper-scissors-well
 * "The well wins against rock and scissors (because both fall into it)
 *    but loses against paper (because the paper covers it)"
 */
object RockPaperScissorsWellGame
  extends ExtensiveGame[RpswState, Unit, RpswAction]
{
  def playerCount: Int =
    2


  def initialState: RpswState =
    RpswState(Nil)


  def node(state: RpswState): ExtensiveNode[Unit, RpswAction] = {
    val decisionCount = state.choices.length

    if (decisionCount == playerCount)
    {
      Terminal(
        terminalPayoff(state))
    }
    else
    {
      val choices : Traversable[RpswAction] =
        Seq(RpswRock, RpswPaper, RpswScissors, RpswWell)

      Decision(decisionCount, (), choices)
    }
  }


  private def terminalPayoff(state: RpswState) : Seq[Double] = {
    assert(playerCount == 2, "Only two-player game supported")

    payoffsForWinner(
      state,
      winningAction(
        state.choices(0),
        state.choices(1)))
  }


  private def payoffsForWinner(state: RpswState, winner : Option[RpswAction]) : Seq[Double] = {
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


  private def winningAction(a : RpswAction, b : RpswAction): Option[RpswAction] =
    if (a == b)
    {
      None // if both players throw the same shape, the game is tied
    }
    else
    {
      def distinctWinner(a : RpswAction, b : RpswAction) : RpswAction =
        (a, b) match {
          case (RpswRock    , RpswScissors) => RpswRock     // rock beats scissors
          case (RpswScissors, RpswPaper   ) => RpswScissors // scissors beat paper
          case (RpswPaper   , RpswRock    ) => RpswPaper    // paper beats rock
          case (RpswPaper   , RpswWell    ) => RpswPaper    // paper covers well
          case (RpswWell    , RpswRock    ) => RpswWell     // rock drowns in well
          case (RpswWell    , RpswScissors) => RpswWell     // scissors drown in well

          case _ => distinctWinner(b, a) // match in reverse order
        }

      Some(distinctWinner(a, b))
    }

  def transition(nonTerminal: RpswState, action: RpswAction): RpswState =
    RpswState(nonTerminal.choices :+ action)
}
