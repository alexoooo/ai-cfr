package ao.learn.mst.gen5.node

import ao.learn.mst.gen4.{Nature, Rational, Player}
import ao.learn.mst.gen4.sp.{TerminalPartition, ChancePartition, DecisionPartition, StatePartition}


//----------------------------------------------------------------------------------------------------------------------
sealed trait ExtensiveNode[State, InformationSet, Action] {
  def state : State
  def statePartition : StatePartition
}


//----------------------------------------------------------------------------------------------------------------------
sealed trait NonTerminal[State, InformationSet, Action] {
  def nextToAct : Player
}


//----------------------------------------------------------------------------------------------------------------------
case class Decision[State, InformationSet, Action](
  state          : State,
  nextToAct      : Rational,
  informationSet : InformationSet,
  choices        : Traversable[Action]
) extends ExtensiveNode[State, InformationSet, Action]
  with NonTerminal[State, InformationSet, Action] {
  val statePartition = DecisionPartition
}


//----------------------------------------------------------------------------------------------------------------------
case class Chance[State, InformationSet, Action](
  state    : State,
  outcomes : Traversable[Outcome[Action]]
) extends ExtensiveNode[State, InformationSet, Action]
  with NonTerminal[State, InformationSet, Action] {
  val statePartition = ChancePartition
  val nextToAct = Nature
}

case class Outcome[Action](
  action      : Action,
  probability : Double)

object Outcome {
  def equalProbability[Action](actions : Traversable[Action]) : Traversable[Outcome[Action]] = {
    require(! actions.isEmpty, "Must have one or more actions")

    val equalProbability : Double =
      1.0 / actions.size

    def outcome(action : Action) : Outcome[Action] =
      Outcome(action, equalProbability)

    actions.map(outcome)
  }
}


//----------------------------------------------------------------------------------------------------------------------
case class Terminal[State, InformationSet, Action](
  state   : State,
  payoffs : Seq[Double]
) extends ExtensiveNode[State, InformationSet, Action] {
  val statePartition = TerminalPartition
}

object Terminal {
  def fromPayoffs[State](state : State, payoffs : Seq[Double]): Terminal[State, _, _]=
    new Terminal(state, payoffs)
}
