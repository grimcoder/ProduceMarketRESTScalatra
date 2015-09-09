package com.github.grimcoder.producemarketrestscalatra

import java.text.SimpleDateFormat
import com.github.grimcoder.producemarketrestscalatra.dao.DataAccess
import com.github.grimcoder.producemarketrestscalatra.model.{PriceChange, Sale, Price}
import net.liftweb.json._
import org.scalatra._
import org.scalatra.liftjson.LiftJsonSupport
import org.scalatra.scalate.ScalateSupport

class ProduceMarketServlet extends ScalatraServlet with ScalateSupport with LiftJsonSupport with CorsSupport {

  implicit val formatsz = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, X-HTTP-Method-Override, Content-Type, Cache-Control, Accept");
    response.setHeader("Access-Control-Allow-Methods", "HEAD,GET,PUT,POST,DELETE,OPTIONS");
    200
  }

  get("/api/prices") {
    if (request.parameters.contains("id")) {
      val id = params("id").toString
      Extraction.decompose(
        DataAccess.pricesFilter(id)
      )
    }
    else Extraction.decompose(DataAccess.prices)
  }

  get("/api/sales") {
    if (request.parameters.contains("id")) {
      val id = params("id").toString
      Extraction.decompose(
        DataAccess.salesfilter(id)
      )
    }
    else Extraction.decompose(DataAccess.sales)
  }

  get("/api/reports/prices") {
    Extraction.decompose(DataAccess.history)
  }

  post("/api/prices") {
    val price: Price = parsedBody.extract[Price]
    DataAccess.postPrices(price)
  }

  post("/api/sales") {
    val sale: Sale = parsedBody.extract[Sale]
    DataAccess.postSale(sale)
  }

  delete("/api/prices") {
    val id = params("id").toString;
    DataAccess.deletePrice(id)
  }

  delete("/api/sales") {
    val id = params("id").toString;
    DataAccess.deleteSale(id)
  }

  notFound {
    // remove content type in case it was set through an action
    contentType = null
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }
}
