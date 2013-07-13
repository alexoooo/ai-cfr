package ao.learn.mst.example.ocp.adapt

import ao.learn.mst.example.kuhn.state.{KuhnOutcome, KuhnState}
import ao.learn.mst.gen2.game.ExtensiveGameTerminal
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.example.ocp.state.{OcpOutcome, OcpState}

/**
 * User: ao
 * Date: 26/07/12
 * Time: 9:33 PM
 */

case class OcpGameTerminal(delegate: OcpState)
  extends ExtensiveGameTerminal {
  def payoff = {
    val outcome: OcpOutcome =
      delegate.stake.toOutcome(delegate.winner.get)

    ExpectedValue(
      outcome.toSeq)
  }
}
