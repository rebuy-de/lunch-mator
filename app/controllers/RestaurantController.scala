package controllers

import com.google.inject.Inject
import exceptions.RestaurantNotFoundException
import models.CreateRestaurantDto
import models.Formats._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.mvc.{Action, EssentialAction}
import scala.concurrent.ExecutionContext
import services.{AuthenticatedController, RestaurantService}

class RestaurantController @Inject()(restaurantsService: RestaurantService)(implicit ec: ExecutionContext, implicit val dbConfig: DatabaseConfigProvider) extends AuthenticatedController {

  def listRestaurants(): EssentialAction = async { request =>
    restaurantsService.getAllRestaurants.map(restaurants =>
      Ok(Json.toJson(restaurants))
    )
  }

  def addRestaurant(): Action[JsValue] = async(parse.json) { request =>
    val restaurant = request.body.as[CreateRestaurantDto]
    restaurantsService.createNewRestaurant(restaurant, request.userRow).map {
      result => Created(Json.toJson(result))
    }
  }

  def findRestaurant(name: String): EssentialAction = async { request =>
    restaurantsService.getRestaurantByName(name).map(restaurant =>
      Ok(Json.toJson(restaurant))
    )
  }

  def loadRestaurant(id: Int): EssentialAction = async { request =>
    restaurantsService.loadRestaurant(id).map { restaurant =>
      Ok(Json.toJson(restaurant.getOrElse(throw RestaurantNotFoundException(id))))
    }
  }

  def searchRestaurant(name: String): EssentialAction = async { request =>
    restaurantsService.searchRestaurant(name).map { restaurant =>
      Ok(Json.toJson(restaurant))
    }
  }

  def getRandomRestaurant: EssentialAction = async { request =>
    restaurantsService.getRandomRestaurant().map { restaurant =>
      Ok(Json.toJson(restaurant))
    }
  }
}
