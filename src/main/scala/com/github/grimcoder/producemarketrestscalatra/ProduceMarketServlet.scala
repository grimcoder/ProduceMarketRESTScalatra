package com.github.grimcoder.producemarketrestscalatra

import java.text.SimpleDateFormat
import javax.servlet.{ServletConfig, ServletContext}
import com.github.grimcoder.producemarketrestscalatra.dao.{DataAccessMongo, DataAccess, MemoryDataAccess}
import com.github.grimcoder.producemarketrestscalatra.model.{PriceChange, Sale, Price}
import net.liftweb.json._
import org.bson.types.ObjectId
import org.scalatra._
import org.scalatra.liftjson.LiftJsonSupport
import org.scalatra.scalate.ScalateSupport

class ObjectIdSerializer extends Serializer[ObjectId] {
  private val Class = classOf[ObjectId]

  def deserialize(implicit format: Formats) = {
    case (TypeInfo(Class, _), json) => json match {
      case JObject(JField("_id", JString(s)) :: Nil) => new ObjectId(s)
      case x => throw new MappingException("Can't convert " + x + " to  ObjectId")
    }
  }

  def serialize(implicit format: Formats) = {
    case x: ObjectId => JString(x.toString)
  }
}

class ProduceMarketServlet extends ScalatraServlet with ScalateSupport with LiftJsonSupport with CorsSupport {

  var dao: DataAccess = _
  implicit val formatsz = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  } + new ObjectIdSerializer

  override def init(config: ServletConfig){

    super.init(config);
    val context : ServletContext = getServletContext();

    dao = context.getInitParameter("dao") match  {
      case "memory" => MemoryDataAccess
      case _ => DataAccessMongo
    }
  }

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, X-HTTP-Method-Override, Content-Type, Cache-Control, Accept");
    response.setHeader("Access-Control-Allow-Methods", "HEAD,GET,PUT,POST,DELETE,OPTIONS");
    200
  }

  get("/api/prices") {
    if (request.parameters.contains("id")) {
      val id = params("id").toString
      val prices = dao.pricesFilter(id)
      Extraction.decompose(
        prices
      )
    }
    else Extraction.decompose(dao.prices)
  }

  get("/api/sales") {
    if (request.parameters.contains("id")) {
      val id = params("id").toString
      Extraction.decompose(
        dao.salesfilter(id)
      )
    }
    else Extraction.decompose(dao.sales)
  }

  get("/api/reports/prices") {
    Extraction.decompose(dao.history)
  }

  post("/api/prices") {
    val price: Price = parsedBody.extract[Price]
    dao.postPrices(price)
  }

  post("/api/sales") {
    val sale: Sale = parsedBody.extract[Sale]
    dao.postSale(sale)
  }

  delete("/api/prices") {
    val id = params("id").toString;
    dao.deletePrice(id)
  }

  delete("/api/sales") {
    val id = params("id").toString;
    dao.deleteSale(id)
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
