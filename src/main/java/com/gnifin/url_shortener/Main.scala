package com.gnifin.url_shortener

import java.net.InetSocketAddress

import com.gnifin.url_shortener.database.{MemoryDatabase, RedisDatabase}
import com.gnifin.url_shortener.service.UrlShortenerService
import com.squeed.url_shortener.database.MemoryDatabase
import com.twitter.finagle.redis.{Client, Redis}
import com.twitter.finagle.builder.{ClientBuilder, ServerBuilder}
import com.twitter.finagle.httpx.Http

object Main {

  def main(args: Array[String]): Unit = {
    val parsedArgs = parseArguments(args.toList) match {
      case Left(a)      => a
      case Right(error) =>
        println(usage())
        println(error)
        sys.exit(1)
    }

    if (parsedArgs.help) {
      println(usage())
      sys.exit(0)
    }

    val port = parsedArgs.port.getOrElse(8080)
    val address = new InetSocketAddress(port)
    val database = parsedArgs.databaseHost.map(databaseHost => {
      val client = ClientBuilder()
        .name("redis")
        .codec(Redis())
        .hosts(databaseHost)
        .hostConnectionLimit(2)
        .build()
      new RedisDatabase(new Client(client))
    }).getOrElse(new MemoryDatabase)
    val service = new UrlShortenerService(database, parsedArgs.baseUrl)

    ServerBuilder()
      .codec(Http())
      .bindTo(address)
      .name("Url Shortener")
      .build(service)
  }

  def parseArguments(args: List[String]): Either[Arguments, String] = {
    val defaultArguments = Arguments(help = false, "", Option.empty, Option.empty)
    parseArguments(args, defaultArguments).fold(parsedArguments => {
      if (parsedArguments.baseUrl.isEmpty && !parsedArguments.help) {
        Right("A base url must be supplied")
      } else {
        Left(parsedArguments)
      }
    }, error => Right(error))
  }

  def parseArguments(args: List[String], acc: Arguments): Either[Arguments, String] = {
    args match {
      case "--help" :: tail                  => parseArguments(tail, acc.copy(help = true))
      case "--port" :: port :: tail          =>
        try {
          parseArguments(tail, acc.copy(port = Option.apply(port.toInt)))
        } catch {
          case e: NumberFormatException => Right("Invalid port number")
        }
      case "--database-host" :: dest :: tail => parseArguments(tail, acc.copy(databaseHost = Option.apply(dest)))
      case "--base" :: baseUrl :: tail       => parseArguments(tail, acc.copy(baseUrl = baseUrl))
      case invalidArgument :: tail           => Right("Unknown argument " + invalidArgument)
      case _                                 => Left(acc)
    }
  }

  def usage(): String = {
    """url_shortener --base <base_url> [--database-host <host>] [--port <port>]
      | --help                 Show this usage info
      | --base base_url        The base url to use for short urls
      | --database-host host   The Redis database to connect to in the format <host>:<port>.
      |                        If not supplied an in memory database will be used.
      | --port port            The port to listen on
    """.stripMargin
  }

  case class Arguments(help: Boolean, baseUrl: String, databaseHost: Option[String], port: Option[Int])
}
