package ao.learn.mst.gen5.br

/**
 * Best response from each player's perspective.
 */
case class BestResponseProfile[InformationSet, Action](
  bestResponses: Seq[BestResponse[InformationSet, Action]])
