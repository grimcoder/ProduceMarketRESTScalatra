package com.github.grimcoder.producemarketrestscalatra.dao

import com.github.grimcoder.producemarketrestscalatra.model.{PriceChange, Price, Sale}
import com.mongodb.casbah.commons.ValidBSONType.ObjectId
import com.novus.salat._
import com.novus.salat.global._


/**
 * Created by taras.kovtun on 9/9/2015.
 */
object DataAccessMongo extends DataAccess{
  import com.mongodb.casbah.Imports._

  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("ProduceMarket")

  override var history: List[PriceChange] = _

  override def deletePrice(id: String): Unit = ()

  override def postPrices(price: Price): Unit = ()

  override def salesfilter(id: String): List[Sale] = null

  override def postSale(sale: Sale): Unit = ()

  override def pricesFilter(id: String): List[Price] =

    db("prices").find(MongoDBObject("_id" -> new ObjectId(id))).toList.map(

    obj =>
      grater[Price].asObject(obj)
  )

  override def deleteSale(id: String): Unit = ()

  override var sales: List[Sale] = _
  var test = db("prices").find.toList.head

  override var prices: List[Price] =
    db("prices").find.toList.map(
      obj =>
      grater[Price].asObject(obj)
    )

}