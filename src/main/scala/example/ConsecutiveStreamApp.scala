package example

import zio.console._
import zio.stream.{Sink, ZStream}
import zio.{App, ExitCode, ZIO}

import java.io.IOException

object ProductCatalog {
  case class LocalizedProduct(id: Int, name: String)

//  object LocalizedProduct {
//    implicit val toId: GetId[LocalizedProduct] = (t: LocalizedProduct) => t.id
//  }

  val deProducts: List[LocalizedProduct] = List(1, 2, 5, 8, 9, 10, 12).map(id =>
    LocalizedProduct(id, s"Deutsches Produkt mit id $id")
  )
  val enProducts: List[LocalizedProduct] = List(2, 4, 8, 9, 11).map(id =>
    LocalizedProduct(id, s"English Product with id $id")
  )
}

object ConsecutiveStream {
  def consecutiveStream(
      stream: ZStream[Any, Nothing, Int],
      maxId: Int
  ): ZStream[Any, Nothing, (Int, Option[Int])] = {
    stream.zipWithPreviousAndNext
      .flatMap {

        case (None, i, _) =>
          ZStream(i -> Some(i))

        case (_, i, Some(next)) =>
          val fillerElementsCurrentNext =
            if (next - i > 1) ZStream.range(i + 1, next).map(i => i -> None)
            else ZStream.empty

          ZStream(
            i -> Some(i)
          ) ++ fillerElementsCurrentNext

        case (_, i, None) =>
          ZStream(
            i -> Some(i)
          ) ++ ZStream.range(i + 1, maxId + 1).map(i => i -> None)
      }
  }
}

object ConsecutiveStreamApp extends App {

  def run(args: List[String]): zio.URIO[Console, ExitCode] =
    myAppLogic.fold(_ => ExitCode.failure, _ => ExitCode.success)

  val myAppLogic: ZIO[Console, IOException, Unit] = {
    val stream: ZStream[Any, Nothing, Int] = ZStream(2, 3, 5, 6, 9)

    val testStream = ConsecutiveStream.consecutiveStream(stream, maxId = 10)

    for {
      result <- testStream.run(Sink.collectAll)
      _ <- putStrLn("All elements (consecutive):")
      _ <- ZIO.succeed(result.foreach(println))
      _ <- putStrLn("Done")
    } yield ()
  }
}

//object Old {
//  val maxId = Math.max(
//    ProductCatalog.deProducts.maxBy(_.id).id,
//    ProductCatalog.enProducts.maxBy(_.id).id
//  )
//  val minId = Math.min(
//    ProductCatalog.deProducts.minBy(_.id).id,
//    ProductCatalog.enProducts.minBy(_.id).id
//  )
//
//  def toMd(localizedProduct: LocalizedProduct): String = {
//    s"| ${localizedProduct.id} | ${localizedProduct.name} |"
//  }
//
//  println("## deProducts")
//  println("| id | name |")
//  println("| -------: | ------- |")
//  ProductCatalog.deProducts.foreach(p => println(toMd(p)))
//
//  println("## enProducts")
//  println("| id | name |")
//  println("| -------: | ------- |")
//  ProductCatalog.enProducts.foreach(p => println(toMd(p)))
//
//  println("## zipped")
//  displayZippedCatalog(zipped)
//
//  println("## cleaned up (empty elements removed)")
//  displayZippedCatalog(cleaned)
//
//  def displayZippedCatalog(
//      products: Seq[(Payload[LocalizedProduct], Payload[LocalizedProduct])]
//  ) = {
//    println("| de id | de product | en id| en product")
//    println("| -------: | ------- | ------- | ------- |")
//
//    def getName(payload: Payload[LocalizedProduct]) = {
//      payload match {
//        case WithPayload(LocalizedProduct(_, name)) => name
//        case EmptyPayload(_)                        => "---"
//      }
//    }
//
//    products.foreach { case (de, en) =>
//      println(
//        s"| ${id.getId(de)} | ${getName(de)} | ${id.getId(en)} | ${getName(en)} |"
//      )
//
//    }
//
//  }
//
//  Await.ready(system.terminate, 5.seconds)
//
//}
