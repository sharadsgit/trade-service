package com.trade.model

case class Stock(
  val symbol: String,
  indexType: String = "NSE",
  currentNAV: Float = 0, //Always fetched
  otherInfo: String //To be decided in future
)
