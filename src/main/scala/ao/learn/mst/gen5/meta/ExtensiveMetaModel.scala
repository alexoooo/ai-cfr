package ao.learn.mst.gen5.meta


trait ExtensiveMetaModel[InformationSet, Action]
{
  def describeInformationSet(informationSet : InformationSet) : Seq[Measure]
  def describeAction        (action         : Action        ) : Seq[Measure]
}
