package services

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.basic.DatabaseConfig
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.{JdbcBackend, JdbcProfile}

trait DbService {

  def usingDB[T](f: => DBIOAction[T, NoStream, Nothing])(implicit dbConfigProvider: DatabaseConfigProvider): Future[T] = {
    val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
    val db: JdbcBackend#DatabaseDef = dbConfig.db

    db.run(f)
  }
}

