package ao.learn.mst.gen4

/**
 * Types of game states.
 */
sealed trait StatePartition


case object Decision extends StatePartition
case object Chance   extends StatePartition
case object Terminal extends StatePartition
