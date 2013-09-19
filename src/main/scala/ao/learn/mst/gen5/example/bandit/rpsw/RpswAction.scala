package ao.learn.mst.gen5.example.bandit.rpsw



sealed trait RpswAction


case object RpswRock     extends RpswAction
case object RpswPaper    extends RpswAction
case object RpswScissors extends RpswAction
case object RpswWell     extends RpswAction
