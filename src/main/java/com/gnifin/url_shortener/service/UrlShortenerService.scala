package com.gnifin.url_shortener.service

import java.util.Base64
import com.gnifin.url_shortener.database.Database
import com.twitter.finagle._
import com.twitter.finagle.httpx.{Method, Request}
import com.twitter.util.Future

import scala.util.hashing.MurmurHash3

/**
 * The Url Shortener service implemented as an Finagle HTTP service.
 *
 * @param database Database to use for storing mappings between hashes and urls.
 * @param baseUrl Base url that should be returned to the caller appended with the hash.
 */
class UrlShortenerService(database: Database, baseUrl: String) extends Service[httpx.Request, httpx.Response] {

  private val base64 = Base64.getUrlEncoder.withoutPadding()
  private val RedirectUrlPatten = "/(.*)".r

  override def apply(request: httpx.Request): Future[httpx.Response] = {
    (request.method, request.path) match {
      case (Method.Put, "/")                     => shortUrlResponse(request)
      case (Method.Get, RedirectUrlPatten(hash)) => redirectResponse(request, hash)
      case _                                     => notFoundResponse(request)
    }
  }

  def shortUrlResponse(request: httpx.Request): Future[httpx.Response] = {
    request.params.get("url").map(url => {
      val hash: String = hashUrl(url)
      val shortUrl: String = createShortUrl(hash)
      database.put(hash, url).map(_ => {
        val response = httpx.Response()
        response.location = shortUrl
        response.status = httpx.Status.Created
        response
      })
    }).getOrElse(Future.value(httpx.Response(httpx.Status.BadRequest)))
  }

  def redirectResponse(request: httpx.Request, hash: String): Future[httpx.Response] = {
    database.get(hash).map(url => {
      url.map(u => {
        val response = httpx.Response()
        response.status = httpx.Status.MovedPermanently
        response.location = u
        response
      }).getOrElse(httpx.Response(httpx.Status.NotFound))
    })
  }

  def notFoundResponse(request: Request) = {
    Future.value(httpx.Response(httpx.Status.NotFound))
  }

  def hashUrl(url: String): String = {
    val hash = MurmurHash3.stringHash(url)
    base64.encodeToString(BigInt(hash).toByteArray)
  }

  def createShortUrl(hash: String): String = {
    baseUrl + hash
  }
}
