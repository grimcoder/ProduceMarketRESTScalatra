package com.github.grimcoder.producemarketrestscalatra

import java.io.InputStream

import com.github.grimcoder.producemarketrestscalatra.model.Price
import net.liftweb.json._
import org.scalatra._
import org.scalatra.liftjson.LiftJsonSupport
import org.scalatra.scalate.ScalateSupport
import scala.io.Source
import scala.sys._
import sys.process._
import java.nio.file.{Paths, Path}
//import org.json4s.jackson.Serialization
//import org.json4s._


class ProduceMarketServlet extends ScalatraServlet with ScalateSupport with LiftJsonSupport with CorsSupport {

  val stream : InputStream = getClass.getResourceAsStream("/prices.json")
  val lines  = scala.io.Source.fromInputStream( stream ).getLines.mkString
  val prices = Serialization.read[List[Price]](lines);

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
