package ao.learn.mst.gen5.example.kuhn.action

//----------------------------------------------------------------------------
object KuhnActionSequence extends Enumeration
{
  //--------------------------------------------------------------------------
  type KuhnActionSequence = Value

  val Empty = Value("FirstAction")

    val Check = Value("Check")
      val CheckCheck = Value("Check / Check")
      val CheckRaise = Value("Check / Raise")
      val CheckRaiseFold = Value("Check / Raise / Fold")
      val CheckRaiseCall = Value("Check / Raise / Call")

    val Raise = Value("Raise")
      val RaiseFold = Value("Raise / Fold")
      val RaiseCall = Value("Raise / Call")
}