package ao.learn.mst.gen5.example.kuhn.card

/**
 * Date: 20/09/11
 * Time: 12:10 PM
 */
object KuhnCard extends Enumeration
{
  type KuhnCard = Value

  val Jack  = Value("J")
  val Queen = Value("Q")
  val King  = Value("K")
}