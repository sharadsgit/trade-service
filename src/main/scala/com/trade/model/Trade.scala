package com.trade.model

final case class Trade(
  stock: Stock,
  account: Account,
  txnType: String,
  invNAV: Float,
  quantity: Int)
