package ao.learn.mst.gen2.prob

import collection.immutable.SortedMap
import ao.learn.mst.gen2.player.model.FiniteAction

/**
 * http://en.wikipedia.org/wiki/Probability_mass_function
 *
 * In probability theory and statistics, a probability mass function (pmf)
 *  is a function that gives the probability that a discrete random
 *  variable is exactly equal to some value.
 */
case class ActionProbabilityMass(
  actionProbabilities : SortedMap[FiniteAction, Double])