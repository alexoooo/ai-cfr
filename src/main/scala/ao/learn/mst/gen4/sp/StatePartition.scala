package ao.learn.mst.gen4.sp

/**
 * Types of game states.
 */
sealed trait StatePartition

case object DecisionPartition extends StatePartition
case object ChancePartition   extends StatePartition
case object TerminalPartition extends StatePartition
