package ao.learn.mst.gen2.example.kuhn

import ao.learn.mst.gen2.example.kuhn.card.KuhnDeck
import util.Random
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.ScalaCheck

/**
 * Date: 20/09/11
 * Time: 12:03 AM
 */

class KuhnDeckSpec
    extends SpecificationWithJUnit with ScalaCheck
{
  //--------------------------------------------------------------------------
  "Two distinct cards should be deals from any starting point" ! check {
    (randomSeed : Int) => {
      val randomSource =
        new Random(randomSeed)

      val cardSequence =
        KuhnDeck.deal(randomSource)

      cardSequence.first mustNotEqual
        cardSequence.last
    }
  }
}