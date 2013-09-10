package ao.learn.mst.gen5.cfr

import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.learn.mst.gen3.strategy.{ExtensiveStrategyProfile, CfrStrategyBuffer, CfrStrategyProfile}
import ao.learn.mst.gen3.strategy.impl.{MapCfrStrategyBuffer, ArrayCfrStrategyProfile}
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.ExtensiveGame
import scala._
import scala.Function
import ao.learn.mst.gen5.node.Decision
import ao.learn.mst.gen5.node.Chance
import scala.collection.immutable.SortedMap


//----------------------------------------------------------------------------------------------------------------------
class ChanceSampledCfrMinimizer[State, InformationSet, Action]
  extends ExtensiveSolver[State, InformationSet, Action]
{
  //--------------------------------------------------------------------------------------------------------------------
  def initialSolution(
    game: ExtensiveGame[State, InformationSet, Action]
    ): SolutionApproximation[InformationSet, Action] =
  {
    new Solution(game)
  }


  //--------------------------------------------------------------------------------------------------------------------
  private class Solution(
    game: ExtensiveGame[State, InformationSet, Action])
    extends SolutionApproximation[InformationSet, Action]
  {
    val model : CfrStrategyProfile =
      new ArrayCfrStrategyProfile


    //------------------------------------------------------------------------------------------------------------------Z
    def optimize(extensiveAbstraction: ExtensiveAbstraction[InformationSet, Action]) {
      new Optimization(extensiveAbstraction).updateFromRoot()
    }


    //------------------------------------------------------------------------------------------------------------------Z
    private class Optimization(
      abstraction: ExtensiveAbstraction[InformationSet, Action])
    {
      val buffer : CfrStrategyBuffer =
        new MapCfrStrategyBuffer


      //----------------------------------------------------------------------------------------------------------------
      def updateFromRoot() {
        cfrUpdate(
          game.initialState,
          game.node( game.initialState ),
          Seq.fill( game.playerCount )( 1.0 ))

        buffer.commit(model)
      }


      //----------------------------------------------------------------------------------------------------------------
      private def cfrUpdate(
          state              : State,
          node               : ExtensiveNode[InformationSet, Action],
          reachProbabilities : Seq[Double]
          ): Seq[Double] =
      {
        node match {
          case decision : Decision[InformationSet, Action] =>
            walkDecision(state, decision, reachProbabilities)

          case chance : Chance[InformationSet, Action] =>
            walkChance(state, chance, reachProbabilities)

          case terminal : Terminal[InformationSet, Action] => {
            // train.cpp line 606
            terminal.payoffs
          }
        }
      }


      //----------------------------------------------------------------------------------------------------------------
      private def walkChance(
          state              : State,
          node               : Chance[InformationSet, Action],
          reachProbabilities : Seq[Double]
          ): Seq[Double] =
      {
        val sampledOutcome : Outcome[Action] =
          node.outcomes.maxBy(_.probability * math.random)

        val sampledAction : Action =
          sampledOutcome.action

        val sampledProbability : Double =
          sampledOutcome.probability

        val sampledState : State =
          game.transition(state, sampledAction)

        val sampledNode : ExtensiveNode[InformationSet, Action] =
          game.node(sampledState)

        val reachProbability : Seq[Double] =
          reachProbabilities.map(_ * sampledProbability)

        cfrUpdate(sampledState, sampledNode, reachProbability)
      }


      //----------------------------------------------------------------------------------------------------------------
      private def walkDecision(
          state              : State,
          node               : Decision[InformationSet, Action],
          reachProbabilities : Seq[Double]
          ) : Seq[Double] =
      {
        val infoSet : InformationSet =
          node.informationSet

        val informationSetIndex : Int =
          abstraction.informationSetIndex(infoSet)

        val actionCount : Int =
          abstraction.actionCount(node.informationSet)

        // Compute σ1(I(r1)) according to Equation 8.
        val actionProbabilities: Seq[Double] =
          model.positiveRegretStrategy(
            informationSetIndex, actionCount).probabilities

        // for Each action a ∈ A(I(r1))
        //   Compute u1(σ, I(r1), a) and u2(σ, r2, a)
        val playerChildUtilities: Seq[Seq[Double]] =
          childUtilities(
            state,
            node,
            actionProbabilities,
            reachProbabilities
          ).transpose

        // Compute u1(σ, I(r1))) = sum[a∈A(I(r1)) | σ1(I(r1))(a) × u1(σ, I(r1), a)].
        def playerUtility(playerIndex: Int): Double =
          (for ((actionProbability, action) <- actionProbabilities.zipWithIndex)
            // train.cpp line 668: expected += ev[player]*probability[i];
            yield actionProbability * playerChildUtilities(playerIndex)(action)
          ).sum

        val expectedUtilities: Seq[Double] =
          (for (playerIndex <- 0 until game.playerCount)
            // train.cpp line 669: sum      += ev[opponent];
            // note: why doesn't it multiply by action probability? (is it done internally?)
            yield playerUtility(playerIndex)
          ).toSeq

        // Update counterfactual regret and average strategy
        {
          val nextToAct : Int =
            node.nextToAct.index

          val opponentReachProbability : Double =
            (for (playerIndex <- 0 until game.playerCount
                  if playerIndex != nextToAct)
              yield reachProbabilities(playerIndex)
            ).product

          val actionRegret: Seq[Double] =
            playerChildUtilities(nextToAct).map(childUtility =>
              childUtility - expectedUtilities(nextToAct))

          buffer.bufferUpdate(
            informationSetIndex,
            actionRegret,
            opponentReachProbability)
        }

        expectedUtilities
      }


      //----------------------------------------------------------------------------------------------------------------
      private def childUtilities(
          state               : State,
          node                : Decision[InformationSet, Action],
          actionProbabilities : Seq[Double],
          reachProbabilities  : Seq[Double]
          ): Seq[Seq[Double]] = // Action Index -> Player -> Payoff
      {
        def actionIndex(action : Action) : Int =
          abstraction.actionSubIndex(
            node.informationSet, action)

        val actionToIndex : Map[Action, Int] =
          node.choices.map(a => (a, actionIndex(a))).toMap

        val indexToAction : Map[Int, Traversable[Action]] =
          actionToIndex.keys.groupBy(actionToIndex)

        def sampleAction(choices : Traversable[Action]) : Action =
          choices.maxBy(a => math.random)

        val sampledActions = SortedMap[Int, Action]() ++
          indexToAction.transform(
            (index: Int, actions: Traversable[Action]) =>
            sampleAction(actions))

        assert(
          sampledActions.size == (sampledActions.keySet.max + 1),
          "Action indexes must be in [0, n).")

        for ((index, action) <- sampledActions.toSeq)
        yield {
          // Find the associated child of c1 of r1 and c2 of r2.
          val childState : State =
            game.transition(state, action)

          val childNode : ExtensiveNode[InformationSet, Action] =
            game.node(childState)

          val actionProbability : Double =
            actionProbabilities( index )

          val childReachProbabilities : Seq[Double] =
            updateReachProbability(
              reachProbabilities,
              game.playerCount,
              node.nextToAct.index,
              actionProbability)

          cfrUpdate(childState, childNode, childReachProbabilities)
        }
      }

      private def updateReachProbability(
          reachProbabilities  : Seq[Double],
          rationalPlayerCount : Int,
          actingPlayerIndex   : Int,
          actionProbability   : Double
          ): Seq[Double] =
      {
        // each player's contribution to the probability is in its own bucket.
        reachProbabilities.zipWithIndex map Function.tupled {
          (reachProbability: Double, playerIndex: Int) =>
            if (playerIndex == actingPlayerIndex)
              reachProbability * actionProbability
            else
              reachProbability
        }
      }
    }


    //------------------------------------------------------------------------------------------------------------------
    def strategy: ExtensiveStrategyProfile = {
      model.toExtensiveStrategyProfile
    }
  }
}
