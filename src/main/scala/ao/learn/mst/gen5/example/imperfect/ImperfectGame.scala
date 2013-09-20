package ao.learn.mst.gen5.example.imperfect

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Terminal, Decision, ExtensiveNode}


//----------------------------------------------------------------------------------------------------------------------
object ImperfectGame
    extends
      ExtensiveGame[
        ImperfectState,
        ImperfectInformationSet,
        ImperfectAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def playerCount: Int =
    2


  //--------------------------------------------------------------------------------------------------------------------
  def initialState: ImperfectState =
    ImperfectState(Seq.empty)


  //--------------------------------------------------------------------------------------------------------------------
  def transition(nonTerminal: ImperfectState, action: ImperfectAction): ImperfectState =
    ImperfectState(nonTerminal.actions :+ action)


  //--------------------------------------------------------------------------------------------------------------------
  def node(state: ImperfectState): ExtensiveNode[ImperfectInformationSet, ImperfectAction] = {
    val actionValues : Seq[Boolean] =
      Seq(false, true)

    state.actions.toList match {
      case Nil =>
        Decision(
          0,
          ImperfectPlayerOneInfo,
          actionValues.map(
            ImperfectPlayerOneAction))

      case List(firstAction) =>
        Decision(
          1,
          ImperfectPlayerTwoInfo,
          actionValues.map(
            ImperfectPlayerTwoAction))

      case List(firstAction, secondAction) =>
        Terminal(
          (firstAction.value, secondAction.value) match {
            case (true , true ) => Seq(0, 0)
            case (true , false) => Seq(2, 1)
            case (false, true ) => Seq(1, 2)
            case (false, false) => Seq(3, 1)
          })
    }
  }
}
