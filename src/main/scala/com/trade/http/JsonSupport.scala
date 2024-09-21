package com.trade.http

import UserRegistryActor.ActionPerformed
import com.trade.model.{ Account, Stock, Trade }
import com.trade.tradeKeeper.{ TradeKeeperActor, Trades }

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val stockJsonFormat = jsonFormat4(Stock)
  implicit val accountJsonFormat = jsonFormat3(Account)
  implicit val tradeJsonFormat = jsonFormat5(Trade)
  implicit val tradesJsonFormat = jsonFormat1(Trades)
  implicit val tradePerformedJsonFormat = jsonFormat1(TradeKeeperActor.ActionPerformed)

}
//#json-support
