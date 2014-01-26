package ao.learn.mst.gen5.br

import ao.learn.mst.gen5.{ExtensiveAbstraction, ExtensiveGame}
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile
import ao.learn.mst.gen5.example.abstraction.AbstractionUtils
import scala._

/**
 *
 */
object BestResponseFinder
{
  //--------------------------------------------------------------------------------------------------------------------
  def bestResponseProfile[S, I, A](
    game          : ExtensiveGame[S, I, A],
    abstraction   : ExtensiveAbstraction[I, A],
    mixedStrategy : ExtensiveStrategyProfile)
    : BestResponseProfile[I, A] =
  {
    val infoTree : BestResponseInfoTree[S, I, A] =
      BestResponseInfoTreeBuilder.build(game)

    val bestResponses : Seq[BestResponse[I, A]] =
      for (i <- 0 until game.playerCount)
      yield bestResponse(
        Context(game, infoTree, i),
        abstraction, mixedStrategy)

    BestResponseProfile(bestResponses)
  }

  private case class Context[S, I, A](
    game             : ExtensiveGame[S, I, A],
    infoTree         : BestResponseInfoTree[S, I, A],
    respondingPlayer : Int)
  

  //--------------------------------------------------------------------------------------------------------------------
  private def bestResponse[S, I, A](
    context       : Context[S, I, A],
    abstraction   : ExtensiveAbstraction[I, A],
    mixedStrategy : ExtensiveStrategyProfile)
    : BestResponse[I, A] =
  {
//    val allInfoActions : Map[I, Map[A, I]] =
//      AbstractionUtils.decisions(game)
//        .filter(decision => decision.nextToAct.index == respondingPlayer)
//        .map(decision => decision.informationSet -> decision.choices.toSet)
//        .toMap
//        .mapValues((actions: Set[A]) =>
//          actions.map(action => (action, game.)))

    val allInfoActions : Map[I, Set[A]] =
      AbstractionUtils.decisions(context.game)
        .filter(decision => decision.nextToAct.index == context.respondingPlayer)
        .map(decision => decision.informationSet -> decision.choices.toSet)
        .toMap

    val initialKnownInfoActionValues : Map[I, Map[A, Double]] =
      leafInfoActionValues(context, abstraction, mixedStrategy)

    val initialKnownInfoValues : Map[I, Double] = {
      val initialKnownInfos : Traversable[I] =
          initialKnownInfoActionValues
            .filter((infoActionValues: (I, Map[A, Double])) =>
              infoActionValues._2.size == allInfoActions(infoActionValues._1).size)
            .map(_._1)

      initialKnownInfos
        .map(info => info -> initialKnownInfoActionValues(info).values.max)
        .toMap
    }



//    val initialKnownActionValues : Map[I, Map[A, Double]] =
//      Map()

//    val allInfos : Set[I] =
//      AbstractionUtils.decisions(context.game)
//        .filter(decision => decision.nextToAct.index == context.respondingPlayer)
//        .map(_.informationSet)

    val initialUnknownInfos : Set[I] =
      allInfoActions.keySet.filterNot(initialKnownInfoValues.keySet.contains)

//    val initialUnknownInfos : Set[I] =
//      context.infoTree
//        .nonTerminalInformationSets(context.respondingPlayer)
    
//    val initialUnknownActionValues : Map[I, Set[A]] =
//      subtractKnownActions(
//        infoTree.infoActions(respondingPlayer),
//        initialKnownActionValues.mapValues(_.keySet))
//    
//    val initialFullyKnownInfos : Set[I] =
//      allInfoActions.keySet
//        .filterNot(initialUnknownActionValues.contains)
//
//    val initialKnownInfoValues : Map[I, Double] =
//      initialKnownActionValues
//        .filterKeys(initialFullyKnownInfos.contains)
//        .mapValues(_.values.max)
    
//    val initialUnknownInfoActions : Map[I, Set[A]] =
//      allInfoActions.filterKeys(! fullyKnownInfos.contains(_))
//
//    {
//      val respondingDecisions : Set[Decision[I, A]] =
//        AbstractionUtils.decisions(game)
//          .filter(decision => decision.nextToAct.index == respondingPlayer)
//
//      val allInfoSetActions : Map[I, Set[A]] =
//        respondingDecisions.map(decision =>
//          decision.informationSet -> decision.choices.toSet
//        ).toMap
//
//      allInfoSetActions.filter((e: (I, Set[A])) =>
//        initialKnownInfoValues.contains(e._1))
//    }

    bestResponse(
      context,
      initialUnknownInfos,
      initialKnownInfoValues,
      initialKnownInfoActionValues)
  }
  
  private def bestResponse[S, I, A](
    context           : Context[S, I, A],
    unknownInfos      : Set[I],
    knownInfoValues   : Map[I, Double],
    knownActionValues : Map[I, Map[A, Double]])
    : BestResponse[I, A] =
  {
    if (unknownInfos.isEmpty)
    {
      val root : I =
        context.infoTree.root(context.respondingPlayer)
      
      val rootValue : Double =
        knownInfoValues(root)

      val strategy : Map[I, A] =
        knownActionValues.mapValues((infoActionValues: Map[A, Double]) =>
          infoActionValues.maxBy(_._2)._1)

      BestResponse(rootValue, strategy)
    }
    else
    {
      val learnableInfos : Set[I] =
        unknownInfos.filter(info => {
          val actionTransitions : Map[A, I] =
            context.infoTree.actionTransitions(info, context.respondingPlayer)
          
          val knownActions : Set[A] =
            actionTransitions
              .filter(transition => knownInfoValues.contains(transition._2))
              .keySet
          
          actionTransitions.keySet == knownActions
        })
      
      assert(! learnableInfos.isEmpty)
      
      val nextUnknownInfos : Set[I] =
        unknownInfos.filterNot(learnableInfos.contains)

      val learnedActionValues : Map[I, Map[A, Double]] =
        learnableInfos
          .map(info => {
            val actionTransitions : Map[A, I] =
              context.infoTree.actionTransitions(info, context.respondingPlayer)

            val actionValues : Map[A, Double] =
              actionTransitions.mapValues(knownInfoValues)
          
            (info, actionValues)
          })
          .toMap  
      
      val nextKnownActionValues : Map[I, Map[A, Double]] =
        (knownActionValues.keySet ++ learnedActionValues.keySet)
          .map(knownActionInfo => {
            val knownForInfo : Map[A, Double] =
              knownActionValues.getOrElse(knownActionInfo, Map.empty)

            val learnedForInfo : Map[A, Double] =
              learnedActionValues.getOrElse(knownActionInfo, Map.empty)

            val newKnownForInfo : Map[A, Double] =
              knownForInfo ++ learnedForInfo

            (knownActionInfo, newKnownForInfo)
          })
          .toMap

        knownActionValues ++ learnedActionValues

      val learnedInfoValues : Map[I, Double] =
        learnableInfos
          .map(info => (info, nextKnownActionValues(info).values.max))
          .toMap

      val nextKnownInfoValues : Map[I, Double] =
        knownInfoValues ++ learnedInfoValues

      bestResponse(
        context,
        nextUnknownInfos,
        nextKnownInfoValues,
        nextKnownActionValues
      )
    }

//    if (unknownInfoSets.isEmpty)
//    {
//      val root : I =
//        ???
//
//      val rootValue : Double =
//        infoValues(root)
//
//      val strategy : Map[I, A]=
//        actionValues.mapValues((infoActionValues: Map[A, Double]) =>
//          infoActionValues.maxBy(_._2)._1)
//
//      BestResponse(rootValue, strategy)
//    }
//    else
//    {
//      val valueObservations : Set[Option[(I, Double)]] =
//        for (i <- unknownInfoSets) yield {
//          val actions : Set[A] =
//
//            ???
//
//          val knownActions : Set[A] =
//            actionValues(i).keySet
//
//          if (knownActions.size == actions.size)
//          {
//            val values : Map[A, Double] =
//              knownActions.map(a => (a, actionValues(i))).toMap
//
//            val infoValue : Double =
//              values.maxBy(_._2)._2
//
//            Some((i, infoValue))
//          }
//          else
//          {
//            None
//          }
//        }
//
//      val learnedInfoValues : Map[I, Double] =
//        valueObservations.flatten.toMap
//
//      {
//        val nextActionValues : Map[(I, A), Double] = {
//          val predecessors : Map[I, (I, A)] =
//            ???
//
//          predecessors.map((e: (I, (I, A))) =>
//            (e._2, learnedInfoValues(e._1)))
//        }
//
//        val nextInfoValues : Map[I, Double] =
//          infoValues ++ learnedInfoValues
//
//        val nextUnknownInfoSets : Set[I] =
//          unknownInfoSets.filterNot(learnedInfoValues.keySet.contains)
//
//        bestResponseIteration(
//          nextActionValues, nextInfoValues, nextUnknownInfoSets)
//      }
//    }
  }



  //--------------------------------------------------------------------------------------------------------------------
//  private def subtractKnownActions(
//      allInfoActions : Map[I, Set[A]], knownInfoActions : Map[I, Set[A]]) : Map[I, Set[A]] =
//    allInfoActions
//      .map((infoActions: (I, Set[A])) => {
//      val (info : I, actions : A) =
//        infoActions
//
//      val known : Set[A] =
//        knownInfoActions.get(info) match {
//          case None => Set.empty
//          case Some(actionValues) => actionValues
//        }
//
//      val unknown : Set[A] =
//        actions.filterNot(known.contains)
//
//      (info, unknown)
//    })
//      .filterNot(_._2.isEmpty)
  
  
  private def leafInfoActionValues[S, I, A](
    context       : Context[S, I, A],
    abstraction   : ExtensiveAbstraction[I, A],
    mixedStrategy : ExtensiveStrategyProfile)
    : Map[I, Map[A, Double]] =
  {
    val responseTreeLeaves : Traversable[ResponseTreeLeaf[S, I, A]] =
      ResponseTreeTraverser.traverseResponseTreeLeaves(
        context.game, abstraction, mixedStrategy, context.respondingPlayer)

    val infoToLeaves : Map[I, Traversable[ResponseTreeLeaf[S, I, A]]] =
      responseTreeLeaves
        .groupBy(_.informationSet)

    def actionValues(leaves: Traversable[ResponseTreeLeaf[S, I, A]]): Map[A, Double] = {
      val actionLeaves : Map[A, Traversable[ResponseTreeLeaf[S, I, A]]] =
        leaves.groupBy(_.action)

      def expectedValueForLeaves(leaves: Traversable[ResponseTreeLeaf[S, I, A]]): Double =
        leaves.map(_.counterfactualValue).sum

      actionLeaves.mapValues(expectedValueForLeaves)
    }

    infoToLeaves.mapValues(actionValues)
  }
}
