import zio.test._
import zio.test.Assertion._

object ZioFirstTest extends DefaultRunnableSpec {

  def spec = suite("ZioFirstTest")(
    suite("Unit Tests")(
      test("It's working"){
        println("First Test")
        assert(true)(isTrue)
      }
    )
  )

}
