package com.gnifin.url_shortener.database

import com.twitter.util.Future

trait Database {
  def get(key: String): Future[Option[String]]
  def put(key: String, value: String): Future[Unit]
}
