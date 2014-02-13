package ao.learn.mst.gen5.example.stochastic


sealed trait CoinFlipDeterministicAction

case class CoinFlipDeterministicChance(value: Boolean) extends CoinFlipDeterministicAction
case class CoinFlipDeterministicDecision(value: Int) extends CoinFlipDeterministicAction