package com.trade.model

case class User(
  val emailId: String,
  phoneNumber: Option[String] = None,
  firstName: Option[String] = None,
  lastName: Option[String] = None,
  age: Option[Int] = None)