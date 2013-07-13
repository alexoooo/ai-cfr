package ao.learn.mst.example.ocp.action

/**
 * Date: 25/10/11
 * Time: 11:05 PM
 */

//----------------------------------------------------------------------------
object OcpAction extends Enumeration
{
  //--------------------------------------------------------------------------
  type OcpAction = Value

  val CheckFold = Value("Check/Fold")
  val CallRaise = Value("Call/Raise")
}