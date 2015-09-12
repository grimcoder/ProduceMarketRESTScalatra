package com.github.grimcoder.producemarketrestscalatra.dao

import java.text.SimpleDateFormat

import com.github.grimcoder.producemarketrestscalatra.model.{SaleDetail, PriceChange, Price, Sale}
import  java.util.Date
import com.novus.salat._
import com.novus.salat.global._

object DataAccessMongo extends DataAccess{
  import com.mongodb.casbah.Imports._

  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("ProduceMarket")
  val dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  override def history: List[PriceChange] = db("pricechanges").find.toList.map( obj => grater[PriceChange].asObject(obj))

  override def deletePrice(id: String) = {
    val oldPrice = pricesFilter(id).head
    db("prices").remove(MongoDBObject("_id" -> new ObjectId(id)))
    val newHistory = PriceChange(None, oldPrice.Price, oldPrice.ItemName, Some(oldPrice.Price), "Delete")
    db("pricechanges").insert(grater[PriceChange].asDBObject(newHistory))
  }

  override def postPrices(price: Price) = price.Id match {

    case None => {
      val jsonNewPrice = grater[Price].asDBObject(price)
      val  result = db("prices").insert(jsonNewPrice)
      val newId = jsonNewPrice.get("_id").toString
      val newHistory = PriceChange(None, price.Price, price.ItemName, None, "New")
      db("pricechanges").insert(grater[PriceChange].asDBObject(newHistory))
    }

      case Some(id) => {
        val oldPrice = pricesFilter(id).head

        val newPriceJson = grater[Price].asDBObject(price)

        newPriceJson("_id") = new ObjectId(id)

        db("prices").update(MongoDBObject("_id" -> new ObjectId(id)),newPriceJson)

        val newHistory = PriceChange(None, price.Price, price.ItemName, Some(oldPrice.Price), "Edit")

        db("pricechanges").insert(grater[PriceChange].asDBObject(newHistory))
      }
  }

  override def salesfilter(id: String): List[Sale] = db("sales")
    .find(MongoDBObject("_id" -> new ObjectId(id)))
    .toList.map(toSale)

  override def postSale(sale: Sale): Unit = sale.Id match {
    case None => {
      val jsonNewSale = grater[Sale].asDBObject(sale)
      jsonNewSale("Date") = dateFormatter.format(sale.Date)
      val  result = db("sales").insert(jsonNewSale)
    }
    case Some(id) => {
      val jsonSale = grater[Sale].asDBObject(sale)
      jsonSale("Date") = dateFormatter.format(sale.Date)
      jsonSale("_id") = new ObjectId(id)
      db("sales").update(MongoDBObject("_id" -> new ObjectId(id)),jsonSale)
    }

  }

  override def pricesFilter(id: String): List[Price] = id match {
    case "0" => List();
    case _ => db("prices").find(MongoDBObject("_id" -> new ObjectId(id))).toList.map(obj => grater[Price].asObject(obj))
  }

  override def deleteSale(id: String): Unit = db("sales").findAndRemove(MongoDBObject("_id" -> new ObjectId(id)))

  override def sales: List[Sale] = db("sales").find.toList.map(toSale)

  private def toSale (o:DBObject) : Sale = Sale(Some(o("_id").toString),
    dateFormatter.parse(o("Date").toString),
    o.getAs[BasicDBList]("SaleDetails").get.toList.map(
      i => i match {
        case s: BasicDBObject =>
          SaleDetail(s("ItemName").toString,
            s("Price").toString.toDouble,
            s("Units").toString.toInt)
      }
    )
  )

  override def prices: List[Price] = db("prices").find.toList.map( obj => grater[Price].asObject(obj))

}
