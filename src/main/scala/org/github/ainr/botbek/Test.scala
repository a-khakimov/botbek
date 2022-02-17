//package org.github.ainr.botbek
//import cats.effect.{ExitCode, IO, IOApp}
//import org.github.ainr.botbek.conf.Config
//import org.github.ainr.botbek.tg.bot.BotBek
//import org.github.ainr.botbek.unsplash.module.UnsplashModule
//import org.http4s.blaze.client.BlazeClientBuilder
//import org.nspl.awtrenderer._
//import org.nspl.{RelFontSize, StrokeConf, line, xyplot}
//import telegramium.bots.{ChatIntId, InputPartFile}

//object Test extends IOApp {
//
//  val app: IO[Unit] = BlazeClientBuilder[IO]
//    .resource
//    .use { httpClient =>
//      for {
//        config <- Config.make[IO]()
//        bot = BotBek.make[IO](config.telegram, httpClient)
//        botFiber <- bot.start().start
//        unsplash = UnsplashModule[IO](config.unsplash, httpClient)
//        stat <- unsplash.unsplashService.getUserStatistics("ainr")
//        views = stat.views.historical.values.map(_.value.toDouble)
//        downloads = stat.downloads.historical.values.map(_.value.toDouble)
//        plot <-
//          IO.delay(xyplot(views -> line(stroke = StrokeConf(RelFontSize(1))))(
//            main = "Views"
//          ).build)
//        file <- IO.delay(renderToFile(plot, width = 2000))
//        _ <- IO.delay(println(file))
//        _ <- bot.sendPhoto(ChatIntId(174861972), InputPartFile(file))
//        _ <- botFiber.join
//      } yield ()
//    }
//
//  override def run(args: List[String]): IO[ExitCode] =
//    app.as(ExitCode.Success)
//}
