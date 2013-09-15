package ao.learn.mst.gen5.example.simple.rps



sealed trait RpsAction


case object Rock     extends RpsAction
case object Paper    extends RpsAction
case object Scissors extends RpsAction



