package ao.learn.mst.gen5

trait ExtensivePlayer[InformationSet, Action]
{
  def act(
    informationSet : InformationSet,
    actions : Traversable[Action]
    ) : Action
}
