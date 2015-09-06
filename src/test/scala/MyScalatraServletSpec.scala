import com.github.grimcoder.producemarketrestscalatra.MyScalatraServlet
import org.scalatra.test.scalatest.ScalatraFunSuite

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html 
class MyScalatraServletSpec extends ScalatraFunSuite {
// `MyScalatraServlet` is your app which extends ScalatraServlet
  addServlet(classOf[MyScalatraServlet], "/*")

  test("simple get") {
    get("/") {
      status should equal (200)
      body should include ("world!")
    }
  }
  test("json get") {
    get("/json") {
      status should equal (200)
      body should include ("taras")
    }
  }
  test("json post") {


     post("/json", """{"id":1, "age":20, "name":"あ"}""",
       Map("Content-Type" -> "application/json;charset=UTF-8",
         "accept-charset" -> "utf-8")) {
      status should equal (200)
      body should include ("あ")
    }
  }
}
