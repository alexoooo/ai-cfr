package ao.learn.mst.gen3.example

import ao.learn.mst.gen3.NExtensiveAction
import ao.learn.mst.example.kuhn.action.KuhnPlayerAction._
import ao.learn.mst.example.kuhn.action.{KuhnDecision, KuhnCardSequence}

/**
 * Extensive Game wrapper
 */
sealed trait NKuhnAction
  extends NExtensiveAction


case class Chance(
    delegate : KuhnCardSequence
) extends NKuhnAction


case class Decision(
    delegate : KuhnDecision
) extends NKuhnAction
