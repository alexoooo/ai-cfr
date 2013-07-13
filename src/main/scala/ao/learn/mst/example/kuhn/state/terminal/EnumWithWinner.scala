package ao.learn.mst.example.kuhn.state.terminal

import ao.learn.mst.lib.Enum


trait EnumWithWinner[T] extends Enum {
  type EnumVal <: Value with NamedWinnerIndicator[T]
}