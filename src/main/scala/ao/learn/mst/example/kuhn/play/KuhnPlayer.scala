package ao.learn.mst.example.kuhn.play

import ao.learn.mst.example.kuhn.action.KuhnAction
import KuhnAction._
import ao.learn.mst.example.kuhn.state.KuhnState


//----------------------------------------------------------------------------------------------------------------------
trait KuhnPlayer
{
  //--------------------------------------------------------------------------------------------------------------------
  def act(state : KuhnState) : KuhnAction
}