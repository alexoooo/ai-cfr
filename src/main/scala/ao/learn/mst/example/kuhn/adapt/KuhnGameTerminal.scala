package ao.learn.mst.example.kuhn.adapt

import ao.learn.mst.example.kuhn.state.{KuhnOutcome, KuhnState}
import ao.learn.mst.gen2.game.ExtensiveGameTerminal
import ao.learn.mst.gen2.solve.ExpectedValue

/**
 * User: ao
 * Date: 26/07/12
 * Time: 9:33 PM
 */

case class KuhnGameTerminal(delegate : KuhnState)
    extends ExtensiveGameTerminal
{
  def payoff = {
    val outcome: KuhnOutcome =
      delegate.stake.toOutcome(delegate.winner.get)

    ExpectedValue(
      outcome.toSeq)
  }
}
