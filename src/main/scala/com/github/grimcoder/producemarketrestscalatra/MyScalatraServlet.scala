package com.github.grimcoder.producemarketrestscalatra

import com.github.grimcoder.producemarketrestscalatra.model.Price
import net.liftweb.json._
import org.scalatra.ScalatraServlet
import org.scalatra.liftjson.LiftJsonSupport
import org.scalatra.scalate.ScalateSupport


class MyScalatraServlet extends ScalatraServlet with ScalateSupport with LiftJsonSupport {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

  get("/json") {
    Extraction.decompose(
      Price("1", 19, "Beet")
    )
  }
  post("/json") {
    parsedBody match {
      case JNothing ⇒ halt(400, "invalid json")
      case json: JObject ⇒ {
        //(json \ "name").extract[String]

        val pamyu: Price = json.extract[Price]
        pamyu.Price
      }
      case _ ⇒ halt(400, "unknown json")
    }
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
