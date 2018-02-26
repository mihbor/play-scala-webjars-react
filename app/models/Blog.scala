package models

import akka.persistence.typed.scaladsl.PersistentActor
import akka.persistence.typed.scaladsl.PersistentActor.Effect
import akka.actor.typed.{ActorRef, Behavior}


sealed trait BlogEvent extends Serializable

final case class PostAdded(
  postId: Long, author: String, content: String) extends BlogEvent

sealed trait BlogCommand extends Serializable

final case class AddPost(author: String, content: String, replyTo: ActorRef[PostAdded]) extends BlogCommand

final case class GetAllPosts(
  replyTo: ActorRef[Seq[PostAdded]]) extends BlogCommand

object Blog {
  def behavior: Behavior[BlogCommand] =
    PersistentActor.immutable[BlogCommand, BlogEvent, Seq[PostAdded]](
      persistenceId = "abc",
      initialState = Vector.empty,
      commandHandler = PersistentActor.CommandHandler {
        (ctx, state, cmd) =>
        cmd match {
          case AddPost(author, content, replyTo) =>
            val evt = PostAdded(state.length, author, content)
            Effect.persist(evt).andThen {
              replyTo ! evt
            }
          case GetAllPosts(replyTo) =>
            replyTo ! state
            Effect.none
        }
      },
      eventHandler = (state, evt) =>
        evt match {
          case p : PostAdded =>
            state :+ p
        }
    )
}
