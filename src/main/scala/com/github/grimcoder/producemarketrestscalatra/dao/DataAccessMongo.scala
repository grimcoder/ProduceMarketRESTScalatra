package com.github.grimcoder.producemarketrestscalatra.dao

import com.github.grimcoder.producemarketrestscalatra.model.{Price, Sale}

/**
 * Created by taras.kovtun on 9/9/2015.
 */
class DataAccessMongo extends DataAccess{
  import com.mongodb.casbah.Imports._

  override def pricesFilter(id: String): Unit = ()

  override def deletePrice(id: String): Unit = ()

  override def postPrices(price: Price): Unit = ()

  override def salesfilter(id: String): Unit = ()

  override def postSale(sale: Sale): Unit = ()

  override def deleteSale(id: String): Unit = ()
}
