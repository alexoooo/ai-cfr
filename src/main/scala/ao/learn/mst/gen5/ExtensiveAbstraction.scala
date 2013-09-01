package ao.learn.mst.gen5


trait ExtensiveAbstraction[InformationSet, Action]
{
  def informationSetIndex(informationSet : InformationSet) : Int

  def actionIndex(action : Action) : Int

  def actionSubIndex(informationSet : InformationSet, action : Action) : Int
}
