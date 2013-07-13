package ao.learn.mst.gen.history

import ao.learn.mst.gen.act.ExtensiveActionObservation

/**
 * http://poker.cs.ualberta.ca/publications/abourisk.msc.pdf
 *
 * For each player i 2 N a partition Ii of fh 2 H : P (h) = ig with the property that
 *   A(h) = A(h0) whenever h and h0 are in the same member of the partition.
 * Ii is the information partition of player i; a set Ii 2 Ii is an information set of player i.
 *
 * Information sets are groupings by ExtensiveInformationPartition.
 *
 *  http://en.wikipedia.org/wiki/Information_set_(game_theory)
 *
 *  "In game theory, an information set is a set that, for a particular player,
 *    establishes all the possible moves that could have taken place in
 *    the game so far, given what that player has observed."
 */
case class ExtensiveInformationPartition(
    /**
     * This object applies to a particular rational player: P.
     *
     * Sequence of actions observable (and remembered) by P.
     * For chance nodes not observed by P are excluded.
     */
    observations : ExtensiveHistory)
{
  def addEvent(event : ExtensiveActionObservation) =
    ExtensiveInformationPartition(
      ExtensiveHistory(observations.events :+ event))
}