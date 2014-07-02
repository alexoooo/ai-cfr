package ao.learn.mst.gen5.br

import ao.learn.mst.gen5.ExtensivePlayer


/**
 *
 */
case class BestResponsePlayer[I, A](
    profile: BestResponseProfile[I, A])
  extends ExtensivePlayer[I, A]
{
  def act(informationSet: I, actions: Traversable[A]): A =
  {
    val relevantResponses : Seq[BestResponse[I, A]] =
      profile.bestResponses.filter(
        _.strategy.contains(informationSet))

    val actingResponse : BestResponse[I, A] =
      relevantResponses(0)

    actingResponse.strategy(informationSet)
  }
}
