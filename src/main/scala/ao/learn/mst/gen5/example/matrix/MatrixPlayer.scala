package ao.learn.mst.gen5.example.matrix



sealed trait MatrixPlayer


case object RowPlayer extends MatrixPlayer
case object ColumnPlayer extends MatrixPlayer