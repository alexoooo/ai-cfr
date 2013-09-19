package ao.learn.mst.gen5.example.matrix

import ao.learn.mst.gen5.ExtensiveGame
import ao.learn.mst.gen5.node.{Terminal, Decision, ExtensiveNode}
import org.la4j.matrix.Matrix


//----------------------------------------------------------------------------------------------------------------------
case class MatrixGame(
    rowPlayerPayoffs : Matrix,
    columnPlayerPayoffs : Matrix
  ) extends ExtensiveGame[MatrixState, MatrixPlayer, MatrixAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  assert(rowPlayerPayoffs.rows   () == columnPlayerPayoffs.rows   ())
  assert(rowPlayerPayoffs.columns() == columnPlayerPayoffs.columns())


  //--------------------------------------------------------------------------------------------------------------------
  def this(payoffs: PayoffMatrixSource) =
    this(payoffs.rowPayoffs, payoffs.columnPayoffs)



  //--------------------------------------------------------------------------------------------------------------------
  def playerCount: Int =
    2


  //--------------------------------------------------------------------------------------------------------------------
  def initialState: MatrixState =
    RowPlayerDecision


  //--------------------------------------------------------------------------------------------------------------------
  def node(state: MatrixState): ExtensiveNode[MatrixPlayer, MatrixAction] =
    state match {
      case RowPlayerDecision => {
        val actionIndexes : Seq[Int] =
          0 until rowPlayerPayoffs.rows()

        val matrixActions : Traversable[RowPlayerAction] =
          actionIndexes.map(RowPlayerAction)

        Decision(0, RowPlayer, matrixActions)
      }


      case ColumnPlayerDecision(r) => {
        val actionIndexes : Seq[Int] =
          0 until columnPlayerPayoffs.columns()

        val matrixActions : Traversable[ColumnPlayerAction] =
          actionIndexes.map(ColumnPlayerAction)

        Decision(1, ColumnPlayer, matrixActions)
      }

      case MatrixTerminal(r, c) =>
        Terminal(
          Seq(
            rowPlayerPayoffs.get(r.index, c.index),
            columnPlayerPayoffs.get(r.index, c.index)))
    }


  //--------------------------------------------------------------------------------------------------------------------
  def transition(nonTerminal: MatrixState, action: MatrixAction): MatrixState =
    (nonTerminal, action) match {
      case (RowPlayerDecision, r : RowPlayerAction) =>
        ColumnPlayerDecision(r)

      case (ColumnPlayerDecision(r), c: ColumnPlayerAction) =>
        MatrixTerminal(r, c)

      case _ => throw new Error
    }
}
