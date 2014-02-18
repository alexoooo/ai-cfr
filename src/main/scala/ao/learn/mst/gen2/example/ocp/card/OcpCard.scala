package ao.learn.mst.gen2.example.ocp.card

/**
 * Date: 20/09/11
 * Time: 12:10 PM
 */
object OcpCard extends Enumeration
{
  type OcpCard = Value

  val Two   = Value("2")
  val Three = Value("3")
  val Four  = Value("4")
  val Five  = Value("5")
  val Six   = Value("6")
  val Seven = Value("7")
  val Eight = Value("8")
  val Nine  = Value("9")
  val Ten   = Value("T")
  val Jack  = Value("J")
  val Queen = Value("Q")
  val King  = Value("K")
  val Ace   = Value("A")
}