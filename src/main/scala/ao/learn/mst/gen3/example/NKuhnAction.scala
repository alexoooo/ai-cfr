package ao.learn.mst.gen3.example

import ao.learn.mst.gen3.NExtensiveAction
import ao.learn.mst.example.kuhn.card.KuhnCardSequence

/**
 * 13/07/13 8:36 PM
 */
sealed trait NKuhnAction extends NExtensiveAction {

}


case class Chance(
    delegate : KuhnCardSequence
) extends NKuhnAction



