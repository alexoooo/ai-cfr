package ao.learn.mst.gen5.example.simple.rps


case class RpsState(
  choices : Seq[RpsAction])

//case class RpsDecision(playerIndex        : Int) extends RpsState
//case class RpsTerminal(winningPlayerIndex : Int) extends RpsState
//
//
//class