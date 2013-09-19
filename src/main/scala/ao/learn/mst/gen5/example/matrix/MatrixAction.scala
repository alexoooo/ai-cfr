package ao.learn.mst.gen5.example.matrix


sealed trait MatrixAction {
  def index : Int
}


case class RowPlayerAction   (index : Int) extends MatrixAction
case class ColumnPlayerAction(index : Int) extends MatrixAction

