package com.trade.model

case class Account(
  val accountId: String,
  brokerId: Option[String],
  clientId: Option[String])
