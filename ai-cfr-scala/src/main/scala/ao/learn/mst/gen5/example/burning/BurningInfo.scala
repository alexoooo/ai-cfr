package ao.learn.mst.gen5.example.burning


sealed trait BurningInfo


case object BurningInitialDecisionInfo extends BurningInfo

case class BurningRowDecisionInfo   (burned:Boolean) extends BurningInfo
case class BurningColumnDecisionInfo(burned:Boolean) extends BurningInfo


