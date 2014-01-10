package ao.learn.mst.gen5.br

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.node.Chance
import ao.learn.mst.gen5.br.ResponseTreeLeaf
import scala.Some
import ao.learn.mst.gen4.Rational

/**
 * 27/10/13 5:45 PM
 */
class ResponseTreeTraverserNew
{
//  //--------------------------------------------------------------------------------------------------------------------
//  def traverseResponseTreeLeaves[S, I, A](
//      game          : ExtensiveGame[S, I, A],
//      abstraction   : ExtensiveAbstraction[I, A],
//      mixedStrategy : ExtensiveStrategyProfile,
//      player        : Int)
//    : Traversable[ResponseTreeLeaf[S, I, A]] =
//  {
//    new Traversable[ResponseTreeLeaf[S, I, A]] {
//      def foreach[U](visitor: (ResponseTreeLeaf[S, I, A]) => U): Unit =
//      {
//        traverse(
//          game.initialStateNode,
//          Path(1.0, None, None),
//          Context(game, abstraction, mixedStrategy, player, visitor))
//      }
//    }
//  }
//
//
//  //--------------------------------------------------------------------------------------------------------------------
//  private def traverse[S, I, A, U](
//      info    : I,
//      path    : Path[I, A],
//      context : Context[S, I, A, U])
//    : Unit =
//  {
//
//
//
//    stateNode.node match {
//      case Terminal(payoffs) =>
//        context.visitor(ResponseTreeLeaf(
//          path.reachProbability,
//          payoffs(context.player),
//          stateNode.state,
//          path.previousInfo.get,
//          path.previousAction.get))
//
//      case Chance(outcomes) =>
//        outcomes.foreach((outcome: Outcome[A]) =>
//          traverse(
//            context.game.transitionStateNode(stateNode, outcome.action),
//            path.discountProbability(outcome.probability),
//            context)
//        )
//
//      case Decision(Rational(`context`.player), info, choices) =>
//        choices.foreach((choice: A) =>
//          traverse(
//            context.game.transitionStateNode(stateNode, choice),
//            path.choose(info, choice),
//            context)
//        )
//
//      case Decision(Rational(opponent), info, choices) =>
//        choices.foreach((choice: A) => {
//          val probability : Double =
//            context.mixedStrategy.actionProbability(
//              context.abstraction, info, choice)
//
//          traverse(
//            context.game.transitionStateNode(stateNode, choice),
//            path.discountProbability(probability),
//            context)
//        })
//    }
//  }
//
//
//  private case class Path[I, A](
//    reachProbability : Double,
//    previousInfo     : Option[I],
//    previousAction   : Option[A])
//  {
//    def discountProbability(discount : Double) : Path[I, A] =
//      Path(reachProbability * discount, previousInfo, previousAction)
//
//    def choose(info: I, choice : A) : Path[I, A] =
//      Path(reachProbability, Some(info), Some(choice))
//  }
//
//
//  private case class Context[S, I, A, U](
//    game          : ExtensiveGame[S, I, A],
//    abstraction   : ExtensiveAbstraction[I, A], // for looking up opponents' strategies (for calculating reach probabilities)
//    mixedStrategy : ExtensiveStrategyProfile,
//    infoTree      : BestResponseInfoTree,
//    player        : Int,
//    visitor       : (ResponseTreeLeaf[S, I, A]) => U)
}
