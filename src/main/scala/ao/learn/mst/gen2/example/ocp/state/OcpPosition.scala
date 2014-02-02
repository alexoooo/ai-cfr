package ao.learn.mst.gen2.example.ocp.state

/**
 * Date: 25/10/11
 * Time: 11:29 PM
 */

object OcpPosition extends Enumeration
{
  //--------------------------------------------------------------------------
  type OcpPosition = Value

  val FirstToAct = Value("FirstToAct")
  val LastToAct = Value("LastToAct")
}