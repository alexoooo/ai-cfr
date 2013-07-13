package ao.learn.mst.example.ocp.card

import util.Random

/**
 * Date: 20/09/11
 * Time: 12:06 AM
 */
class OcpDeck {
  //--------------------------------------------------------------------------
  def deal(random: Random): OcpCardSequence = {
    val cards = OcpCard.values.toList

    val randomCards = random.shuffle(cards).tail

    OcpCardSequence(
      randomCards(0),
      randomCards(1))
  }
}