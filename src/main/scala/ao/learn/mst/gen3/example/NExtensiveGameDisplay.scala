package ao.learn.mst.gen3.example

import ao.learn.mst.cfr._
import ao.learn.mst.gen3.game._
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.NExtensiveAction

/**
 * 21/07/13 6:37 PM
 */
object NExtensiveGameDisplay
{
  //--------------------------------------------------------------------------------------------------------------------
  def displayExtensiveGameNode(node : NExtensiveGameNode[_ <: InformationSet, _ <: NExtensiveAction]) : xml.Node =
    node match {
      case decision : NExtensiveGameDecision[_, _] =>
        <decision player={ decision.player.index.toString } name={ decision.toString }>
          <information-set>
            {
            //              decision.informationSet.getClass.getSimpleName
            decision.informationSet.toString
            }
          </information-set>

          <actions>
            {
              for (action <- decision.actions) yield
                <action name={ action.toString }>
                  {
                    displayExtensiveGameNode(
                      decision.child(action).get)
                  }
                </action>
            }
          </actions>
        </decision>

      case chance : NExtensiveGameChance[_, _] =>
        <chance name={ chance.toString }>
          {
            for (action <- chance.actions) yield
              <outcome
                name={ action.toString }
                probability={ chance.probability(action).toString }>
                {
                  displayExtensiveGameNode(
                    chance.child( action ).get)
                }
              </outcome>
          }
        </chance>

      case terminal : NExtensiveGameTerminal[_, _] =>
        <terminal name={ terminal.toString }>
          { displayExpectedValue( terminal.payoff ) }
        </terminal>
    }


  //--------------------------------------------------------------------------------------------------------------------
  def displayPlayerViewNode(node : PlayerViewNode) : xml.Node =
  {
    node match {
      case terminal : TerminalNode =>
        <terminal>
          { displayExpectedValue( terminal.outcome ) }
        </terminal>

      case opponent : OpponentNode =>
        <opponent>
          { displayPlayerViewKids( opponent.kids ) }
        </opponent>

      case proponent : ProponentNode =>
        <proponent>
          <informationSet>{ proponent.informationSet }</informationSet>
          <averageStrategy>{ proponent.averageStrategy() }</averageStrategy>
          { displayPlayerViewKids( proponent.kids ) }
        </proponent>

      case chance: ChanceNode =>
        <chance>
          { displayPlayerViewKids( chance.kids ) }
        </chance>

      case other =>
        throw new IllegalArgumentException(other.toString)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  def displayExpectedValue(expectedValue : NExpectedValue) : xml.Node =
    <expectedValue>
      {
      for ((utility, player) <- expectedValue.outcomes.zipWithIndex)
      yield
        <outcome player={ player.toString }>
          { utility }
        </outcome>
      }
    </expectedValue>

  def displayExpectedValue(expectedValue : Seq[Double]) : xml.Node =
    <expectedValue>
      {
      for ((utility, player) <- expectedValue.zipWithIndex)
      yield
        <outcome player={ player.toString }>
          { utility }
        </outcome>
      }
    </expectedValue>

  def displayPlayerViewKids(kids : Seq[PlayerViewNode]) : xml.Node =
    <children>
      {
      for ((child, index) <- kids.zipWithIndex)
      yield <child actionIndex={ index.toString }>
        { displayPlayerViewNode(child) }
      </child>
      }
    </children>

}
