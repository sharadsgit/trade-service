package com.trade.http

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import com.trade.model.Trade

import scala.concurrent.Future
//import UserRegistryActor._
import com.trade.tradeKeeper.Trades
import com.trade.tradeKeeper.TradeKeeperActor._
import akka.pattern.ask
import akka.util.Timeout
//import com.trade.tradeKeeper.TradeKeeperActor.{AddTrade, DeleteTrade, GetTrade, GetTrades}

//#user-routes-class
trait TradeRoutes extends JsonSupport {
  //#user-routes-class

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[TradeRoutes])

  // other dependencies that TradeRoutes use
  def tradeKeeperActor: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  //#all-routes
  //#trades-get-post
  //#trades-get-delete
  lazy val tradeRoutes: Route =
    pathPrefix("trades") {
      concat(
        //#trades-get-delete
        pathEnd {
          concat(
            get {
              val trades: Future[Trades] =
                (tradeKeeperActor ? GetTrades).mapTo[Trades]
              complete(trades)
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
        //#users-get-post
        //#users-get-delete
        path(Segment) { symbol =>
          concat(
            get {
              //#retrieve-user-info
              val maybeTrade: Future[Option[Trade]] =
                (tradeKeeperActor ? GetTrade(symbol)).mapTo[Option[Trade]]
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

}