package ao.learn.mst.gen2.example.kuhn.card

import util.Random
import ao.learn.mst.gen2.example.kuhn.card.KuhnCard._
import ao.learn.mst.gen2.example.kuhn.action.KuhnCardSequence

/**
 * Date: 20/09/11
 * Time: 12:06 AM
 */
object KuhnDeck
{
  //--------------------------------------------------------------------------
  def deal(random : Random) : KuhnCardSequence = {
    permutations(random.nextInt(permutations.length))
  }


  val permutations : Seq[KuhnCardSequence] = {
    val playerCardCombinations : Iterator[Seq[KuhnCard]] =
      KuhnCard.values.toSeq.combinations(2)

    val playerHands : Iterator[Seq[KuhnCard]] =
      playerCardCombinations.flatMap(_.permutations)

    Seq() ++ playerHands.map(p =>
      KuhnCardSequence(p(0), p(1)))
  }
}