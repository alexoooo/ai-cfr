package ao.learn.mst.gen5.node

import ao.learn.mst.gen4.{Nature, Rational, Player}
import ao.learn.mst.gen4.sp.{TerminalPartition, ChancePartition, DecisionPartition, StatePartition}


//----------------------------------------------------------------------------------------------------------------------
sealed trait ExtensiveNode[InformationSet, Action] {
  def statePartition : StatePartition
}


//----------------------------------------------------------------------------------------------------------------------
sealed trait NonTerminal[InformationSet, Action] {
  def nextToAct : Player
}


//----------------------------------------------------------------------------------------------------------------------
case class Decision[InformationSet, Action](
  nextToAct      : Rational,
  informationSet : InformationSet,
  choices        : Traversable[Action]
) extends ExtensiveNode[InformationSet, Action]
  with NonTerminal[InformationSet, Action] {
  val statePartition = DecisionPartition
}

case object Decision
{
  def apply[InformationSet, Action](
    nextToActIndex : Int,
    informationSet : InformationSet,
    choices        : Traversable[Action])
    : Decision[InformationSet, Action] =
  {
    apply(Rational(nextToActIndex), informationSet, choices)
  }
}



//----------------------------------------------------------------------------------------------------------------------
case class Chance[InformationSet, Action](
  outcomes : Traversable[Outcome[Action]]
) extends ExtensiveNode[InformationSet, Action]
  with NonTerminal[InformationSet, Action] {
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
case class Terminal[InformationSet, Action](
  payoffs : Seq[Double]
) extends ExtensiveNode[InformationSet, Action] {
  val statePartition = TerminalPartition
}

object Terminal {
  def fromPayoffs(payoffs : Seq[Double]): Terminal[_, _]=
    new Terminal(payoffs)
}


//----------------------------------------------------------------------------------------------------------------------
sealed trait ExtensiveStateNode[State, InformationSet, Action]
{
  def state : State
  def node  : ExtensiveNode[InformationSet, Action]
}

case class StateDecision[State, InformationSet, Action](
  state : State,
  node  : Decision[InformationSet, Action]
) extends ExtensiveStateNode[State, InformationSet, Action]

case class StateChance[State, InformationSet, Action](
  state : State,
  node  : Chance[InformationSet, Action]
) extends ExtensiveStateNode[State, InformationSet, Action]

case class StateTerminal[State, InformationSet, Action](
  state : State,
  node  : Terminal[InformationSet, Action]
) extends ExtensiveStateNode[State, InformationSet, Action]
