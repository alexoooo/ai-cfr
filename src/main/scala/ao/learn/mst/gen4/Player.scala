package ao.learn.mst.gen4


//---------------------------------------------------------------------------------------------------------------------
/**
 * http://en.wikipedia.org/wiki/Extensive-form_game
 * " A partition of the non-terminal nodes of the game tree in n+1 subsets,
 *    one for each (rational) player, and with a special subset for a fictitious
 *    player called Chance (or Nature). Each player's subset of nodes is referred
 *    to as the "nodes of the player".
 *    (A game of complete information thus has an empty set of Chance nodes.)"
 *
 * Above, "rational" implies that the player is able to interpret the payoff (outcome) at terminal nodes.
 * Also, rationality implies that a player will never knowingly play a strictly dominated strategy.
 * Note that if we plan for rational opponents, we can only do better against irrational one.
 *  Rationality is a safe assumption, but in practice deliberate players might not be rational (but would
 *  still be considered "Rational" as per below).
 */


//---------------------------------------------------------------------------------------------------------------------
sealed trait Player

case class  Rational(index : Int) extends Player
case object Nature                extends Player