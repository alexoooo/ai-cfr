package ao.learn.mst.gen5.example.bandit.deterministic

import ao.learn.mst.gen5.example.bandit.BinaryBanditGame


//----------------------------------------------------------------------------------------------------------------------
case class DeterministicBinaryBanditGame(
    falsePayoff : Double,
    truePayoff  : Double)
  extends BinaryBanditGame
{
  //--------------------------------------------------------------------------------------------------------------------
  def payoff(choice: Boolean): Double =
    choice match {
      case false => falsePayoff
      case true  => truePayoff
    }
}


object DeterministicBinaryBanditGame
{
  val plusMinusOne =
    DeterministicBinaryBanditGame(
      -1, 1)
}