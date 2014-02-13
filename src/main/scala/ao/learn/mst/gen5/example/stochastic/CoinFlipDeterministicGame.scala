package ao.learn.mst.gen5.example.stochastic

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.node.Chance

/**
 * Die roll
 * |
 * +-- false
 * |   |
 * |   +-- 0 -> 0
 * |   |
 * |   +-- 1 -> 1
 * |
 * +-- true
 *     |
 *     +-- 0 -> 0
 *     |
 *     +-- 1 -> 1
 *     |
 *     +-- 2 -> 2
 *
 */
object CoinFlipDeterministicGame
    extends ExtensiveGame[CoinFlipDeterministicState, CoinFlipDeterministicState, CoinFlipDeterministicAction]
{
  override def playerCount: Int =
    1


  override def initialState: CoinFlipDeterministicState =
    CoinFlipDeterministicState(Seq.empty)


  override def node(
      state: CoinFlipDeterministicState)
      : ExtensiveNode[CoinFlipDeterministicState, CoinFlipDeterministicAction] =
  {
    state.actions match {
      case Seq() =>
        Chance(Outcome.equalProbability(
          Seq(false, true).map(CoinFlipDeterministicChance)))

      case Seq(CoinFlipDeterministicChance(value)) =>
        val choices: Seq[CoinFlipDeterministicDecision] =
          (if (value) {
            0 until 3
          } else {
            0 until 2
          }).map(CoinFlipDeterministicDecision)

        Decision(0, state, choices)

      case Seq(_, CoinFlipDeterministicDecision(value)) =>
        Terminal(Seq(value.toDouble))
    }
  }


  override def transition(
      nonTerminal: CoinFlipDeterministicState,
      action: CoinFlipDeterministicAction)
      : CoinFlipDeterministicState =
  {
    CoinFlipDeterministicState(
      nonTerminal.actions :+ action)
  }
}
