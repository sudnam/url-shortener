package com.gnifin.url_shortener.database

import com.twitter.finagle.redis
import com.twitter.finagle.redis.util.{CBToString, StringToChannelBuffer}
import com.twitter.util.Future

class RedisDatabase(client: redis.Client, prefix: String = "surl") extends Database {

  override def get(key: String): Future[Option[String]] = {
    val prefixedKey = prefixKey(key)
   client.get(StringToChannelBuffer(prefixedKey)).map(value => {
      value.map(v => CBToString(v))
    })
  }

  override def put(key: String, value: String): Future[Unit] = {
    val prefixedKey = prefixKey(key)
    client.set(StringToChannelBuffer(prefixedKey), StringToChannelBuffer(value))
  }

  private def prefixKey(key: String): String = {
    prefix + key
  }
}
