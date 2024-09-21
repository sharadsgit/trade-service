package com.service

import scala.io._

case class BookInfo(author: String, title: String) {
  override def toString: String = String.format("Author %10s, Book Name: %10s", author, title)
}

object InvertedIndexExample extends App {

  def HOF(i:Int) :Int = {
    9
  }

  val bookList = List(
    "Carr"->"And so to Murder",
    "Carr"->"The Arabian Nights Murder",
    "Christie"->"The Bad Hatter Mystery",
    "Christie"->"The Seatle Mystery",
    "Carr"->"The Court Murder"
  )
  val fillerList = List("And","so","to","The"," ")

  def prepareInvertedIndex(list:List[(String,String)]) = {
    list.map { case (x,y) => y.split(" ").toList
        .filterNot(fillerList.contains(_))
        .map((_, BookInfo(x, y)))
    }.flatten.foldLeft(Map[String, List[BookInfo]]()){
      case (map, (token, bookInfo: BookInfo))=>
        map ++ Map(token.toLowerCase -> (List(bookInfo) ++ map.getOrElse(token.toLowerCase, Nil)))
    }
  }

  val invertedIndex = prepareInvertedIndex(bookList)

  def searchLibrary(toke: String) = invertedIndex.get(userInput.toLowerCase)
  def printAll(str:String*) = {

  }

  case class COLOUR() {}
//  case class REDNESS() extends COLOUR{} // {val value=1; val key=""}
  object RED extends COLOUR {val value=1; val key=""}
  object WHITE extends COLOUR


  println("invertedIndex: ")
//  invertedIndex.foreach(println)
  invertedIndex.foreach(x=> println(x._2))
  printf("%10.2f\n",5.3445)
  printf("%30s--%02d/%02d/%04d","your Date of birth",6,6,10)

//  invertedIndex.foreach()

  for(i <- 0 to 256) println(i.toChar)





  var userInput = ""
  while(userInput != "exit") {
    userInput = scala.io.StdIn.readLine("Enter token to search")
    println(s"Queried toke: $userInput result: ${invertedIndex.get(userInput.toLowerCase)}")
  }
  println("This marks the closure of main thread")
}