package ao.learn.mst.gen5.example.monty

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Decision, Outcome, Chance, ExtensiveNode}


object MontyHallGame
  extends
    ExtensiveGame[
      MontyHallState,
      MontyHallInfo,
      MontyHallAction]
{
  def playerCount: Int =
    1

  def initialState: MontyHallState =
    MontyHallState(Seq.empty)

  def transition(nonTerminal: MontyHallState, action: MontyHallAction): MontyHallState =
    MontyHallState(nonTerminal.actions :+ action)

  def node(state: MontyHallState): ExtensiveNode[MontyHallInfo, MontyHallAction] = {
    val doors : Seq[Int] =
      0 to  3

    state.actions.toList match {
      case Nil => {
        val possibilities : Seq[MontyPlacePrize] =
          doors.map(
            MontyPlacePrize)

        Chance(Outcome.equalProbability(possibilities))
      }

      case List(MontyPlacePrize(p)) => {
        val choices : Seq[MontyPickDoor] =
          doors.map(
            MontyPickDoor)

        Decision(0, MontyPickDoorInfo, choices)
      }

      case List(MontyPlacePrize(p), MontyPickDoor(d)) => {
        val possibilities : Seq[MontyRevealNoPrize] =
          doors.filter(
            ! Set(p, d).contains(_)
          ).map(MontyRevealNoPrize)

        Chance(Outcome.equalProbability(possibilities))
      }

      case List(MontyPlacePrize(p), MontyPickDoor(d), MontyRevealNoPrize(n)) => {
        val choices : Seq[MontyKeepOrSwitch] =
          Seq(false, true).map(
            MontyKeepOrSwitch)

        Decision(0, MontySwitchDoorInfo(d, n), choices)
      }

      case List(MontyPlacePrize(p), MontyPickDoor(d), MontyRevealNoPrize(n), MontyKeepOrSwitch(s)) => {
        ???
      }
    }
  }
}
