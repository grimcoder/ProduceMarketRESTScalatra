package com.github.grimcoder.producemarketrestscalatra.dao

import java.io.InputStream

import com.github.grimcoder.producemarketrestscalatra.model.{PriceChange, Sale, Price}
import net.liftweb.json.Serialization

object DataAccess {

  var stream: InputStream = getClass.getResourceAsStream("/prices.json")

  val lines = scala.io.Source.fromInputStream(stream).getLines.mkString

  var prices = Serialization.read[List[Price]](lines);

  val sstream: InputStream  = getClass.getResourceAsStream("/sales.json")

  val slines = scala.io.Source.fromInputStream(sstream).getLines.mkString

  var sales : List[Sale] =  Serialization.read[List[Sale]](slines);

  val hstream: InputStream  = getClass.getResourceAsStream("/priceChanges.json")

  var hlines = scala.io.Source.fromInputStream(sstream).getLines.mkString

  var history : List[PriceChange] = List[PriceChange]() ;


  def pricesFilter(id: String) = prices.filter( price=> price.Id == Some(id))

  def salesfiler(id: String) = sales.filter(_.Id == Some(id))

}
