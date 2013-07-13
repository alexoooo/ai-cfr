package ao.learn.mst.cfr

import ao.learn.mst.gen2.game._
import ao.learn.mst.gen2.player.model.RationalPlayer


//----------------------------------------------------------------------------------------------------------------------
object PlayerViewBuilder
{
  //--------------------------------------------------------------------------------------------------------------------
//  def expand(game : ExtensiveGame, protagonist : RationalPlayer) : PlayerViewNode =
//    expand(game, game.gameTreeRoot, protagonist)
  
  private def expand(
      game        : ExtensiveGame,
      root        : ExtensiveGameNode,
      protagonist : RationalPlayer
      ): PlayerViewNode =
  {
    root match {
      case nonTerminal: ExtensiveGameNonTerminal => {
        val kids : Seq[PlayerViewNode] =
          nonTerminal.actions
            .map(nonTerminal.child(_))
            .map(expand(game, _, protagonist))
            .toIndexedSeq

        nonTerminal match {
          case decision : ExtensiveGameDecision => {
            if (decision.player == protagonist) {
              new ProponentNode( kids, decision.informationSet )
            } else {
              new OpponentNode( kids )
            }
          }

          case chance: ExtensiveGameChance =>
            new ChanceNode(kids)
        }
      }

      case terminal: ExtensiveGameTerminal => {
        val rationalPlayers: Seq[RationalPlayer] =
          (0 until game.rationalPlayerCount).map( RationalPlayer(_) )
        
        new TerminalNode( rationalPlayers.map(terminal.payoff.outcomes(_)) )
      }
    }
  }
}