package ao.learn.mst.lib



trait NamedNumberIndicator {
  def numVal : Int
  def name   : String
}

trait EnumWithNumber extends Enum {
  type EnumVal <: Value with NamedNumberIndicator
}

object FooWithNumber
    extends EnumWithNumber
{
  case class EnumVal private[FooWithNumber](
    name: String, numVal : Int)
    extends Value with NamedNumberIndicator
  type FooWithNumber = EnumVal

  val HELLO_WORLD = EnumVal("Hello", 42)

//  val SmallShowdown = EnumVal("SmallShowdown", None)
//  val BigShowdown   = EnumVal("BigShowdown"  , None)
//
//  val FirstToActWins = EnumVal("FirstToAct Wins", Some(KuhnPosition.FirstToAct))
//  val LastToActWins  = EnumVal("LastToAct Wins" , Some(KuhnPosition.LastToAct ))
}



/**
 * Date: 12/11/11
 * Time: 1:01 PM
 */

object enumTest extends App {

  import FooWithNumber._

  val f = FooWithNumber.HELLO_WORLD

  print( f )

  def fooBar(h : Option[FooWithNumber]) {

  }

}