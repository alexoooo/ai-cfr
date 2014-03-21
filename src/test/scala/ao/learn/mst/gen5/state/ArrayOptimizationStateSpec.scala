package ao.learn.mst.gen5.state

import ao.learn.mst.gen5.state.impl.ArrayOptimizationState
import org.specs2.mutable.SpecificationWithJUnit
import org.apache.commons.io.FileUtils
import java.io.File

class ArrayOptimizationStateSpec
  extends SpecificationWithJUnit
{
  //--------------------------------------------------------------------------------------------------------------------
  val testDir = "work/test"

  def dir(name: String): String =
    s"$testDir/$name"

  def cleanup(): Unit = {
    FileUtils.deleteDirectory(new File(testDir))
  }


  //--------------------------------------------------------------------------------------------------------------------
  "Array optimization state" should {

    "When empty" in {
      val empty = new ArrayOptimizationState

      "Be written" in {
        val path = dir("empty")
        empty.write(path)

        "And read back out" in {
          val read = ArrayOptimizationState.readOrEmpty(path)

          "As empty" in {
            empty must be equalTo read
          }
        }
      }
    }

    "When populated" in {
      val populated = new ArrayOptimizationState

      populated.regretStore.commit(Map(42L -> Seq(1, 2)))
      populated.strategyStore.commit(Map(0L -> Seq(42)))

      "Be written" in {
        val path = dir("populated")
        populated.write(path)

        "And read back out" in {
          val read = ArrayOptimizationState.readOrEmpty(path)

          "As populated" in {
            populated must be equalTo read
          }
        }
      }
    }

    "When non-existing" in {
      val nonExisting = ArrayOptimizationState.readOrEmpty(dir("non-existing"))

      "Is empty" in {
        nonExisting must be equalTo new ArrayOptimizationState
      }
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  step(cleanup())
}