package ao.learn.mst.example

import ao.learn.mst.gen2.game.{ExtensiveGameTerminal, ExtensiveGameChance, ExtensiveGameDecision, ExtensiveGameNode}
import ao.learn.mst.gen2.cfr._
import ao.learn.mst.gen2.solve.ExpectedValue

/**
 *
 */
object ExtensiveGameDisplay
{
  //--------------------------------------------------------------------------------------------------------------------
  def displayExtensiveGameNode(
      node : ExtensiveGameNode) : xml.Node =
    node match {
      case decision : ExtensiveGameDecision =>
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
              <action index={ action.index.toString } name={ action.toString }>
                { displayExtensiveGameNode(
                decision.child(action)) }
              </action>
            }
          </actions>
        </decision>

      case chance : ExtensiveGameChance =>
        <chance name={ chance.toString }>
          {
          for (action <- chance.probabilityMass.actionProbabilities) yield
            <outcome
            index={ action._1.index.toString }
            name={ action._1.toString }
            probability={ action._2.toString }>
              {
              displayExtensiveGameNode(
                chance.child( action._1 ))
              }
            </outcome>
          }
        </chance>

      case terminal : ExtensiveGameTerminal =>
        <terminal name={ terminal.toString }>
          { displayExpectedValue( terminal.payoff ) }
        </terminal>
    }


  //--------------------------------------------------------------------------------------------------------------------
  def displayPlayerViewNode(
                             node : PlayerViewNode) : xml.Node =
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
  def displayExpectedValue(
                            expectedValue : ExpectedValue) : xml.Node =
    <expectedValue>
      {
      for ((player, utility) <- expectedValue.outcomes)
      yield
        <outcome player={ player.index.toString }>
          { utility }
        </outcome>
      }
    </expectedValue>

  def displayExpectedValue(
                            expectedValue : Seq[Double]) : xml.Node =
    <expectedValue>
      {
      for ((utility, player) <- expectedValue.zipWithIndex)
      yield
        <outcome player={ player.toString }>
          { utility }
        </outcome>
      }
    </expectedValue>

  def displayPlayerViewKids(
                             kids : Seq[PlayerViewNode]) : xml.Node =
    <children>
      {
      for ((child, index) <- kids.zipWithIndex)
      yield <child actionIndex={ index.toString }>
        { displayPlayerViewNode(child) }
      </child>
      }
    </children>


  //  def displayExpectedValue(
  //                            expectedValue : ExpectedValue) : xml.Node =
  //  {
  //
  //  }
}
