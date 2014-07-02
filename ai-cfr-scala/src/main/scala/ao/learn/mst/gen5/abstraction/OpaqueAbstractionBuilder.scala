package ao.learn.mst.gen5.example.abstraction

import ao.learn.mst.gen5.{ExtensiveGame, ExtensiveAbstraction}

/**
 * 01/09/13 1:25 AM
 */
trait OpaqueAbstractionBuilder
{
  def generate[State, InformationSet, Action](
    game : ExtensiveGame[State, InformationSet, Action]
    ) : ExtensiveAbstraction[InformationSet, Action]
}
