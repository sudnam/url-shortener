package com.gnifin.url_shortener.database

import com.gnifin.url_shortener.TwitterFutures
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.redis.util.RedisCluster
import com.twitter.finagle.redis.{Client, Redis}
import org.scalatest._

/**
 * Integration tests for the Redis database. Assumes that Redis is installed
 * and the redis-server command is available in the path to be able to start
 * up Redis instances used in the testing.
 */
class RedisDatabaseSpec extends FlatSpec with Matchers with TwitterFutures with OptionValues with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    RedisCluster.start()
  }

  override def afterAll(): Unit = {
    client.release()
    RedisCluster.stopAll()
  }

  lazy val client = new Client(ClientBuilder()
    .codec(Redis())
    .hosts(RedisCluster.hostAddresses())
    .hostConnectionLimit(2)
    .build())
  lazy val database = new RedisDatabase(client)

  behavior of "The Redis database"

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
