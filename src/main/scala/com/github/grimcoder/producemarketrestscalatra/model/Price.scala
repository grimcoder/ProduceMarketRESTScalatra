package com.github.grimcoder.producemarketrestscalatra.model
//import com.github.nscala_time.time.Imports._
import  java.util.Date


/**
 * Created by taraskovtun on 9/6/15.
 */
case class Price (Id: Option[String], Price: Double, ItemName: String) {

}

case class Sale (Id: Option[String], Date: Date, SaleDetails: List[SaleDetail]) {


}

case class SaleDetail(ItemName: String, Price: Double, Units : Int){

}

case class PriceChange (Id: Option[String], Price: Double, ItemName: String, priceWas : Double, Action: String) {

}

