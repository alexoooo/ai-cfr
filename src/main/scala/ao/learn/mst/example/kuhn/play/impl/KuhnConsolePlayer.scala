package ao.learn.mst.example.kuhn.play.impl

import ao.learn.mst.example.kuhn.play.KuhnPlayer
import ao.learn.mst.example.kuhn.state.KuhnState
import ao.learn.mst.example.kuhn.action.KuhnAction._

/**
 * Date: 14/11/11
 * Time: 2:14 PM
 */

class KuhnConsolePlayer
    extends KuhnPlayer
{
  //--------------------------------------------------------------------------------------------------------------------
  def act(state: KuhnState) : KuhnAction =
  {
    println(state)
    selectAction(state)
  }


  private def selectAction(state: KuhnState) : KuhnAction =
  {
    try {
      trySelectAction(state)
    } catch {
      case _ : NumberFormatException | _ : NoSuchElementException =>
        selectAction(state)
    }
  }

  private def trySelectAction(state: KuhnState) : KuhnAction =
  {
    val actions = state.availableActions

    val actionIndexes : Map[Int, KuhnAction] =
      (1 to actions.length).map(i => (i, actions(i - 1))).toMap

    val actionChoices : Seq[String] =
      actionIndexes.toList.map(e => e._1 + ") " + e._2)

    println("Choose one of: " + actionChoices.mkString(", "))
    val selectedActionIndex = readInt()

    actionIndexes( selectedActionIndex )
  }
}