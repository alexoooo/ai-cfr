package ao.learn.mst.gen5.example.kuhn.view

import ao.learn.mst.gen5.example.kuhn.card.KuhnCard._
import ao.learn.mst.gen5.example.kuhn.action.KuhnActionSequence._
import ao.learn.mst.gen5.example.kuhn.state.KuhnStake

/**
 * All information available to a particular player
 */
case class KuhnObservation(
  holeCard       : KuhnCard,
  actionSequence : KuhnActionSequence,
  stake          : KuhnStake)
