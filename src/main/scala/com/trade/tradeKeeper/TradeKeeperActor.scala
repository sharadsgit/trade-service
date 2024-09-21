package com.trade.tradeKeeper

import akka.actor.{ Actor, ActorLogging, Props }
import com.trade.model.Trade
import com.trade.tradeKeeper.TradeKeeperActor.{ ActionPerformed, AddTrade, DeleteTrade, GetTrade, GetTrades }

final case class Trades(trades: Seq[Trade])

object TradeKeeperActor {
  final case class ActionPerformed(description: String)
  final case class AddTrade(trade: Trade)
  final case class GetTrade(symbol: String)
  final case object GetTrades
  final case class DeleteTrade(symbol: String)
  final val props = Props[TradeKeeperActor]
}

class TradeKeeperActor extends Actor with ActorLogging {

  var trades = Set.empty[Trade]
  override def receive: Receive = {
    case GetTrade(symbol) => sender() ! trades.find(_.stock.symbol == symbol)
    case GetTrades =>

      log.info(s"${logPrefix()} # Processing trade request : $trades")
      sender() ! Trades(trades.toSeq)
    case AddTrade(trade) =>
      trades += trade
      log.info(s"${logPrefix()} # trade created with details : $trade")
      sender() ! ActionPerformed(s"trade created with details : $trade")
    case DeleteTrade(symbol) =>
      log.info(s"${logPrefix()} # trade deleted for symbol : $symbol")
      sender() ! ActionPerformed(s"trade deleted for symbol : $symbol")
  }

  def logPrefix() = s"Service Actor: ${self.toString()} from Actor: ${sender().toString()}"
}