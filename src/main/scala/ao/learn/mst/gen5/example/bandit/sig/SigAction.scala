package ao.learn.mst.gen5.example.bandit.sig


sealed trait SigAction

case class SigIdentify(senderType      : Boolean) extends SigAction
case class SigSend    (message         : Boolean) extends SigAction
case class SigReceive (senderTypeGuess : Boolean) extends SigAction


