package ao.learn.mst.gen5.abstraction

import ao.learn.mst.gen5.ExtensiveAbstraction
import com.google.common.collect.{ArrayListMultimap, ListMultimap, Multimap}


class InfoPerDecisionSetAbstraction[InformationSet, Action]
    extends ExtensiveAbstraction[InformationSet, Action]
{
  private val knownInfosByActions: ListMultimap[InformationSet, Action] =
    ArrayListMultimap.create()


  override def actionCount(informationSet: InformationSet): Int = {
    val size = knownInfosByActions.get(informationSet).size()
    math.max(size, 1)
  }

  
  override def actionSubIndex(informationSet: InformationSet, action: Action): Int = {
    val currentSubIndex: Int =
      knownInfosByActions.get(informationSet).indexOf(action)

    if (currentSubIndex == -1) {
      knownInfosByActions.put(informationSet, action)
      knownInfosByActions.get(informationSet).size() - 1
    } else {
      currentSubIndex
    }
  }


  override def informationSetIndex(informationSet: InformationSet): Int =
    knownInfosByActions.keySet().size()


  override def actionIndex(action: Action): Int = ???
}
