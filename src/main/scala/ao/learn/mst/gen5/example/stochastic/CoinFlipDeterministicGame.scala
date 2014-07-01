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
 *     +-- 1 -> 0
 *     |
 *     +-- 2 -> 1
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
        val falseProb = 0.5

        Chance(
          MapOutcomeSet(Map(
            CoinFlipDeterministicChance(value = false) -> falseProb,
            CoinFlipDeterministicChance(value = true ) -> (1.0 - falseProb)
          )))

      case Seq(CoinFlipDeterministicChance(value)) =>
        val choices: Seq[CoinFlipDeterministicDecision] =
          (if (value) {
            0 until 3
          } else {
            0 until 2
          }).map(CoinFlipDeterministicDecision)

        Decision(0, state, choices)

      case Seq(CoinFlipDeterministicChance(outcome), CoinFlipDeterministicDecision(choice)) =>
        Terminal(Seq(
          if (outcome) {
            if (choice == 2) {
              1
            } else {
              0
            }
          } else {
            if (choice == 1) {
              1
            } else {
              0
            }
          }))
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
