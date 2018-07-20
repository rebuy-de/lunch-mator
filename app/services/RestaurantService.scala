package services

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import play.api.db.slick.DatabaseConfigProvider

import mappers.RestaurantMapper
import models.{CreateRestaurantDto, RestaurantRow, UserRow}
import persistence.repository.Restaurants

class RestaurantService @Inject()(val userService: UserService)(implicit val dbConfigDataProvider: DatabaseConfigProvider, ec: ExecutionContext) extends DbService
{

  def searchRestaurant(name: String): Future[Seq[RestaurantRow]] = usingDB {
    Restaurants.searchRestaurant(name)
  }

  def loadRestaurant(id: Int): Future[Option[RestaurantRow]] = usingDB {
    Restaurants.getRestaurantsById(id)
  }

  def createNewRestaurant(restaurant: RestaurantRow): Future[Int] = usingDB {
    Restaurants.addRestraurant(restaurant)
  }

  def createNewRestaurant(restaurantDto: CreateRestaurantDto, user: UserRow): Future[Int] = usingDB {
    val restaurantRow = RestaurantMapper.map(restaurantDto, user)
    Restaurants.addRestraurant(restaurantRow)
  }

  def getRandomRestaurant(): Future[RestaurantRow] = usingDB {
    Restaurants.selectRandomRestaurant()
  }

  def getAllRestaurants: Future[Seq[RestaurantRow]] = usingDB {
    Restaurants.getAll
  }

  def getRestaurantByName(name: String): Future[Option[RestaurantRow]] = usingDB {
    Restaurants.getRestaurantsByName(name)
  }
}
