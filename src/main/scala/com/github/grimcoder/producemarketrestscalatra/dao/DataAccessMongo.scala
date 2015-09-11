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
  override var history: List[PriceChange] = _

  override def deletePrice(id: String): Unit = ()

  override def postPrices(price: Price): Unit = ()

  override def salesfilter(id: String): List[Sale] = db("sales")
    .find(MongoDBObject("_id" -> new ObjectId(id)))
    .toList.map(toSale)

  override def postSale(sale: Sale): Unit = ()

  override def pricesFilter(id: String): List[Price] =

    db("prices").find(MongoDBObject("_id" -> new ObjectId(id))).toList.map(

    obj =>
      grater[Price].asObject(obj)
  )

  override def deleteSale(id: String): Unit = ()

  override var sales: List[Sale] =

    db("sales").find.toList.map(toSale)

  def toSale (o:DBObject) : Sale = Sale(Some(o("_id").toString),
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

  override var prices: List[Price] =
    db("prices").find.toList.map(
      obj =>
      grater[Price].asObject(obj)
    )

}
