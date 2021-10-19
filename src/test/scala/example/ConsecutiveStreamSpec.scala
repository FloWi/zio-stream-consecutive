package example

import zio.stream.{Sink, ZStream}
import zio.test.Assertion._
import zio.test._

object ConsecutiveStreamSpec extends DefaultRunnableSpec {
  val input = List(2, 3, 5, 6, 9)
  val stream = ZStream.fromIterable(input)
  val consecutiveStream =
    ConsecutiveStream.consecutiveStream(stream, maxId = 10)

  def spec = suite("ConsecutiveStream")(
    testM("consecutiveStream should generate the correct number of elements") {
      for {
        result <- consecutiveStream
          .run(Sink.collectAll)
          .map(_.toList)
      } yield assert(result.map { case (id, _) => id })(
        hasSameElements(2.to(10))
      )
    },
    testM(
      "consecutiveStream emit Some for the present elements in the stream"
    ) {
      for {
        result <- consecutiveStream
          .run(Sink.collectAll)
          .map(_.toList)
      } yield assert(result.collect { case (_, Some(id)) => id })(
        hasSameElements(input)
      )
    }
  )
}
