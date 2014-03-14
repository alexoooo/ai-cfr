package ao.learn.mst.gen5.state.impl.data

import java.util
import java.io._
import com.google.common.io.Files
import scala.Some


/**
 * 
 */
class ArrayTableTally
{
  //--------------------------------------------------------------------------------------------------------------------
  private var valueSums: Array[Array[Double]] =
    Array.empty


  //--------------------------------------------------------------------------------------------------------------------
  def addAll(indexToValues: Map[Long, Seq[Double]]): Unit =
    if (! indexToValues.isEmpty)
    {
      val maxIndex: Int =
        checkIndex(indexToValues.keySet.max)

      initInfoSetIfRequired(maxIndex)

      for ((infoSet, strategy) <- indexToValues) {
        add(checkIndex(infoSet), strategy)
      }
    }

  private def add(index: Int, values: Seq[Double]): Unit = {
    val valueCount = values.length
    initValuesIfRequired(index, valueCount)

    for (i <- 0 until valueCount) {
      valueSums(index)(i) += checkValue(values(i))
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  def get(index: Long): Option[IndexedSeq[Double]] = {
    if (size <= index) {
      None
    } else {
      val regret: Array[Double] =
        valueSums(checkIndex(index))

      if (regret == null) {
        None
      } else {
        Some(regret)
      }
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  private def size: Int =
    valueSums.size

  private def checkIndex(index: Long): Int = {
    require(index >= 0)
    require(index < Int.MaxValue - 8)
    index.toInt
  }

  private def checkValue(value: Double): Double = {
    require(! value.isNaN)
    require(! value.isInfinity)
    value
  }



  //--------------------------------------------------------------------------------------------------------------------
  private def initInfoSetIfRequired(informationSetIndex: Int): Unit =
    if (informationSetIndex >= size) {
      valueSums = util.Arrays.copyOf(valueSums, informationSetIndex + 1)
    }

  private def initValuesIfRequired(informationSetIndex: Int, actionCount: Int): Unit = {
    val current: Array[Double] =
      valueSums(informationSetIndex)

    if (current == null) {
      valueSums(informationSetIndex) = new Array[Double](actionCount)
    } else if (current.size < actionCount) {
      valueSums(informationSetIndex) = util.Arrays.copyOf(current, actionCount)
    }
  }



  //--------------------------------------------------------------------------------------------------------------------
  def write(filePath: File): Unit = {
    Files.createParentDirs(filePath)

    val out: ObjectOutputStream =
      new ObjectOutputStream(
        new BufferedOutputStream(
          new FileOutputStream(
            filePath)))

    try {
      out.writeObject(valueSums)
    } finally {
      out.close()
    }
  }

  def read(filePath: File): Unit = {
    val in: ObjectInputStream =
      new ObjectInputStream(
        new BufferedInputStream(
          new FileInputStream(
            filePath)))

    try {
      valueSums =
        in.readObject()
          .asInstanceOf[Array[Array[Double]]]
    } finally {
      in.close()
    }
  }
}

