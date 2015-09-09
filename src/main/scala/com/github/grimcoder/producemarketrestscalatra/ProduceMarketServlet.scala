package com.github.grimcoder.producemarketrestscalatra

import java.text.SimpleDateFormat
import javax.servlet.{ServletConfig, ServletContext}
import com.github.grimcoder.producemarketrestscalatra.dao.MemoryDataAccess
import com.github.grimcoder.producemarketrestscalatra.model.{PriceChange, Sale, Price}
import net.liftweb.json._
import org.scalatra._
import org.scalatra.liftjson.LiftJsonSupport
import org.scalatra.scalate.ScalateSupport

class ProduceMarketServlet extends ScalatraServlet with ScalateSupport with LiftJsonSupport with CorsSupport {

  implicit val formatsz = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  override def init(config: ServletConfig)   {
    super.init(config);
    val context : ServletContext = getServletContext();
    val dao = context.getInitParameter("dao");
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
        MemoryDataAccess.pricesFilter(id)
      )
    }
    else Extraction.decompose(MemoryDataAccess.prices)
  }

  get("/api/sales") {
    if (request.parameters.contains("id")) {
      val id = params("id").toString
      Extraction.decompose(
        MemoryDataAccess.salesfilter(id)
      )
    }
    else Extraction.decompose(MemoryDataAccess.sales)
  }

  get("/api/reports/prices") {
    Extraction.decompose(MemoryDataAccess.history)
  }

  post("/api/prices") {
    val price: Price = parsedBody.extract[Price]
    MemoryDataAccess.postPrices(price)
  }

  post("/api/sales") {
    val sale: Sale = parsedBody.extract[Sale]
    MemoryDataAccess.postSale(sale)
  }

  delete("/api/prices") {
    val id = params("id").toString;
    MemoryDataAccess.deletePrice(id)
  }

  delete("/api/sales") {
    val id = params("id").toString;
    MemoryDataAccess.deleteSale(id)
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
