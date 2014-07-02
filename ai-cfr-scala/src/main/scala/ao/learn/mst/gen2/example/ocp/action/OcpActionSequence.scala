package ao.learn.mst.gen2.example.ocp.action

//----------------------------------------------------------------------------
object OcpActionSequence extends Enumeration
{
  //--------------------------------------------------------------------------
  type OcpActionSequence = Value

  val FirstAction = Value("FirstAction")

  val Check = Value("Check")
  val CheckCheck = Value("Check / Check")
  val CheckRaise = Value("Check / Raise")
  val CheckRaiseFold = Value("Check / Raise / Fold")
  val CheckRaiseCall = Value("Check / Raise / Call")

  val Raise = Value("Raise")
  val RaiseFold = Value("Raise / Fold")
  val RaiseCall = Value("Raise / Call")
}