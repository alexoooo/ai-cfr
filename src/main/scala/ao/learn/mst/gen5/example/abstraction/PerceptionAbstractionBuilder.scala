package ao.learn.mst.gen5.example.abstraction

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen5.meta.ExtensiveMetaModel

/**
 * Builds game abstraction based on what can be observed by players.
 */
trait PerceptionAbstractionBuilder
{
  def generate[State, InformationSet, Action](
    game      : ExtensiveGame[State, InformationSet, Action],
    metaModel : ExtensiveMetaModel[InformationSet, Action]
    ) : ExtensiveAbstraction[InformationSet, Action]
}
