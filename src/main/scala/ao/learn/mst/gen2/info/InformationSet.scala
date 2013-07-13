package ao.learn.mst.gen2.info

//----------------------------------------------------------------------------------------------------------------------
/**
 * An information set is essentially a set of remembered observations
 *  from the point of view of a rational player.
 *
 * http://en.wikipedia.org/wiki/Extensive-form_game#Incomplete_information
 *
 * An information set is a set of decision nodes such that:
 *  1) Every node in the set belongs to one player.
 *  2) When play reaches the information set, the player with the move
 *      cannot differentiate between nodes within the information set;
 *      i.e. if the information set contains more than one node,
 *      the player to whom that set belongs does not know which node
 *      in the set has been reached.
 *
 * Each set of nodes of a rational player is further partitioned in information sets,
 * which make certain choices indistinguishable for the player when making a move,
 * in the sense that:
 *  1) there is a one-to-one correspondence between outgoing edges of any two
 *      nodes of the same information set—thus the set of all outgoing edges
 *      of an information set is partitioned in equivalence classes,
 *      each class representing a possible choice for a player's move at some point, and
 *  2) every (directed) path in the tree from the root to a terminal node can cross
 *      each information set at most once.
 *
 * In a game of perfect information, the information sets are singletons.
 *
 * A player cannot distinguish between nodes in the same information set when making a decision.
 * If a game has an information set with more than one member that game is said to have imperfect information.
 *
 */
class InformationSet(/*index: Int*/)
{
  // equals, hashCode
}