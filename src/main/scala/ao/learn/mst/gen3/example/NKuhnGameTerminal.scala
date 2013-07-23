package ao.learn.mst.gen3.example

import ao.learn.mst.example.kuhn.state.{KuhnOutcome, KuhnState}
import ao.learn.mst.gen3.game.{NExpectedValue, NExtensiveGameTerminal}

/**
 * 21/07/13 5:59 PM
 */
case class NKuhnGameTerminal(delegate : KuhnState)
  extends NExtensiveGameTerminal[NKuhnInfoSet, NKuhnAction]
{
  def payoff: NExpectedValue = {
    val outcome: KuhnOutcome =
      delegate.stake.toOutcome(delegate.winner.get)

    NExpectedValue(outcome.toSeq)
  }
}