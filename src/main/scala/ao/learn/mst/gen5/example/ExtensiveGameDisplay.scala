package ao.learn.mst.gen5.example

import ao.learn.mst.gen5.node.{Terminal, Chance, Decision, ExtensiveNode}
import ao.learn.mst.gen5.ExtensiveGame


/**
 * 13/02/14 11:36 PM
 */
object ExtensiveGameDisplay
{
  //--------------------------------------------------------------------------------------------------------------------
  def displayExtensiveGameNode[S, I, A](game: ExtensiveGame[S, I, A], state: S) : xml.Node =
    game.node(state) match {
      case decision: Decision[I, A] =>
        <decision player={ decision.nextToAct.index.toString } info={ decision.informationSet.toString }>
          {
            for (choice <- decision.choices) yield
            <action name={ choice.toString }>
              {
                val nextState: S =
                  game.transition(state, choice)

                displayExtensiveGameNode(game, nextState)
              }
            </action>
          }
        </decision>

      case chance: Chance[I, A] =>
        <chance>
          {
            for (outcome <- chance.outcomes) yield
            <outcome name={ outcome.action.toString } probability={ outcome.probability.toString }>
              {
                val nextState: S =
                  game.transition(state, outcome.action)

                displayExtensiveGameNode(game, nextState)
              }
            </outcome>
          }
        </chance>

      case terminal: Terminal[I, A] =>
        <terminal payoffs={ terminal.payoffs.mkString(", ") }/>
    }


  //--------------------------------------------------------------------------------------------------------------------
//  def displayPlayerViewNode(
//                             node : PlayerViewNode) : xml.Node =
//  {
//    node match {
//      case terminal : TerminalNode =>
//        <terminal>
//          { displayExpectedValue( terminal.outcome ) }
//        </terminal>
//
//      case opponent : OpponentNode =>
//        <opponent>
//          { displayPlayerViewKids( opponent.kids ) }
//        </opponent>
//
//      case proponent : ProponentNode =>
//        <proponent>
//          <informationSet>{ proponent.informationSet }</informationSet>
//          <averageStrategy>{ proponent.averageStrategy() }</averageStrategy>
//          { displayPlayerViewKids( proponent.kids ) }
//        </proponent>
//
//      case chance: ChanceNode =>
//        <chance>
//          { displayPlayerViewKids( chance.kids ) }
//        </chance>
//
//      case other =>
//        throw new IllegalArgumentException(other.toString)
//    }
//  }


  //--------------------------------------------------------------------------------------------------------------------
//  def displayExpectedValue(
//                            expectedValue : ExpectedValue) : xml.Node =
//    <expectedValue>
//      {
//      for ((player, utility) <- expectedValue.outcomes)
//      yield
//        <outcome player={ player.index.toString }>
//          { utility }
//        </outcome>
//      }
//    </expectedValue>

//  def displayExpectedValue(expectedValue : Seq[Double]) : xml.Node =
//    <expectedValue>
//      {
//      for ((utility, player) <- expectedValue.zipWithIndex)
//      yield
//        <outcome player={ player.toString }>
//          { utility }
//        </outcome>
//      }
//    </expectedValue>

//  def displayPlayerViewKids(
//                             kids : Seq[PlayerViewNode]) : xml.Node =
//    <children>
//      {
//      for ((child, index) <- kids.zipWithIndex)
//      yield <child actionIndex={ index.toString }>
//        { displayPlayerViewNode(child) }
//      </child>
//      }
//    </children>


  //  def displayExpectedValue(
  //                            expectedValue : ExpectedValue) : xml.Node =
  //  {
  //
  //  }
}
