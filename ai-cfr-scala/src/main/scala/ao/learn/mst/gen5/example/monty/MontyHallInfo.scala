package ao.learn.mst.gen5.example.monty


sealed trait MontyHallInfo


case object MontyPickDoorInfo extends MontyHallInfo

case class MontySwitchDoorInfo(
  selectedDoor : Int,
  revealedLosingDoor : Int) extends MontyHallInfo


