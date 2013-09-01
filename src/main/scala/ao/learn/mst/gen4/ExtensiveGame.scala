package ao.learn.mst.gen4

import ao.learn.mst.gen4.sp.StatePartition


/**
 *
 */
trait ExtensiveGame[State, Action, InformationSet]
{
  // meta

  def playerCount : Int

  def initialState : State

  def identify(state : State) : StatePartition


  // terminal

  def payoff(state : State) : Option[Seq[Double]]


  // non-terminal

  def nextToAct(state : State) : Option[Player]

  def actions(state : State) : Option[Traversable[Action]]

  def transition(state : State, action : Action) : Option[State]


  // chance

  def probability(state : State, action : Action) : Option[Double]


  // decision

  def informationSet(state : State) : Option[InformationSet]
}
