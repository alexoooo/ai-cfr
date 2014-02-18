package ao.learn.mst.gen5.example.kuhn.state

/**
 * Date: 25/10/11
 * Time: 11:29 PM
 */

object KuhnPosition extends Enumeration
{
  //--------------------------------------------------------------------------
  type KuhnPosition = Value

  val FirstToAct = Value("FirstToAct")
  val LastToAct  = Value("LastToAct")
}