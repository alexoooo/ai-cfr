package ao.learn.mst.gen5.cfr

import ao.learn.mst.gen5.solve.{SolutionApproximation, ExtensiveSolver}
import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.learn.mst.gen5.strategy._
import ao.learn.mst.gen5.strategy.impl.{AveragingCfrStrategyProfile, MapCfrChanceRegretBuffer, ArrayCfrStrategyProfile}
import ao.learn.mst.gen5.node._
import ao.learn.mst.gen5.ExtensiveGame
import scala._
import scala.Function
import scala.collection.immutable.SortedMap
import scala.util.Random
import com.google.common.collect.Iterables
import ao.learn.mst.gen5.state.MixedStrategy


//----------------------------------------------------------------------------------------------------------------------
case class ChanceSampledCfrMinimizer[State, InformationSet, Action](
    averageStrategy : Boolean = false,
    rand            : Random = new Random())
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
    val strategyProfile : CfrStrategyProfile = {
      val baseStrategy = new ArrayCfrStrategyProfile

      if (averageStrategy) {
        new AveragingCfrStrategyProfile(baseStrategy)
      } else {
        baseStrategy
      }
    }

    //------------------------------------------------------------------------------------------------------------------Z
    def optimize(extensiveAbstraction: ExtensiveAbstraction[InformationSet, Action]) : Unit = {
      new Optimization(extensiveAbstraction).updateFromRoot()
    }


    //------------------------------------------------------------------------------------------------------------------
    private class Optimization(
      abstraction: ExtensiveAbstraction[InformationSet, Action])
    {
      val buffer : CfrChanceRegretBuffer =
        new MapCfrChanceRegretBuffer


      //----------------------------------------------------------------------------------------------------------------
      def updateFromRoot() {
        cfrUpdate(
          game.initialStateNode,
          Seq.fill( game.playerCount )( 1.0 ))

//        println(s"buffer: $buffer")
        buffer.commit(strategyProfile)
//        println(s"strategyProfile: $strategyProfile")
      }


      //----------------------------------------------------------------------------------------------------------------
      private def cfrUpdate(
          stateNode          : ExtensiveStateNode[State, InformationSet, Action],
          reachProbabilities : Seq[Double]
          ): Seq[Double] =
      {
        stateNode match {
          case stateDecision : StateDecision[State, InformationSet, Action] =>
            walkDecision(stateDecision, reachProbabilities)

          case stateChance : StateChance[State, InformationSet, Action] =>
            walkChance(stateChance, reachProbabilities)

          case terminal : StateTerminal[State, InformationSet, Action] =>
            // train.cpp line 606
            terminal.node.payoffs
        }
      }


      //----------------------------------------------------------------------------------------------------------------
      private def walkChance(
          stateNode          : StateChance[State, InformationSet, Action],
          reachProbabilities : Seq[Double]
          ): Seq[Double] =
      {
        val sampledAction: Action =
          stateNode.node.outcomes.sample(rand)

        val sampledProbability: Double =
          stateNode.node.outcomes.probability(sampledAction)

        val sampledStateNode: ExtensiveStateNode[State, InformationSet, Action] =
          game.transitionStateNode(stateNode, sampledAction)

        val nextReachProbabilities: Seq[Double] =
          reachProbabilities.map(_ * sampledProbability)

        cfrUpdate(sampledStateNode, nextReachProbabilities)
      }


      //----------------------------------------------------------------------------------------------------------------
      private def walkDecision(
          stateNode          : StateDecision[State, InformationSet, Action],
          reachProbabilities : Seq[Double]
          ): Seq[Double] =
      {
        val infoSet: InformationSet =
          stateNode.node.informationSet

        val informationSetIndex: Int =
          abstraction.informationSetIndex(infoSet)

        val actionCount: Int =
          abstraction.actionCount(infoSet)

        // Compute σ1(I(r1)) according to Equation 8.
        val actionProbabilities: Seq[Double] =
          strategyProfile.positiveRegretMatchingStrategy(
            informationSetIndex, actionCount)

        // for Each action a ∈ A(I(r1))
        //   Compute u1(σ, I(r1), a) and u2(σ, r2, a)
        val playerChildUtilities: Seq[Seq[Double]] =
          childUtilities(
            stateNode,
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
            stateNode.node.nextToAct.index

          val opponentReachProbability : Double =
            (for (playerIndex <- 0 until game.playerCount
                  if playerIndex != nextToAct)
              yield reachProbabilities(playerIndex)
            ).product

          val actionRegret: Seq[Double] =
            playerChildUtilities(nextToAct).map(childUtility =>
              childUtility - expectedUtilities(nextToAct))

          buffer.bufferRegret(
            informationSetIndex,
            actionRegret,
            opponentReachProbability)
        }

        expectedUtilities
      }


      //----------------------------------------------------------------------------------------------------------------
      private def childUtilities(
          stateNode           : StateDecision[State, InformationSet, Action],
          actionProbabilities : Seq[Double],
          reachProbabilities  : Seq[Double]
          ): Seq[Seq[Double]] = // Action Index -> Player -> Payoff
      {
        def actionIndex(action : Action) : Int =
          abstraction.actionSubIndex(
            stateNode.node.informationSet, action)

        val actionToIndex : Map[Action, Int] =
          stateNode.node.choices.map(a => (a, actionIndex(a))).toMap

        val indexToAction : Map[Int, Traversable[Action]] =
          actionToIndex.keys.groupBy(actionToIndex)

        def sampleAction(choices: Traversable[Action]) : Action = {
          if (choices.size == 1) {
            Iterables.getOnlyElement(
              scala.collection.JavaConversions.asJavaIterable(choices.toIterable))
          } else {
            val sampledIndex : Int =
              (choices.size * rand.nextDouble()).toInt

            choices.toSeq(sampledIndex)
          }
        }
          

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
          val childStateNode : ExtensiveStateNode[State, InformationSet, Action] =
            game.transitionStateNode(stateNode, action)

          val actionProbability : Double =
            actionProbabilities( index )

          val childReachProbabilities : Seq[Double] =
            updateReachProbability(
              reachProbabilities,
//              game.playerCount,
              stateNode.node.nextToAct.index,
              actionProbability)

          cfrUpdate(childStateNode, childReachProbabilities)
        }
      }

      private def updateReachProbability(
          reachProbabilities  : Seq[Double],
//          rationalPlayerCount : Int,
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
    override def strategyView: MixedStrategy =
      strategyProfile.toExtensiveStrategyProfile
  }
}
