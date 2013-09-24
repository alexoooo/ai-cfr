package ao.learn.mst.gen5.example.burning


sealed trait BurningAction


case class BurningBurnAction(burn : Boolean) extends BurningAction

case class BurningRowAction   (value : Boolean) extends BurningAction
case class BurningColumnAction(value : Boolean) extends BurningAction


