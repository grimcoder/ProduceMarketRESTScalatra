package com.github.grimcoder.producemarketrestscalatra

import java.io.InputStream
import java.text.SimpleDateFormat
import com.github.grimcoder.producemarketrestscalatra.model.{Sale, Price}
import net.liftweb.json._
import org.scalatra._
import org.scalatra.liftjson.LiftJsonSupport
import org.scalatra.scalate.ScalateSupport


class ProduceMarketServlet extends ScalatraServlet with ScalateSupport with LiftJsonSupport with CorsSupport {

  var stream : InputStream = getClass.getResourceAsStream("/prices.json")
  def lines  = scala.io.Source.fromInputStream( stream ).getLines.mkString
  val prices = Serialization.read[List[Price]](lines);

  implicit val formatsz = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  get("/api/prices") {
    if (request.parameters.contains("id")){
      val id = params("id")
      val idparam = request.getParameter(id)
      val filtered = prices.filter(_.Id == id)
      Extraction.decompose(
        filtered
      )
    }
    else Extraction.decompose(prices)
  }

  get("/api/sales") {

    stream = getClass.getResourceAsStream("/sales.json")
    val sales = Serialization.read[List[Sale]](lines);

    if (request.parameters.contains("id")){
      val id = params("id")
      val idparam = request.getParameter(id)
      val filtered = sales.filter(_.Id == id)
      Extraction.decompose(
        filtered
      )
    }
    else Extraction.decompose(sales)
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
