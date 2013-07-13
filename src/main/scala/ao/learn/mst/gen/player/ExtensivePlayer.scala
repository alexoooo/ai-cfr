package ao.learn.mst.gen.player

import ao.learn.mst.gen.ExtensiveGameNodeX
import ao.learn.mst.gen.act.ExtensiveAction

/**
 * Date: 15/11/11
 * Time: 12:05 AM
 */

trait ExtensivePlayer
{
  //--------------------------------------------------------------------------------------------------------------------
  def act(state : ExtensiveGameNodeX) : ExtensiveAction
}