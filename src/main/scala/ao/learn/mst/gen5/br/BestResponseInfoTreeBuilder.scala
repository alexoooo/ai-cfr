package ao.learn.mst.gen5.br

import ao.learn.mst.gen5.ExtensiveGame
/**
 * 28/12/13 10:42 PM
 */
object BestResponseInfoTreeBuilder
{
  def build[S, I, A](
    game : ExtensiveGame[S, I, A])
    : BestResponseInfoTree[S, I, A] =
  {
    def nonTerminalInfoActions(respondingPlayer: Int): Map[I, Map[A, I]] = {
      val transitions : Traversable[InfoTreeTransition[I, A]] = 
        InfoTreeTraverser.traverseInfoSetTransitions(game, respondingPlayer)

      transitions
        .groupBy(_.parentInfoSet)
        .mapValues((infoTransitions: Traversable[InfoTreeTransition[I, A]]) => {
          infoTransitions
            .map(transition => (transition.action, transition.childInfoSet))
            .toMap
        })
    }

    val playerNonTerminalInfoActions : Seq[Map[I, Map[A, I]]] =
      (0 until game.playerCount)
        .map(nonTerminalInfoActions)

    val infoRoots : Seq[I] =
      (0 until game.playerCount)
        .map(InfoTreeTraverser.rootInfoSet(game, _))

    LiteralBestResponseInfoTree(
      infoRoots,
      playerNonTerminalInfoActions)


//        .mapValues((actions: Set[A]) =>
//          actions.map(action => (action, game.)))



    //    val allInfoActions : Map[I, Map[A, I]] =
    //      AbstractionUtils.decisions(game)
    //        .filter(decision => decision.nextToAct.index == respondingPlayer)
    //        .map(decision => decision.informationSet -> decision.choices.toSet)
    //        .toMap
    //        .mapValues((actions: Set[A]) =>
    //          actions.map(action => (action, game.)))
  }

  private case class LiteralBestResponseInfoTree[S, I, A](
    infoRoots : Seq[I],
    nonTerminalInfoActions : Seq[Map[I, Map[A, I]]])
    extends BestResponseInfoTree[S, I, A]
  {
    def root(player: Int): I =
      infoRoots(player)

    def nonTerminalInformationSets(player: Int): Set[I] =
      nonTerminalInfoActions(player).keySet

    def actionTransitions(informationSet: I, player: Int): Map[A, I] =
      nonTerminalInfoActions(player)(informationSet)
  }
}
