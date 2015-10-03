package com.gnifin.url_shortener

import Main.Arguments
import org.scalatest.{Matchers, EitherValues, FlatSpec}

/**
 * Unit tests for the command line argument parsing
 * in the main object.
 */
class MainSpec extends FlatSpec with Matchers with EitherValues {

  behavior of "The Main class"

  it should "Give an error on unknown arguments" in {
    val args = List("-u")
    Main.parseArguments(args).right.value should be ("Unknown argument -u")
  }

  it should "Give an error when no base url is given" in {
    Main.parseArguments(List()).right.value should be ("A base url must be supplied")
  }

  it should "Give an error when an invalid port is given" in {
    Main.parseArguments(List("--port", "invalid")).right.value should be ("Invalid port number")
  }

  it should "Return a Arguments object with options on valid arguments" in {
    val baseUrl = "http://sq.com"
    val databaseHost = "localhost:8182"
    val port = 9090
    val args = List("--database-host", databaseHost, "--port", port.toString, "--base", baseUrl)
    val expected = Arguments(help = false, baseUrl, Option.apply(databaseHost), Option.apply(port))
    Main.parseArguments(args).left.value should be (expected)
  }
}
