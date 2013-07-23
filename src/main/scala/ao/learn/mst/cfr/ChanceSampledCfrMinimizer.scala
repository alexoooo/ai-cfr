package ao.learn.mst.cfr

import ao.learn.mst.gen2.info.{InformationSet, InformationSetIndex}
import ao.learn.mst.gen2.game._
import scala._
import ao.learn.mst.gen2.solve.ExtensiveGameSolver
import collection.immutable.SortedMap
import ao.learn.mst.gen2.player.model.{DeliberatePlayer, FiniteAction}


//----------------------------------------------------------------------------------------------------------------------
/**
 * Like CfrMinimizer but sampling chance outcomes.
 *
 */
class ChanceSampledCfrMinimizer extends ExtensiveGameSolver
{
  //--------------------------------------------------------------------------------------------------------------------
  def reduceRegret(
      game                : ExtensiveGame,
      informationSetIndex : InformationSetIndex,
      strategyProfile     : StrategyProfile)
  {
    val rootCounterfactualReachProbabilities =
      Seq.fill( game.rationalPlayerCount )( 1.0 )

    cfrUpdate(
      game,
      game.treeRoot,
      strategyProfile,
      rootCounterfactualReachProbabilities)

    strategyProfile.commitBuffers()
  }

  private def getDecisionForInformationSet(
      root           : ExtensiveGameNode,
      informationSet : InformationSet
      ): ExtensiveGameDecision =
  {
    if (root.isInstanceOf[ExtensiveGameDecision])
    {
      val decision = root.asInstanceOf[ExtensiveGameDecision]

      if (decision.informationSet == informationSet)
      {
        return decision
      }
    }

    getChildDecisionForInformationSet(
      root, informationSet)
  }
  private def getChildDecisionForInformationSet(
      root           : ExtensiveGameNode,
      informationSet : InformationSet
      ): ExtensiveGameDecision =
  {
    root match {
      case terminal: ExtensiveGameTerminal => null
      case nonTerminal: ExtensiveGameNonTerminal => {
          for (action <- root.actions) {
            val childNode = nonTerminal.child(action)
            val matchingNode = getDecisionForInformationSet(childNode, informationSet)
            if (matchingNode != null) {
              return matchingNode
            }
          }
          null
        }
    }
  }


  private def cfrUpdate(
      game               : ExtensiveGame,
      node               : ExtensiveGameNode,
      strategyProfile    : StrategyProfile,
      reachProbabilities : Seq[Double]
      ): Seq[Double] =
  {
    node match {
      case decision: ExtensiveGameDecision =>
        walkDecision(
          game,
          decision,
          strategyProfile,
          reachProbabilities)

      case chance: ExtensiveGameChance =>
        walkChance(
          game,
          chance,
          strategyProfile,
          reachProbabilities)

      case terminal : ExtensiveGameTerminal => {
        // train.cpp line 606

        val rationalPlayers: Seq[DeliberatePlayer] =
          (0 until game.rationalPlayerCount).map( DeliberatePlayer(_) )

        rationalPlayers.map(terminal.payoff.outcomes(_))
      }
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def walkChance(
      game               : ExtensiveGame,
      node               : ExtensiveGameChance,
      strategyProfile    : StrategyProfile,
      reachProbabilities : Seq[Double]
      ): Seq[Double] =
  {
    val actionProbabilities:SortedMap[FiniteAction, Double] =
      node.probabilityMass.actionProbabilities

    val actionObservations:Traversable[(FiniteAction, Double)] =
      actionProbabilities.map(e => (e._1, e._2 * math.random))

    val sampledAction:FiniteAction =
      actionObservations.maxBy(_._2)._1

    val sampledProbability:Double =
      actionProbabilities(sampledAction)

    val child:ExtensiveGameNode = node.child( sampledAction )

    cfrUpdate(
      game,
      child,
      strategyProfile,
      reachProbabilities.map(_ * sampledProbability)
    )
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def walkDecision(
      game               : ExtensiveGame,
      node               : ExtensiveGameDecision,
      strategyProfile    : StrategyProfile,
      reachProbabilities : Seq[Double]
      ) : Seq[Double] =
  {
    // Compute σ1(I(r1)) according to Equation 8.
    val actionProbabilities: Seq[Double] =
      strategyProfile.positiveRegretStrategy(
        node.informationSet, node.actions.size)

    // for Each action a ∈ A(I(r1))
    //   Compute u1(σ, I(r1), a) and u2(σ, r2, a)
    val playerChildUtilities: Seq[Seq[Double]] =
      childUtilities(
        game,
        node,
        strategyProfile,
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
      (for (playerIndex <- 0 until game.rationalPlayerCount)
        // train.cpp line 669: sum      += ev[opponent];
        // note: why doesn't it multiply by action probability? (is it done internally?)
        yield playerUtility(playerIndex)
      ).toSeq

    // Update counterfactual regret and average strategy
    {
      val opponentReachProbability =
        (for (playerIndex <- 0 until game.rationalPlayerCount
              if playerIndex != node.player.index)
          yield reachProbabilities(playerIndex)
        ).product

      val actionRegret: Seq[Double] =
        playerChildUtilities(node.player.index).map(childUtility =>
          childUtility - expectedUtilities(node.player.index))

      strategyProfile.bufferUpdate(node.informationSet,
        actionRegret, opponentReachProbability)
    }

    expectedUtilities
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def childUtilities(
      game                : ExtensiveGame,
      node                : ExtensiveGameDecision,
      strategyProfile     : StrategyProfile,
      actionProbabilities : Seq[Double],
      reachProbabilities  : Seq[Double]
      ): Seq[Seq[Double]] =
  {
    for (action <- node.actions.toSeq)
    yield {
      // Find the associated child of c1 of r1 and c2 of r2.
      val child:ExtensiveGameNode = node.child( action )

      val actionProbability: Double =
        actionProbabilities( action.index )

      val childReachProbabilities:Seq[Double] =
        updateReachProbability(
          reachProbabilities,
          game.rationalPlayerCount,
          node.player.index,
          actionProbability)

      cfrUpdate(
        game,
        child,
        strategyProfile,
        childReachProbabilities)
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