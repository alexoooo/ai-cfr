package ao.learn.mst.gen5.br

import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen5.node.{Chance, Terminal, Decision, ExtensiveNode}
import ao.learn.mst.gen4.Rational

/**
 * http://en.wikipedia.org/wiki/Best_response
 */
object GameValueFinder
{
  def bestResponseProfile[S, I, A](
    game             : ExtensiveGame[S, I, A],
    abstraction      : ExtensiveAbstraction[I, A],
    mixedStrategy    : ExtensiveStrategyProfile)
    : BestResponseProfile[I, A] =
  {
    val bestResponses : Seq[BestResponse[I, A]] =
      for (i <- 0 until game.playerCount)
      yield bestResponse(game, abstraction, mixedStrategy, i)

    BestResponseProfile(bestResponses)
  }


  def bestResponse[S, I, A](
    game             : ExtensiveGame[S, I, A],
    abstraction      : ExtensiveAbstraction[I, A],
    mixedStrategy    : ExtensiveStrategyProfile,
    respondingPlayer : Int)
    : BestResponse[I, A] =
  {
    var strategy : Map[I, A] =
      Map.empty

    def evaluate(state: S, reachProbability: Double): Double =
    {
      val node : ExtensiveNode[I, A] =
        game.node(state)

      node match
      {
        case Decision(Rational(`respondingPlayer`), info, choices) =>
        {
          def choiceValue(c: A) : Double =
            evaluate(game.transition(state, c), reachProbability)

          val actionToValue : Traversable[(A, Double)] =
            choices.map(c => (c, choiceValue(c)))

          val maxChoice : (A, Double) =
            actionToValue.maxBy(_._2)

          strategy += info -> maxChoice._1

          maxChoice._2
        }


        case Decision(Rational(opposingPlayer), info, choices) => {
          val infoIndex : Int =
            abstraction.informationSetIndex(info)

          val actionProbabilities : Seq[Double] =
            mixedStrategy.actionProbabilityMass(
              infoIndex, choices.size)

          val childValues : Traversable[Double] =
            for (c <- choices) yield
            {
              val actionIndex : Int =
                abstraction.actionSubIndex(info, c)

              val actionProbability : Double =
                actionProbabilities(actionIndex)

              val nextReachProbability : Double =
                reachProbability * actionProbability

              val childValue : Double =
                evaluate(game.transition(state, c), nextReachProbability)

              childValue
            }

          childValues.sum
        }


        case Chance(outcomes) => {
          val childValues : Traversable[Double] =
            for (o <- outcomes) yield
            {
              val nextReachProbability : Double =
                reachProbability * o.probability

              val childValue : Double =
                evaluate(game.transition(state, o.action), nextReachProbability)

              childValue
            }

          childValues.sum
        }


        case Terminal(payoffs) =>
          reachProbability *
            payoffs(respondingPlayer)
      }
    }

    val gameValue : Double =
      evaluate(game.initialState, 1.0)

    BestResponse(gameValue, strategy)
  }
}
