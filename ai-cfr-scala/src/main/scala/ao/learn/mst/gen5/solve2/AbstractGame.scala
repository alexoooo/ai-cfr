package ao.learn.mst.gen5.solve2

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}

/**
 *
 */
case class AbstractGame[State, InformationSet, Action](
  game: ExtensiveGame[State, InformationSet, Action],
  abstraction: ExtensiveAbstraction[InformationSet, Action])
