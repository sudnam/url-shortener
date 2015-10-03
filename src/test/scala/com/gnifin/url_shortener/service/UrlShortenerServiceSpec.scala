package com.gnifin.url_shortener.service

import com.gnifin.url_shortener.database.Database
import com.twitter.finagle._
import com.twitter.finagle.httpx.{Method, Version}
import com.twitter.util.{Await, Future}
import org.scalamock.scalatest.MockFactory
import org.scalatest._

class UrlShortenerServiceSpec extends FlatSpec with Matchers with OneInstancePerTest
  with OptionValues with MockFactory {

  val baseUrl = "http://test.com"
  val database = mock[Database]
  val urlShortenerService = new UrlShortenerService(database, baseUrl)

  behavior of "A UrlShortenerService"

  it should "return 404 Not Found for an invalid request" in {
    val request = httpx.Request(Version.Http11, Method.Post, "/")
    val response = Await.result(urlShortenerService.apply(request))

    response.status should be (httpx.Status.NotFound)
  }

  it should "return a 400 Bad Request response for a Put request without an url" in {
    val request = httpx.Request(Version.Http11, Method.Put, "/")
    val response = Await.result(urlShortenerService.apply(request))

    response.status should be (httpx.Status.BadRequest)
  }

  it should "return a 204 Created response with a short url for a Put request with an url" in {
    val url = "http://www.google.com"
    val request = httpx.Request(Version.Http11, Method.Put, "/?url=" + url)

    database.put _ expects(*, url) returning Future.value(())

    val response = Await.result(urlShortenerService.apply(request))

    response.status should be (httpx.Status.Created)
    response.location.value should startWith (baseUrl)
  }

  it should "return a 404 Not Found when an invalid hash is sent in" in {
    val hash = "hash"
    val request = httpx.Request(Version.Http11, Method.Get, "/" + hash)

    database.get _ expects hash returning Future.value(Option.empty)

    val response = Await.result(urlShortenerService.apply(request))

    response.status should be (httpx.Status.NotFound)
  }

  it should "redirect with a 301 Moved Permanently when a short url is used" in {
    val url = "http://www.google.com"
    val hash = "hash"
    val request = httpx.Request(Version.Http11, Method.Get, "/" + hash)

    database.get _ expects hash returning Future.value(Option.apply(url))

    val response = Await.result(urlShortenerService.apply(request))

    response.status should be (httpx.Status.MovedPermanently)
    response.location.value should be (url)
  }

  it should "create the same short url for the same long url" in {
    val url = "http://www.google.com"
    val request = httpx.Request(Version.Http11, Method.Put, "/?url=" + url)

    database.put _ expects(*, url) returning Future.value(()) twice()

    val response1 = Await.result(urlShortenerService.apply(request))
    val response2 = Await.result(urlShortenerService.apply(request))

    response1.status should be (httpx.Status.Created)
    response2.status should be (httpx.Status.Created)

    response1.location.value shouldEqual response2.location.value
  }
}
