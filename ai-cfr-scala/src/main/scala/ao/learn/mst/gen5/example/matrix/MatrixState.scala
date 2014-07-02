package ao.learn.mst.gen5.example.matrix


//----------------------------------------------------------------------------------------------------------------------
sealed trait MatrixState


case object RowPlayerDecision extends MatrixState


case class ColumnPlayerDecision(
    rowPlayerAction : RowPlayerAction
  ) extends MatrixState


case class MatrixTerminal(
    rowPlayerAction    : RowPlayerAction,
    columnPlayerAction : ColumnPlayerAction
  ) extends MatrixState



