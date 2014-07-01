package ao.learn.mst.gen5.example.abstraction

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node._
import com.google.common.collect.TreeTraverser
import ao.learn.mst.gen5.node.Chance
import ao.learn.mst.gen5.node.Decision
import scala.collection.convert.Wrappers.{JIterableWrapper, IterableWrapper}


object GameTraversal
{
  def traverseTreeBreadthFirst[State, InformationSet, Action](
    game : ExtensiveGame[State, InformationSet, Action]
    ) : Traversable[ExtensiveNode[InformationSet, Action]] =
  {
    new Traversable[ExtensiveNode[InformationSet, Action]](){
      def foreach[U](traverser: (ExtensiveNode[InformationSet, Action]) => U)
      {
        val traversalOrderSelector : TreeTraverser[State] => State => java.lang.Iterable[State] =
          _.breadthFirstTraversal

        val states : Traversable[State] =
          traverseStates(
            game, traversalOrderSelector)

        def traverseState(state : State) =
          traverser(game.node(state))

        states.foreach(traverseState)
      }
    }
  }

  private def traverseStates[State, InformationSet, Action](
      game : ExtensiveGame[State, _, Action],
      traversalOrderSelector : TreeTraverser[State] => State => java.lang.Iterable[State]
      ) : Traversable[State] =
  {
    val root : State =
      game.initialState

    val traverser : TreeTraverser[State] =
      new GameTreeStateTraverser(game)

    val states : java.lang.Iterable[State] =
      traversalOrderSelector(traverser)(root)

    new JIterableWrapper[State](
      states)
  }

  private class GameTreeStateTraverser[State, Action](
    game : ExtensiveGame[State, _, Action])
    extends TreeTraverser[State]
  {
    def children(state: State) : java.lang.Iterable[State] = {
      val node : ExtensiveNode[_, Action] =
        game.node(state)

      val actions : Traversable[Action] =
        node match {
          case Terminal(_            ) => Traversable.empty
          case Decision(_, _, choices) => choices
          case Chance  (outcomes     ) => outcomes.actions
        }

      def transitionTo(action : Action) =
        game.transition(state, action)

      val children : Traversable[State] =
        actions.map(transitionTo)

      new IterableWrapper[State](
        children.toIterable)
    }
  }
}
