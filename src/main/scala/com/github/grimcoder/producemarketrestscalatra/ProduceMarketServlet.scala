package com.github.grimcoder.producemarketrestscalatra

import java.io.InputStream
import java.text.SimpleDateFormat
import com.github.grimcoder.producemarketrestscalatra.dao.DataAccess
import com.github.grimcoder.producemarketrestscalatra.model.{PriceChange, Sale, Price}
import net.liftweb.json._
import org.scalatra._
import org.scalatra.liftjson.LiftJsonSupport
import org.scalatra.scalate.ScalateSupport



import scala.util.control.Exception

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
      val filtered = DataAccess.salesfilter(_.Id == Some(id))
      Extraction.decompose(
        filtered
      )
    }
    else Extraction.decompose(DataAccess.sales)
  }

  get ("/api/reports/prices"){
    history = history.length match {
      case 0 => Serialization.read[List[PriceChange]](hlines);
      case _ => history
    }

    Extraction.decompose(history)
  }

  post("/api/prices") {

    val price: Price = parsedBody.extract[Price]
    price.Id match  {
      case None => {
        val maxId = prices.map(_.Id.get.toInt).max + 1
        val newPrice = Price(Some(maxId.toString), price.Price, price.ItemName)
        val newHistory = PriceChange(newPrice.Id, newPrice.Price, newPrice.ItemName, None, "New" )

        prices = newPrice :: prices
        history = newHistory :: history

      }
      case Some(id) => {
        val oldPrice = prices.filter(_.Id==price.Id).head
        prices = prices.filterNot(_.Id==price.Id)
        prices = price :: prices
        val newHistory = PriceChange(price.Id, price.Price, price.ItemName, Some(oldPrice.Price), "Edit" )

        prices = price :: prices
        history = newHistory :: history
      }
    }
  }

  post("/api/sales") {

    val sale: Sale = parsedBody.extract[Sale]
    sale.Id match  {
      case None => {
        val maxId = sales.map(_.Id.get.toInt).max + 1
        val newPrice = Sale(Some(maxId.toString), sale.Date, sale.SaleDetails)
        sales = newPrice :: sales
      }
      case Some(id) => {
        sales = sales.remove(_.Id==sale.Id)
        sales = sale :: sales
      }
    }
  }

  delete("/api/prices"){
    val id = params("id").toString;
    val oldPrice = prices.filter(_.Id==Some(id)).head

    prices = prices.filter(price => price.Id != Some(id));
    val newHistory = PriceChange(oldPrice.Id, oldPrice.Price, oldPrice.ItemName, Some(oldPrice.Price), "Delete" )
    history = newHistory :: history

  }

  delete("/api/sales"){
    val id = params("id").toString;
    sales = sales.filter(price => price.Id != Some(id));
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
