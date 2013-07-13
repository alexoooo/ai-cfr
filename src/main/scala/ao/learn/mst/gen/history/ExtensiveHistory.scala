package ao.learn.mst.gen.history

import ao.learn.mst.gen.act.ExtensiveActionObservation

/**
 * Sequence of actions taken by rational players or chance.
 */
case class ExtensiveHistory(
    events : Seq[ExtensiveActionObservation])