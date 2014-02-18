package ao.learn.mst.gen5.example.kuhn.play

import ao.learn.mst.gen5.example.kuhn.action.{KuhnDecision, KuhnPlayerAction}
import KuhnPlayerAction._
import ao.learn.mst.gen5.example.kuhn.state.KuhnState


//----------------------------------------------------------------------------------------------------------------------
trait KuhnPlayer
{
  //--------------------------------------------------------------------------------------------------------------------
  def act(state : KuhnState) : KuhnDecision
}