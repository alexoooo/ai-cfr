package ao.learn.mst.gen5.solve2

case class StrategyRegretSample(
  strategy: Map[Long, Seq[Double]],
  regret: Map[Long, Seq[Double]])
