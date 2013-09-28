package ao.learn.mst.gen5.br

/**
 * http://en.wikipedia.org/wiki/Best_response
 *
 * Pure strategy
 */
case class BestResponse[InformationSet, Action](
  value    : Double,
  strategy : Map[InformationSet, Action])
