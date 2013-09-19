package ao.learn.mst.gen5.example.matrix

import org.la4j.matrix.source.MatrixSource
import org.la4j.matrix.Matrix
import org.la4j.matrix.dense.Basic2DMatrix


//----------------------------------------------------------------------------------------------------------------------
case class PayoffMatrixSource(
  payoffs : Seq[Seq[(Double, Double)]])
{
  //--------------------------------------------------------------------------------------------------------------------
  def rowPayoffs : Matrix =
    new Basic2DMatrix(rowPayoffSource)

  def rowPayoffSource : MatrixSource = new MatrixSourceWithDimensions {
    def get(i: Int, j: Int): Double =
      payoffs(i)(j)._1
  }


  //--------------------------------------------------------------------------------------------------------------------
  def columnPayoffs : Matrix =
    new Basic2DMatrix(columnPayoffSource)

  def columnPayoffSource : MatrixSource = new MatrixSourceWithDimensions {
    def get(i: Int, j: Int): Double =
      payoffs(i)(j)._2
  }


  //--------------------------------------------------------------------------------------------------------------------
  private trait MatrixSourceWithDimensions extends MatrixSource{
    def columns(): Int = payoffs(0).length
    def rows(): Int = payoffs.length
  }
}
