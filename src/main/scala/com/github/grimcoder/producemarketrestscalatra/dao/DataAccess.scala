package com.github.grimcoder.producemarketrestscalatra.dao

import java.io.{PrintWriter, File, OutputStream, InputStream}
import java.text.SimpleDateFormat

import com.github.grimcoder.producemarketrestscalatra.model.{PriceChange, Sale, Price}
import net.liftweb.json.{DefaultFormats, Serialization}

object DataAccess {

  implicit val formatsz = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

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

  var prices = Serialization.read[List[Price]](lines);

  val sstream: InputStream  = getClass.getResourceAsStream("/sales.json")

  val slines = scala.io.Source.fromInputStream(sstream).getLines.mkString

  var sales : List[Sale] =  Serialization.read[List[Sale]](slines);

  val hstream: InputStream  = getClass.getResourceAsStream("/priceChanges.json")

  var hlines = scala.io.Source.fromInputStream(sstream).getLines.mkString

  var history : List[PriceChange] = Serialization.read[List[PriceChange]](hlines);

  def pricesFilter(id: String) = prices.filter( price=> price.Id == Some(id))

  def salesfilter(id: String) = sales.filter(_.Id == Some(id))

  def postPrices(price: Price) =  price.Id match {
    case None => {
      val maxId = prices.map(_.Id.get.toInt).max + 1
      val newPrice = Price(Some(maxId.toString), price.Price, price.ItemName)
      val newHistory = PriceChange(newPrice.Id, newPrice.Price, newPrice.ItemName, None, "New")

      prices = newPrice :: prices
      history = newHistory :: history
    }

    case Some(id) => {
      val oldPrice = prices.filter(_.Id == price.Id).head
      prices = prices.filterNot(_.Id == price.Id)
      prices = price :: prices
      val newHistory = PriceChange(price.Id, price.Price, price.ItemName, Some(oldPrice.Price), "Edit")
      history = newHistory :: history
    }

      saveObjects
  }

  def postSale(sale: Sale) = sale.Id match {
    case None => {
      val maxId = sales.map(_.Id.get.toInt).max + 1
      val newPrice = Sale(Some(maxId.toString), sale.Date, sale.SaleDetails)
      sales = newPrice :: sales
    }
    case Some(id) => {
      sales = sales.filterNot(_.Id == sale.Id)
      sales = sale :: sales
    }

      saveObjects
  }

  def deletePrice(id: String) = {
    val oldPrice = prices.filter(_.Id == Some(id)).head

    prices = prices.filter(price => price.Id != Some(id));
    val newHistory = PriceChange(oldPrice.Id, oldPrice.Price, oldPrice.ItemName, Some(oldPrice.Price), "Delete")
    history = newHistory :: history

    saveObjects
  }

  def deleteSale(id: String) = {
    sales = sales.filter(price => price.Id != Some(id))

    saveObjects
  }


}
