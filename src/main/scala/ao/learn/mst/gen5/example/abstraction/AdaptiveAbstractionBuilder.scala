package ao.learn.mst.gen5.example.abstraction

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.meta.ExtensiveMetaModel

/**
 * Learns over time.
 *
 */
trait AdaptiveAbstractionBuilder
{
  def build[State, InformationSet, Action](
    game      : ExtensiveGame[State, InformationSet, Action],
    metaModel : ExtensiveMetaModel[InformationSet, Action]
    ) : AdaptiveAbstraction[InformationSet, Action]
}
