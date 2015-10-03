package com.gnifin.url_shortener.database

import com.twitter.util.Future

/**
 * Trait for the Database access needed in the service.
 */
trait Database {
  def get(key: String): Future[Option[String]]
  def put(key: String, value: String): Future[Unit]
}
