package ao.learn.mst.gen2.example.kuhn.play.impl

import ao.learn.mst.gen2.example.kuhn.play.KuhnPlayer
import ao.learn.mst.gen2.example.kuhn.state.KuhnState
import ao.learn.mst.gen2.example.kuhn.action.KuhnPlayerAction._
import ao.learn.mst.gen2.example.kuhn.action.KuhnDecision

/**
 * Date: 14/11/11
 * Time: 2:14 PM
 */

class KuhnConsolePlayer
    extends KuhnPlayer
{
  //--------------------------------------------------------------------------------------------------------------------
  def act(state: KuhnState) : KuhnDecision =
  {
    println(state)
    selectAction(state)
  }


  private def selectAction(state: KuhnState) : KuhnDecision =
  {
    try {
      trySelectAction(state)
    } catch {
      case _ : NumberFormatException | _ : NoSuchElementException =>
        selectAction(state)
    }
  }

  private def trySelectAction(state: KuhnState) : KuhnDecision =
  {
    val actions = state.availableActions

    val actionIndexes : Map[Int, KuhnDecision] =
      (1 to actions.length).map(i => (i, actions(i - 1))).toMap

    val actionChoices : Seq[String] =
      actionIndexes.toList.map(e => e._1 + ") " + e._2)

    println("Choose one of: " + actionChoices.mkString(", "))
    val selectedActionIndex = readInt()

    actionIndexes( selectedActionIndex )
  }
}