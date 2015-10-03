package com.gnifin.url_shortener

import com.twitter.util.{Throw, Return}
import org.scalatest.concurrent.Futures
import org.scalatest.time.{Seconds, Span}

/**
 * Trait for working with Twitter Futures in ScalaTest. Duplicated in unit and integration test code
 * as they cannot share code (should be looked at though...).
 */
trait TwitterFutures extends Futures {

  import scala.language.implicitConversions

  implicit def convertTwitterFuture[T](twitterFuture: com.twitter.util.Future[T]): FutureConcept[T] =
    new FutureConcept[T] {
      override def eitherValue: Option[Either[Throwable, T]] = {
        twitterFuture.poll.map {
          case Return(o) => Right(o)
          case Throw(e)  => Left(e)
        }
      }
      override def isCanceled: Boolean = false
      override def isExpired: Boolean = false
    }

  implicit val asyncConfig = PatienceConfig(timeout = scaled(Span(2, Seconds)))
}

