package ao.learn.mst.gen5.state.impl.data


/**
 *
 */
class MapTallyBuffer
{
  //--------------------------------------------------------------------------------------------------------------------
  private var valueSums: Map[Long, IndexedSeq[Double]] =
    Map[Long, IndexedSeq[Double]]()


  //--------------------------------------------------------------------------------------------------------------------
  def add(index: Long, values: Seq[Double]): Unit = {
    require(index >= 0)

    val existing: IndexedSeq[Double] =
      initSumsIfRequired(index, values.size)

    val sum: IndexedSeq[Double] =
      (existing, values).zipped.map(_ + _)
    
    valueSums +=
      index -> sum
  }

  private def initSumsIfRequired(index: Long, valueCount: Int): IndexedSeq[Double] = {
    valueSums.get(index) match {
      case None =>
        val initial = IndexedSeq.fill(valueCount)(0.0)

        valueSums +=
          index -> initial

        initial

      case Some(existing) =>
        require(existing.length <= valueCount)

        if (existing.length < valueCount) {
          val extended: IndexedSeq[Double] =
            existing.padTo(valueCount, 0.0)

          valueSums +=
            index -> extended

          extended
        } else {
          existing
        }
    }
  }



  //--------------------------------------------------------------------------------------------------------------------
  def accumulated: Map[Long, IndexedSeq[Double]] =
    valueSums
  
  
  //--------------------------------------------------------------------------------------------------------------------
  def clear(): Unit =
    valueSums = valueSums.empty
}
