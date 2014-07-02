package ao.learn.mst.gen5.example.monty

/**
 * Doesn't distinguish between initial door choice or which door was revealed.
 */

sealed trait BasicMontyHallInfo

object BasicMontyHallInfo
{
  def fromMontyHallInfo(montyHallInfo: MontyHallInfo) : BasicMontyHallInfo = {
    montyHallInfo match {
      case MontyPickDoorInfo =>
        BasicMontyPickDoorInfo

      case MontySwitchDoorInfo(_, _) =>
        BasicMontySwitchDoorInfo
    }
  }
}


case object BasicMontyPickDoorInfo   extends BasicMontyHallInfo

case object BasicMontySwitchDoorInfo extends BasicMontyHallInfo


