package ao.learn.mst.gen5.example.burning

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Terminal, Decision, ExtensiveNode}

/**
 * http://en.wikipedia.org/wiki/Battle_of_the_sexes_(game_theory)#Burning_money
 */
case object BurningGame
  extends ExtensiveGame[BurningState, BurningInfo, BurningAction]
{
  def playerCount: Int =
    2

  def initialState: BurningState =
    BurningState(Seq.empty)

  def transition(nonTerminal: BurningState, action: BurningAction): BurningState =
    BurningState(nonTerminal.actions :+ action)

  private val choices : Seq[Boolean] =
    Seq(false, true)

  def node(state: BurningState): ExtensiveNode[BurningInfo, BurningAction] =
    state.actions.toList match {
      case Nil =>
        val actions =
          choices.map(
            BurningBurnAction)

        Decision(0, BurningInitialDecisionInfo, actions)

      case List(BurningBurnAction(burn)) =>
        val actions =
          choices.map(
            BurningRowAction)

        Decision(0, BurningRowDecisionInfo(burn), actions)

      case List(BurningBurnAction(burn), BurningRowAction(_)) =>
        val actions =
          choices.map(
            BurningColumnAction)

        Decision(0, BurningColumnDecisionInfo(burn), actions)

      case List(BurningBurnAction(burn), BurningRowAction(r), BurningColumnAction(c)) =>
        Terminal(
          if (! burn) {
            (r, c) match {
              case (false, false) => Seq(4, 1)
              case (false, true ) => Seq(0, 0)
              case (true , false) => Seq(0, 0)
              case (true , true ) => Seq(1, 4)
            }
          } else {
            (r, c) match {
              case (false, false) => Seq( 2, 1)
              case (false, true ) => Seq(-2, 0)
              case (true , false) => Seq(-2, 0)
              case (true , true ) => Seq(-1, 4)
            }
          }
        )
    }
}
