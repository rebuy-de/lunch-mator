package persistence.repository

import models.RestaurantRow
import slick.dbio.Effect.{Read, Write}
import slick.jdbc.PostgresProfile.api._
import slick.sql.{FixedSqlAction, FixedSqlStreamingAction, SqlAction}

class Restaurants(tag: Tag) extends Table[RestaurantRow](tag, Some("lunch_world"), "restaurants") {

  def id = column[Int]("id", O.AutoInc, O.PrimaryKey)

  def name = column[String]("name")

  def website = column[String]("website")

  def description = column[String]("description")

  def userId = column[Int]("added_by_user_id")

  override def * = (id.?, name, website, description.?, userId) <> (RestaurantRow.tupled, RestaurantRow.unapply _)
}

object Restaurants {

  lazy val restaurants = TableQuery[Restaurants]

  def getAll = {
    restaurants.sortBy(a => a.name.toLowerCase).result
  }

  def searchRestaurant(name: String): FixedSqlStreamingAction[Seq[RestaurantRow], RestaurantRow, Read] = {
    restaurants.filter(_.name.toLowerCase like "%" + name.toLowerCase + "%").result
  }

  def selectRandomRestaurant(): SqlAction[RestaurantRow, NoStream, Read] = {
    val rand = SimpleFunction.nullary[Double]("random")
    restaurants.sortBy(x => rand).take(1).result.head
  }

  def addRestraurant(restaurant: RestaurantRow): FixedSqlAction[Int, NoStream, Write] = {
    restaurants += restaurant
  }

  def getRestaurantsById(restaurant_id: Int): SqlAction[Option[RestaurantRow], NoStream, Read] = {
    restaurants.filter(_.id === restaurant_id).result.headOption
  }

  def getRestaurantsByName(name: String): SqlAction[Option[RestaurantRow], NoStream, Read] = {
    restaurants.filter(_.name === name).result.headOption
  }
}
