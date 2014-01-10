package ao.learn.mst.gen5.br

/**
 *
 */
case class ResponseTreeLeaf[State, InformationSet, Action](
  reachProbability : Double,
  payoff           : Double,
  terminalState    : State,
  informationSet   : InformationSet,
  action           : Action)
{
  def counterfactualValue : Double =
    reachProbability * payoff
}
