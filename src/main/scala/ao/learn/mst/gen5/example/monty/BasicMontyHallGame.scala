package ao.learn.mst.gen5.example.monty

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Terminal, Chance, Decision, ExtensiveNode}

/**
 *
 */
object BasicMontyHallGame
  extends
    ExtensiveGame[
      MontyHallState,
      BasicMontyHallInfo,
      MontyHallAction]
{
  private val underlyingGame =
    MontyHallGame

  def playerCount: Int =
    underlyingGame.playerCount

  def initialState: MontyHallState =
    underlyingGame.initialState

  def transition(nonTerminal: MontyHallState, action: MontyHallAction): MontyHallState =
    underlyingGame.transition(nonTerminal, action)

  def node(state: MontyHallState): ExtensiveNode[BasicMontyHallInfo, MontyHallAction] = {
    val underlyingNode : ExtensiveNode[MontyHallInfo, MontyHallAction]=
      underlyingGame.node(state)

    underlyingNode match {
      case Decision(p, informationSet : MontyHallInfo, c) =>
        Decision(p, BasicMontyHallInfo.fromMontyHallInfo(informationSet), c)

      case Chance(outcomes) =>
        Chance(outcomes)

      case Terminal(payoffs) =>
        Terminal(payoffs)
    }
  }
}
