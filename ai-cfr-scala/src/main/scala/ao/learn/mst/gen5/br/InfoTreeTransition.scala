package ao.learn.mst.gen5.br

/**
 * 
 */
case class InfoTreeTransition[InformationSet, Action](
  parentInfoSet : InformationSet,
  action        : Action,
  childInfoSet  : InformationSet)
