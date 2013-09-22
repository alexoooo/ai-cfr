package ao.learn.mst.gen5.example.monty

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.node.Chance


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
      0 until  3

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
        val choices : Seq[MontySwitch] =
          Seq(false, true).map(
            MontySwitch)

        Decision(0, MontySwitchDoorInfo(d, n), choices)
      }

      case List(MontyPlacePrize(p), MontyPickDoor(d), MontyRevealNoPrize(n), MontySwitch(s)) => {
        val selectedDoor : Int =
          if (! s) {
            d
          } else {
            doors.filter(
              ! Set(d, n).contains(_))(0)
          }

        val victory : Boolean =
          selectedDoor == p

        Terminal(
          Seq(if (victory) 1 else 0))
      }
    }
  }
}
