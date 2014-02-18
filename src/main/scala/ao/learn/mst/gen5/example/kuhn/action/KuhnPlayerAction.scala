package ao.learn.mst.gen5.example.kuhn.action

/**
 * Date: 25/10/11
 * Time: 11:05 PM
 */

//----------------------------------------------------------------------------
object KuhnPlayerAction extends Enumeration
{
  //--------------------------------------------------------------------------
  type KuhnPlayerAction = Value

  val CheckFold = Value("Check/Fold")
  val CallRaise = Value("Call/Raise")
}