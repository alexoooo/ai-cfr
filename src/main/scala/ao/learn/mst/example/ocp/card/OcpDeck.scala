package ao.learn.mst.example.ocp.card

import util.Random
import ao.learn.mst.example.ocp.card.OcpCard._

/**
 * Date: 20/09/11
 * Time: 12:06 AM
 */
class OcpDeck {
  //--------------------------------------------------------------------------
  def deal(random: Random): OcpCardSequence = {
    val cards : List[OcpCard] =
      OcpCard.values.toList

    val randomCards : List[OcpCard] =
      random.shuffle(cards).tail

    OcpCardSequence(
      randomCards(0),
      randomCards(1))
  }
}