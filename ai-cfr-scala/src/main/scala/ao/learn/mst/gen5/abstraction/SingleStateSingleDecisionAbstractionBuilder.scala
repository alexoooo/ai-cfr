package ao.learn.mst.gen5.example.abstraction

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}


//----------------------------------------------------------------------------------------------------------------------
/**
 * Singleton information set where all information sets and all actions appear identical.
 */
case object SingleStateSingleDecisionAbstractionBuilder
  extends OpaqueAbstractionBuilder
{
  //--------------------------------------------------------------------------------------------------------------------
  def generate[State, InformationSet, Action](
    game: ExtensiveGame[State, InformationSet, Action]
    ): ExtensiveAbstraction[InformationSet, Action] =
  {
    new SingleStateSingleActionAbstraction()
  }


  //--------------------------------------------------------------------------------------------------------------------
  private class SingleStateSingleActionAbstraction[InformationSet, Action]
    extends ExtensiveAbstraction[InformationSet, Action]
  {
    def informationSetIndex(informationSet: InformationSet): Int =
      0

    def actionIndex(action: Action): Int =
      0

    def actionSubIndex(informationSet: InformationSet, action: Action): Int =
      0

    def actionCount(informationSet: InformationSet): Int =
      1
  }
}
