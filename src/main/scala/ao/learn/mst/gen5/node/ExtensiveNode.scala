package ao.learn.mst.gen5.node

import ao.learn.mst.gen4.{Nature, Rational, Player}


//----------------------------------------------------------------------------------------------------------------------
sealed trait ExtensiveNode[InformationSet, Action]


//----------------------------------------------------------------------------------------------------------------------
trait NonTerminal[InformationSet, Action]
  extends ExtensiveNode[InformationSet, Action]
{
  def nextToAct : Player
}


//----------------------------------------------------------------------------------------------------------------------
case class Decision[InformationSet, Action](
  nextToAct      : Rational,
  choices        : Traversable[Action],
  informationSet : InformationSet
) extends NonTerminal[InformationSet, Action]


//----------------------------------------------------------------------------------------------------------------------
case class Chance[InformationSet, Action](
  outcomes : Traversable[Outcome[Action]]
) extends NonTerminal[InformationSet, Action] {
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
) extends ExtensiveNode[InformationSet, Action]

object Terminal {
  def fromPayoffs(payoffs : Seq[Double]): Terminal[_, _]=
    new Terminal(payoffs)
}
