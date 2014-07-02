package ao.learn.mst.gen2.info

/**
 * User: ao
 * Date: 26/07/12
 * Time: 9:27 PM
 */
case class ValueInformationSet(value: Any) extends InformationSet {
  override def toString: String = value.toString
}