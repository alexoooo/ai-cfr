package ao.learn.mst.gen2.info

import ao.learn.mst.gen2.player.model.FiniteAction

/**
 *
 */

trait InformationSetIndex
{
  //--------------------------------------------------------------------------------------------------------------------
  def informationSetCount: Int

  def indexOf(informationSet: InformationSet): Int

  /**
   * @param informationSet to look up in index
   * @return arbitrary indexing of actions, index must be unique within information set
   */
  def actionsOf(informationSet: InformationSet): Set[FiniteAction]

  /**
   * use for informational purposes only
   * does not necessarily reflect what was indexed
   */
  def informationSets: Traversable[InformationSet]

//  def nextToAct(informationSet: InformationSet): RationalPlayer
}
