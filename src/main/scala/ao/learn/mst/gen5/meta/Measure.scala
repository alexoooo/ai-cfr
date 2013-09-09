package ao.learn.mst.gen5.meta

import spire.algebra.{Ring, Eq, Order, AdditiveGroup}


//----------------------------------------------------------------------------------------------------------------------
sealed class Measure



//----------------------------------------------------------------------------------------------------------------------
case class Nominal[T : Eq](
  id : T)
  extends Measure



//----------------------------------------------------------------------------------------------------------------------
case class Ordinal[T : Order](
  index : T)
  extends Measure



//----------------------------------------------------------------------------------------------------------------------
case class Interval[T : AdditiveGroup](
  magnitude : T)
  extends Measure



//----------------------------------------------------------------------------------------------------------------------
case class Ratio[T : Ring](
  metric : T)
  extends Measure