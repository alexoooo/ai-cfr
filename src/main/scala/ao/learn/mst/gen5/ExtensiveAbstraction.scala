package ao.learn.mst.gen5

/**
 * Not guaranteed to be deterministic (e.g. could be assigned stochastically).
 *
 * @tparam InformationSet decision state
 * @tparam Action deliberate choice or chance outcome
 */
trait ExtensiveAbstraction[InformationSet, Action]
{
  /**
   * @param informationSet decision state
   * @return abstract index of visible information observed in a decision state
   */
  def informationSetIndex(informationSet : InformationSet) : Int

  def actionIndex(action : Action) : Int

  /**
   * @param informationSet decision state
   * @param action action taken in decision state
   * @return abstract index of action in informationSet
   */
  def actionSubIndex(informationSet : InformationSet, action : Action) : Int

  def actionCount(informationSet : InformationSet) : Int
}
