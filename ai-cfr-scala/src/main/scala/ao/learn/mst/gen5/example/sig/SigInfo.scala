package ao.learn.mst.gen5.example.sig


sealed trait SigInfo


case class SigSender  (identity : SigIdentify) extends SigInfo
case class SigReceiver(message  : SigSend    ) extends SigInfo


