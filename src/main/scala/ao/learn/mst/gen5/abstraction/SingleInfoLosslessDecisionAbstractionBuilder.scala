package ao.learn.mst.gen5.example.abstraction

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen5.node.{Decision, ExtensiveNode}


//----------------------------------------------------------------------------------------------------------------------
/**
 * All information sets are indistinguishable, but actions are retained exactly.
 */
case object SingleInfoLosslessDecisionAbstractionBuilder
  extends OpaqueAbstractionBuilder
{
  //--------------------------------------------------------------------------------------------------------------------
  def generate[State, InformationSet, Action](
    game: ExtensiveGame[State, InformationSet, Action]
    ): ExtensiveAbstraction[InformationSet, Action] =
  {
    val uniqueNonTrivialChoices : Set[Action] =
      AbstractionUtils.uniqueNonTrivialChoices(game)

    val nonTrivialChoiceIndex : Map[Action, Int] =
      uniqueNonTrivialChoices.toSeq.zipWithIndex.toMap

    new SingleInfoLosslessActionAbstraction(
      nonTrivialChoiceIndex)
  }


  //--------------------------------------------------------------------------------------------------------------------
  private class SingleInfoLosslessActionAbstraction[InformationSet, Action](
    nonTrivialChoiceIndex : Map[Action, Int])
    extends ExtensiveAbstraction[InformationSet, Action]
  {
    private val trivialChoiceIndex : Int =
      nonTrivialChoiceIndex.size

    def informationSetIndex(informationSet: InformationSet): Int =
      0

    def actionSubIndex(informationSet: InformationSet, action: Action): Int =
      actionIndex(action)

    def actionIndex(action: Action): Int = {
      if (nonTrivialChoiceIndex.contains(action)) {
        nonTrivialChoiceIndex(action)
      } else {
        trivialChoiceIndex
      }
    }

    def actionCount(informationSet: InformationSet): Int =
      nonTrivialChoiceIndex.size
  }
}
