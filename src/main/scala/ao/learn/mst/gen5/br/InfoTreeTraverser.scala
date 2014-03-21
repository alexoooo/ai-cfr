package ao.learn.mst.gen5.br

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.node.Chance
import scala.Some
import scala.util.control.Breaks

/**
 * 
 */
object InfoTreeTraverser
{
  //--------------------------------------------------------------------------------------------------------------------
  def rootInfoSet[S, I, A](
    game   : ExtensiveGame[S, I, A],
    player : Int)
    : I =
  {
    var traversedRoot: Option[I] = None

    Breaks.breakable {
      traverse(
        Context[S, I, A](
          game,
          player,
          (transition:InfoTreeTransition[I, A]) => (),
          (root: I) => {
            traversedRoot = Some(root)
            Breaks.break()
          }
        ),
        Path(None, None),
        game.initialStateNode)
    }

    traversedRoot.get
  }
  
  
  //--------------------------------------------------------------------------------------------------------------------
  def traverseInfoSetTransitions[S, I, A](
    game   : ExtensiveGame[S, I, A],
    player : Int)
    : Traversable[InfoTreeTransition[I, A]] =
  {
    new Traversable[InfoTreeTransition[I, A]] {
      def foreach[U](visitor: (InfoTreeTransition[I, A]) => U): Unit =
      {
        traverse(
          Context(game, player, visitor, (info:I) => ()),
          Path(None, None),
          game.initialStateNode)
      }
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def traverse[S, I, A](
    context   : Context[S, I, A],
    path      : Path[I, A],
    stateNode : ExtensiveStateNode[S, I, A])
    : Unit =
  {
    stateNode.node match {
      case Chance(outcomes) =>
        outcomes.foreach((outcome: Outcome[A]) =>
          traverse(
            context,
            path,
            context.game.transitionStateNode(stateNode, outcome.action))
        )

      case Decision(Rational(`context`.player), info, choices) =>
        if (path.previousInfo.isDefined)
        {
          val transition : InfoTreeTransition[I, A] =
            InfoTreeTransition(
              path.previousInfo.get,
              path.previousAction.get,
              info)
          
          context.transitionVisitor(transition)
        }  
        else
        {
          context.rootVisitor(info)
        }

        choices.foreach((choice: A) =>
          traverse(
            context,
            path.choose(info, choice),
            context.game.transitionStateNode(stateNode, choice))
        )

      case Decision(Rational(opponent), info, choices) =>
        choices.foreach((choice: A) => {
          traverse(
            context,
            path,
            context.game.transitionStateNode(stateNode, choice))
        })

      case _ =>
    }
  }


  private case class Path[I, A](
    previousInfo   : Option[I],
    previousAction : Option[A])
  {
    def choose(info: I, choice : A) : Path[I, A] =
      Path(Some(info), Some(choice))
  }

  
  private case class Context[S, I, A](
    game              : ExtensiveGame[S, I, A],
    player            : Int,
    transitionVisitor : (InfoTreeTransition[I, A]) => _,
    rootVisitor       : (I) => Unit)
}
