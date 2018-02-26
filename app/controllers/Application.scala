package controllers

import java.util.UUID
import javax.inject.Inject

import akka.stream.scaladsl.{Flow, Sink, Source}
import play.api.libs.json.{JsArray, Json}
import play.api.mvc._

class Application @Inject()(cc:ControllerComponents) extends AbstractController(cc) {
  def socket = WebSocket.accept[String, String] { request =>

    // Log events to the console
    val in = Sink.foreach[String](println)

    // Send a single 'Hello!' message and then leave the socket open
    val out = Source.fromIterator(()=>commentsJson.value.iterator).map(_.toString).concat(Source.maybe)

    Flow.fromSinkAndSource(in, out)
  }
  // serves the web page
  def index = Action {implicit request =>
    Ok(views.html.react())
  }

  // The json keys. The 'id' field was added as without it we would get a warning like this:
  // Warning: Each child in an array or iterator should have a unique "key" prop. Check the render method of CommentList. See https://fb.me/react-warning-keys for more information.
  val JSON_KEY_COMMENTS = "comments"
  val JSON_KEY_AUTHOR = "author"
  val JSON_KEY_TEXT = "text"
  val JSON_KEY_ID = "id"

  // Initialise the comments list
  var commentsJson: JsArray = Json.arr(
    Json.obj(JSON_KEY_ID -> UUID.randomUUID().toString, JSON_KEY_AUTHOR -> "Pete Hunt", JSON_KEY_TEXT -> "This is one comment"),
    Json.obj(JSON_KEY_ID -> UUID.randomUUID().toString, JSON_KEY_AUTHOR -> "Jordan Walke", JSON_KEY_TEXT -> "This is *another* comment")
  )

  // Returns the comments list
  def comments = Action {
    Ok(commentsJson)
  }

  // Adds a new comment to the list and returns it
  def comment(author: String, text: String) = Action {implicit request => {
    val newComment = Json.obj(
      JSON_KEY_ID -> UUID.randomUUID().toString,
      JSON_KEY_AUTHOR -> author,
      JSON_KEY_TEXT -> text)
    commentsJson = commentsJson :+ newComment
    Ok(newComment)
  }}

}
