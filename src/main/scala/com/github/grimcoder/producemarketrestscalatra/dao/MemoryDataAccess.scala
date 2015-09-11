package com.github.grimcoder.producemarketrestscalatra.dao

import java.io.{PrintWriter, File, OutputStream, InputStream}
import java.text.SimpleDateFormat

import com.github.grimcoder.producemarketrestscalatra.model.{PriceChange, Sale, Price}
import net.liftweb.json.{DefaultFormats, Serialization}

object MemoryDataAccess extends DataAccess {

  def saveObjects = {

    val stream = new PrintWriter(getClass.getResource("/prices.json").getPath)
    val lines = Serialization.write[List[Price]](prices);
    stream write lines
    stream close

    val sstream  = new PrintWriter(getClass.getResource("/sales.json").getPath)
    val slines = Serialization.write[List[Sale]](sales);
    sstream write slines
    sstream close

    val hstream = new PrintWriter(getClass.getResource("/priceChanges.json").getPath)
    val hlines = Serialization.write[List[PriceChange]](history);
    hstream write hlines
    hstream close

  }

  var stream: InputStream = getClass.getResourceAsStream("/prices.json")
  val lines = scala.io.Source.fromInputStream(stream).getLines.mkString
  var lprices = Serialization.read[List[Price]](lines);
  def prices = lprices

  val sstream: InputStream  = getClass.getResourceAsStream("/sales.json")
  val slines = scala.io.Source.fromInputStream(sstream).getLines.mkString
  var lsales = Serialization.read[List[Sale]](slines);
  def sales  =  lsales

  val hstream: InputStream  = getClass.getResourceAsStream("/priceChanges.json")
  var hlines = scala.io.Source.fromInputStream(sstream).getLines.mkString
  var lhistory = Serialization.read[List[PriceChange]](hlines);
  def history : List[PriceChange] = history

  def pricesFilter(id: String) = prices.filter( price=> price.Id == Some(id))

  def salesfilter(id: String) = sales.filter(_.Id == Some(id))

  def postPrices(price: Price) =  price.Id match {
    case None => {
      val maxId = prices.map(_.Id.get.toInt).max + 1
      val newPrice = Price(Some(maxId.toString), price.Price, price.ItemName)
      val newHistory = PriceChange(newPrice.Id, newPrice.Price, newPrice.ItemName, None, "New")

      lprices = newPrice :: lprices
      lhistory = newHistory :: lhistory
    }

    case Some(id) => {
      val oldPrice = prices.filter(_.Id == price.Id).head
      lprices = prices.filterNot(_.Id == price.Id)
      lprices = price :: prices
      val newHistory = PriceChange(price.Id, price.Price, price.ItemName, Some(oldPrice.Price), "Edit")
      lhistory = newHistory :: history
    }

      saveObjects
  }

  def postSale(sale: Sale) = sale.Id match {
    case None => {
      val maxId = sales.map(_.Id.get.toInt).max + 1
      val newPrice = Sale(Some(maxId.toString), sale.Date, sale.SaleDetails)
      lsales = newPrice :: sales
    }
    case Some(id) => {
      lsales = sales.filterNot(_.Id == sale.Id)
      lsales = sale :: sales
    }

      saveObjects
  }

  def deletePrice(id: String) = {
    val oldPrice = prices.filter(_.Id == Some(id)).head

    lprices = prices.filter(price => price.Id != Some(id));
    val newHistory = PriceChange(oldPrice.Id, oldPrice.Price, oldPrice.ItemName, Some(oldPrice.Price), "Delete")
    lhistory = newHistory :: history

    saveObjects
  }

    def deleteSale(id: String) = {
    lsales = sales.filter(price => price.Id != Some(id))

    saveObjects
  }


}
