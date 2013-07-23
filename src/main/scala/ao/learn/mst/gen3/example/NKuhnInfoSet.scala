package ao.learn.mst.gen3.example

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.example.kuhn.view.KuhnObservation

/**
 * 13/07/13 8:36 PM
 */
case class NKuhnInfoSet(delegate : KuhnObservation)
  extends InformationSet
