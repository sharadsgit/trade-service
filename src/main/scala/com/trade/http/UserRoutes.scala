package com.trade.http

import java.io.File

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import com.trade.tradeKeeper.Trades

import scala.concurrent.Future
import UserRegistryActor._
import akka.pattern.ask
import akka.util.Timeout
import com.trade.model.Trade
import com.trade.tradeKeeper.TradeKeeperActor.{ ActionPerformed, AddTrade, DeleteTrade, GetTrade, GetTrades }

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{ read, write }

//#user-routes-class
trait UserRoutes extends JsonSupport {
  //#user-routes-class

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[UserRoutes])

  // other dependencies that UserRoutes use
  def userRegistryActor: ActorRef
  // other dependencies that TradeRoutes use
  def tradeKeeperActor: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration
  implicit val formats = Serialization.formats(NoTypeHints)

  //#all-routes
  //#users-get-post
  //#users-get-delete
  lazy val userRoutes: Route =
    pathPrefix("users") {
      concat(
        //#users-get-delete
        pathEnd {
          concat(
            get {
              val users: Future[Users] =
                (userRegistryActor ? GetUsers).mapTo[Users]
              complete(users)
            },
            post {
              entity(as[User]) { user =>
                val userCreated: Future[ActionPerformed] =
                  (userRegistryActor ? CreateUser(user)).mapTo[ActionPerformed]
                onSuccess(userCreated) { performed =>
                  log.info("Created user [{}]: {}", user.name, performed.description)
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        //#users-get-post
        //#users-get-delete
        path(Segment) { name =>
          concat(
            get {
              //#retrieve-user-info
              val maybeUser: Future[Option[User]] =
                (userRegistryActor ? GetUser(name)).mapTo[Option[User]]
              rejectEmptyResponse {
                complete(maybeUser)
              }
              //#retrieve-user-info
            },
            delete {
              //#users-delete-logic
              val userDeleted: Future[ActionPerformed] =
                (userRegistryActor ? DeleteUser(name)).mapTo[ActionPerformed]
              onSuccess(userDeleted) { performed =>
                log.info("Deleted user [{}]: {}", name, performed.description)
                complete((StatusCodes.OK, performed))
              }
              //#users-delete-logic
            })
        })
      //#users-get-delete
    }
  //#all-routes

  import akka.http.scaladsl.server.directives.ContentTypeResolver.Default
  val resourcePrefix = "css"
  lazy val tradeRoutes: Route =
    path(resourcePrefix / Remaining) { resource =>
      getFromResource(resource)
    } ~
    pathPrefix("index") {
      //      var f = new File("src/main/webapps/WEB-INF/index.html")
      //      getFromFile(f, ContentType(MediaTypes.`text/html`))
      //      getFromResource("WEB-INF/index.html", ContentType(MediaTypes.`text/html`)
      getFromFile("src/main/webapps/WEB-INF/index.html")
    } ~ pathPrefix("trades") {
      concat(
        //#trades-get-delete
        pathEnd {
          concat(
            get {
              log.info("Request received")
              val trades: Future[Trades] = (tradeKeeperActor ? GetTrades).mapTo[Trades]
              onSuccess(trades) {x => complete(write(x)) }
            },
            post {
              entity(as[Trade]) { trade =>
                val tradeCreated: Future[ActionPerformed] =
                  (tradeKeeperActor ? AddTrade(trade)).mapTo[ActionPerformed]
                onSuccess(tradeCreated) { performed =>
                  log.info("Created trade [{}]: {}", trade.stock.symbol, performed.description)
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        path("" / IntNumber) { id =>
          (get | put) {
            extractMethod { m =>
              m.name()
              complete("")
            }
          }
        },
        //#users-get-post
        //#users-get-delete
        path(Segment) { symbol =>
          concat(
            get {
              //#retrieve-user-info
              log.info("Request received with symbol : " + symbol)
              log.info("Segment value : " + Segment)
              val maybeTrade: Future[Option[Trade]] =
                (tradeKeeperActor ? GetTrade(symbol)).mapTo[Option[Trade]]
              log.info("Request response with  maybeTrade : " + maybeTrade.toString)
              rejectEmptyResponse {
                complete(maybeTrade)
              }
              //#retrieve-user-info
            },
            delete {
              //#users-delete-logic
              val tradeDeleted: Future[ActionPerformed] =
                (tradeKeeperActor ? DeleteTrade(symbol)).mapTo[ActionPerformed]
              onSuccess(tradeDeleted) { performed =>
                log.info("Deleted trade [{}]: {}", symbol, performed.description)
                complete((StatusCodes.OK, performed))
              }
              //#trade-delete-logic
            })
        })
      //#trades-get-delete
    }
  //#all-routes

  lazy val libraryRoutes: Route =
    pathPrefix("books") {
      concat(
        //#users-get-delete
        pathEnd {
          concat(
            get {
              val users: Future[Users] =
                (userRegistryActor ? GetUsers).mapTo[Users]
              complete(users)
            },
            post {
              entity(as[User]) { user =>
                val userCreated: Future[ActionPerformed] =
                  (userRegistryActor ? CreateUser(user)).mapTo[ActionPerformed]
                onSuccess(userCreated) { performed =>
                  log.info("Created user [{}]: {}", user.name, performed.description)
                  complete((StatusCodes.Created, performed))
                }
              }
            })
        },
        //#users-get-post
        //#users-get-delete
        path(Segment) { name =>
          concat(
            get {
              //#retrieve-user-info
              val maybeUser: Future[Option[User]] =
                (userRegistryActor ? GetUser(name)).mapTo[Option[User]]
              rejectEmptyResponse {
                complete(maybeUser)
              }
              //#retrieve-user-info
            },
            delete {
              //#users-delete-logic
              val userDeleted: Future[ActionPerformed] =
                (userRegistryActor ? DeleteUser(name)).mapTo[ActionPerformed]
              onSuccess(userDeleted) { performed =>
                log.info("Deleted user [{}]: {}", name, performed.description)
                complete((StatusCodes.OK, performed))
              }
              //#users-delete-logic
            })
        })
      //#users-get-delete
    }
  //#all-routes

}
