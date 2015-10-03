package com.gnifin.url_shortener.database

import com.gnifin.url_shortener.TwitterFutures
import org.scalatest.{FlatSpec, Matchers, OneInstancePerTest, OptionValues}

/**
 * Unit test for the in memory database.
 */
class MemoryDatabaseSpec extends FlatSpec with Matchers with OneInstancePerTest with TwitterFutures with OptionValues {

  val database = new MemoryDatabase

  behavior of "The in memory database"

  it should "return empty for a key that does not exist" in {
    database.get("key").futureValue shouldBe None
  }

  it should "return the value of a previously stored key" in {
    database.put("key", "value")
    database.get("key").futureValue.value shouldBe "value"
  }

  it should "return the latest value of a previously stored key" in {
    database.put("key", "value1")
    database.put("key", "value2")
    database.get("key").futureValue.value shouldBe "value2"
  }
}
