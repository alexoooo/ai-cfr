package ao.learn.mst.gen5.example.bandit.rps



sealed trait RpsAction


case object RpsRock     extends RpsAction
case object RpsPaper    extends RpsAction
case object RpsScissors extends RpsAction



