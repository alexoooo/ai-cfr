package ao.learn.mst.gen2.solve

import ao.learn.mst.gen2.game.ExtensiveGame
import ao.learn.mst.gen2.info.InformationSetIndex
import ao.learn.mst.gen2.cfr.StrategyProfile

//----------------------------------------------------------------------------------------------------------------------
trait ExtensiveGameSolver
{
  def reduceRegret(
    game                : ExtensiveGame,
    informationSetIndex : InformationSetIndex,
    strategyProfile     : StrategyProfile)
}