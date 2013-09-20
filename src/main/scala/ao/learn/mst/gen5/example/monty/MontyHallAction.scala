package ao.learn.mst.gen5.example.monty


sealed trait MontyHallAction



case class MontyPlacePrize(door : Int) extends MontyHallAction


case class MontyPickDoor(door : Int) extends MontyHallAction


case class MontyRevealNoPrize(door : Int) extends MontyHallAction


case class MontyKeepOrSwitch(
  switch : Boolean) extends MontyHallAction
