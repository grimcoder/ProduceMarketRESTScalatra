//import com.github.grimcoder.producemarketrestscalatra.ProduceMarketServlet
//import org.scalatra.test.scalatest.ScalatraFunSuite
//
//// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
//class MyScalatraServletSpec extends ScalatraFunSuite {
//// `MyScalatraServlet` is your app which extends ScalatraServlet
//  addServlet(classOf[ProduceMarketServlet], "/*")
//
//  test("simple get") {
//    get("/") {
//      status should equal (200)
//      body should include ("Hi")
//    }
//  }
//  test("json get") {
//    get("/api/prices") {
//      status should equal (200)
//      body should include ("taras")
//    }
//  }
//  test("json post") {
//
//
//     post("/json", """{"Id":"1","Price":19.0,"ItemName":"Beet"}""",
//
//       Map("Content-Type" -> "application/json;charset=UTF-8",
//         "accept-charset" -> "utf-8")) {
//      status should equal (200)
//      body should include ("あ")
//    }
//  }
//}
