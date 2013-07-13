package ao.learn.mst.example.kuhn.card

//import KuhnCard._
import util.Random

/**
 * Date: 20/09/11
 * Time: 12:06 AM
 */
class KuhnDeck
{
  //--------------------------------------------------------------------------
  def deal(random : Random) : KuhnCardSequence =
  {
    val cards = KuhnCard.values.toList

    val randomCards = random.shuffle( cards ).tail

    KuhnCardSequence(
      randomCards(0),
      randomCards(1))
  }
}