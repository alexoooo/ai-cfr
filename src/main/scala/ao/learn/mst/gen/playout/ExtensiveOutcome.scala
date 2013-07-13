package ao.learn.mst.gen.playout

import ao.learn.mst.gen.utility.ExtensivePayoff
import ao.learn.mst.gen.history.ExtensiveHistory

/**
 * Payoffs and history (all actions and chance outcomes) for terminal history
 */
case class ExtensiveOutcome(
    payoffs         : ExtensivePayoff,
    terminalHistory : ExtensiveHistory)