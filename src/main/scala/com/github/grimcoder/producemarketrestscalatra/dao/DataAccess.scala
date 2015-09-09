package com.github.grimcoder.producemarketrestscalatra.dao

import java.text.SimpleDateFormat

import com.github.grimcoder.producemarketrestscalatra.model.{PriceChange, Sale, Price}
import net.liftweb.json.{Serialization, DefaultFormats}

/**
 * Created by taras.kovtun on 9/9/2015.
 */
abstract class DataAccess {

  implicit val formatsz = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  var history : List[PriceChange]

  var sales : List[Sale]

  var prices : List[Price]

  def pricesFilter(id: String) : List[Price]

  def salesfilter(id: String) : List[Sale]

  def postPrices(price: Price)

  def postSale(sale: Sale)

  def deletePrice(id: String)

  def deleteSale(id: String)

}
