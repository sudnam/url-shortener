package com.gnifin.url_shortener.database

import com.twitter.util.Future

import scala.collection.mutable

class MemoryDatabase extends Database {

  private val data = mutable.Map[String, String]()

  override def get(key: String): Future[Option[String]] = {
    Future.value(data.get(key))
  }

  override def put(key: String, value: String): Future[Unit] = {
      Future.value(data.put(key, value))
  }
}
