package com.github.grimcoder.producemarketrestscalatra

import java.io.InputStream
import java.text.SimpleDateFormat
import com.github.grimcoder.producemarketrestscalatra.model.{Sale, Price}
import net.liftweb.json._
import org.scalatra._
import org.scalatra.liftjson.LiftJsonSupport
import org.scalatra.scalate.ScalateSupport
import com.mongodb.casbah.Imports._

class ProduceMarketServlet extends ScalatraServlet with ScalateSupport with LiftJsonSupport with CorsSupport {

  var stream: InputStream = getClass.getResourceAsStream("/prices.json")

  val lines = scala.io.Source.fromInputStream(stream).getLines.mkString

  var prices = Serialization.read[List[Price]](lines);

  val sstream: InputStream  = getClass.getResourceAsStream("/sales.json")

  val slines = scala.io.Source.fromInputStream(sstream).getLines.mkString

  var sales : List[Sale] = List[Sale]() ;


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
      val filtered = prices.filter(price => price.Id == Some(id))
      Extraction.decompose(
        filtered
      )
    }
    else Extraction.decompose(prices)
  }

  get("/api/sales") {

    sales = sales.length match {
      case 0 => Serialization.read[List[Sale]](slines);
      case _ => sales;
    }


    if (request.parameters.contains("id")) {
      val id = params("id").toString

      val filtered = sales.filter(_.Id == Some(id))
      Extraction.decompose(
        filtered
      )
    }

    else Extraction.decompose(sales)
  }

  post("/api/prices") {

    val price: Price = parsedBody.extract[Price]
    price.Id match  {
      case None => {

        val maxId = prices.map(_.Id.get.toInt).max + 1

        val newPrice = Price(Some(maxId.toString), price.Price, price.ItemName)

        prices = newPrice :: prices
      }
      case Some(id) => {

        prices = prices.remove(_.Id==price.Id)

        prices = price :: prices

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
    prices = prices.filter(price => price.Id != Some(id));
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
