package ao.learn.mst.example.kuhn.play.impl

import ao.learn.mst.example.kuhn.play.KuhnPlayer
import ao.learn.mst.example.kuhn.state.KuhnState
import ao.learn.mst.example.kuhn.action.KuhnAction
import util.Random

/**
 * Date: 14/11/11
 * Time: 2:05 PM
 */

class KuhnRandomPlayer extends KuhnPlayer
{
  //--------------------------------------------------------------------------------------------------------------------
  val rand = new Random()

  //--------------------------------------------------------------------------------------------------------------------
  def act(state: KuhnState) = {
    KuhnAction.values.toList(
      rand.nextInt(2))
  }
}