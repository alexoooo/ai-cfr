package ao.learn.mst.example.kuhn

import org.specs2.mutable.SpecificationWithJUnit
import state.KuhnStake


/**
 * Date: 13/09/11
 * Time: 10:09 PM
 */

class KuhnStakeSpec
    extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------
//  "Both Khun Poker player's stake (amount of money in the pot)"
//    should "Be empty for borh players when first created" in {
//
//  }

  "Newly created Stakes should be empty for both players" in {
    new KuhnStake().firstPlayer === 0
    new KuhnStake().lastPlayer  === 0
  }

  "Placing ante should make both players one in" in {
    new KuhnStake().ante.firstPlayer === 1
    new KuhnStake().ante.lastPlayer  === 1
  }

  "Incrementing first player should only affect first player" in {
    new KuhnStake().incrementFirstPlayer.firstPlayer === 1
    new KuhnStake().incrementFirstPlayer.lastPlayer  === 0
  }

  "Incrementing last player should only affect last player" in {
    new KuhnStake().incrementLastPlayer.lastPlayer  === 1
    new KuhnStake().incrementLastPlayer.firstPlayer === 0
  }
}