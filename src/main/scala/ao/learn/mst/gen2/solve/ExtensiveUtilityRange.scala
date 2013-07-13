package ao.learn.mst.gen2.solve

/**
 * http://poker.cs.ualberta.ca/publications/abourisk.msc.pdf
 * 2.1.1
 *
 * Define delta(u,i) = max(z)(ui(z)) - min(z)(ui(z)) to be the range of utilities to player i.
 */
case class ExtensiveUtilityRange(
    minimum: Double,
    maximum: Double)
{
  //--------------------------------------------------------------------------------------------------------------------
  assert(minimum < maximum)


  //--------------------------------------------------------------------------------------------------------------------
  def range = (maximum - minimum)
}