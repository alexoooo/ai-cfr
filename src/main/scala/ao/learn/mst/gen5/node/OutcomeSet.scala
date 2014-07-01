package ao.learn.mst.gen5.node

import scala.util.Random
import scala.annotation.tailrec

/**
 *
 */
sealed trait OutcomeSet[Action]
{
  def actions: Traversable[Action]

  def probability(action: Action): Double

  def sample(sourceOfRandomness: Random): Action
}


case class MapOutcomeSet[Action](
    actionProbabilities: Map[Action, Double])
    extends OutcomeSet[Action]
{
  override def actions =
    actionProbabilities.keys

  override def probability(action: Action) =
    actionProbabilities(action)

  override def sample(sourceOfRandomness: Random): Action =
    actionProbabilities
      .mapValues(p => p * sourceOfRandomness.nextDouble())
      .maxBy(_._2)
      ._1
}


case class UniformOutcomeSet[Action](
    actions: Seq[Action])
    extends OutcomeSet[Action]
{
  val count: Int =
    actions.size

  private val uniformProbability: Double =
    1.0 / count

  override def probability(action: Action): Double =
    uniformProbability


  override def sample(sourceOfRandomness: Random): Action = {
    val index: Int =
      sourceOfRandomness.nextInt(count)

    actions(index)
  }
}



// See: http://en.wikipedia.org/wiki/Combination
case class CombinationOutcomeSet[Choice, Action](
    choices: Seq[Choice],
    selectionAction: Seq[Choice] => Action,
    size: Int)
    extends OutcomeSet[Action]
{
  override def actions: Traversable[Action] =
    new Traversable[Action] {
      override def foreach[U](f: (Action) => U): Unit =
        choices.combinations(size).map(selectionAction).foreach(f)
    }

  override def probability(action: Action) =
    1.0 / count

  override def sample(sourceOfRandomness: Random): Action = {
    val selection: Seq[Choice] =
      sample(sourceOfRandomness, choices.length, size, Nil)

    selectionAction(selection)
  }

  @tailrec
  private def sample(
      sourceOfRandomness: Random,
      upperLimitExclusive: Int,
      remaining: Int,
      buffer: List[Choice])
      : List[Choice] =
  {
    if (remaining == 0) {
      buffer
    } else {
      val nextSample: Int =
        sourceOfRandomness.nextInt(upperLimitExclusive - remaining) + remaining

      sample(
        sourceOfRandomness,
        nextSample,
        remaining - 1,
        choices(nextSample) :: buffer
      )
    }
  }


  def count: Long = {
    val numerator: Long =
      Range.inclusive(choices.length - size, choices.length)
        .foldLeft(1L)((a: Long, n: Int) => a * n)

    val denominator: Long =
      Range.inclusive(2, size)
        .foldLeft(1L)((a: Long, n: Int) => a * n)

    numerator / denominator
  }
}