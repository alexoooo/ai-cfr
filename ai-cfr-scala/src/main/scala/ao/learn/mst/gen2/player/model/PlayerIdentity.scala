package ao.learn.mst.gen2.player.model

//---------------------------------------------------------------------------------------------------------------------
/**
 * http://en.wikipedia.org/wiki/Extensive-form_game
 * " A partition of the non-terminal nodes of the game tree in n+1 subsets,
 *    one for each (rational [aka. deliberate]) player, and with a special subset for a fictitious
 *    player called Chance (or Nature). Each player's subset of nodes is referred
 *    to as the "nodes of the player".
 *    (A game of complete information thus has an empty set of Chance nodes.)"
 */


//---------------------------------------------------------------------------------------------------------------------
sealed abstract class PlayerIdentity

case class  DeliberatePlayer(index : Int) extends PlayerIdentity
case object ChancePlayer                  extends PlayerIdentity