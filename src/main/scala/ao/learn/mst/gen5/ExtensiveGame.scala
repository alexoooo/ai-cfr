package ao.learn.mst.gen5

import ao.learn.mst.gen5.node.ExtensiveNode


//----------------------------------------------------------------------------------------------------------------------
trait ExtensiveGame[State, InformationSet, Action]
{
  def playerCount : Int

  def initialState : State

  def node(state : State) : ExtensiveNode[InformationSet, Action]

  def transition(state : State, action : Action) : State
}
