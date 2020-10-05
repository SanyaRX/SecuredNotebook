package service

import org.scalatra.ScalatraServlet

class WebService extends ScalatraServlet  {
  get("/") {
    "Scalatra rules!"
  }
}