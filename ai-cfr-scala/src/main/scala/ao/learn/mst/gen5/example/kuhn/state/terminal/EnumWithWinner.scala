package ao.learn.mst.gen5.example.kuhn.state.terminal

import ao.learn.mst.lib.Enum


trait EnumWithWinner[T] extends Enum {
  type EnumVal <: Value with NamedWinnerIndicator[T]
}