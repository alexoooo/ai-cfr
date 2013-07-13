package ao.learn.mst.example.kuhn.action

/**
 * Date: 25/10/11
 * Time: 11:05 PM
 */

//----------------------------------------------------------------------------
object KuhnAction extends Enumeration
{
  //--------------------------------------------------------------------------
  type KuhnAction = Value

  val CheckFold = Value("Check/Fold")
  val CallRaise = Value("Call/Raise")
}