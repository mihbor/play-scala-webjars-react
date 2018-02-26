import akka.actor.typed.ActorRef
import akka.testkit.typed.TestKit
import akka.testkit.typed.scaladsl.TestProbe
import models._
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class BlogSpec() extends TestKit("MySpec") with WordSpecLike with BeforeAndAfterAll {

  override def afterAll {
    shutdown()
  }

  "Blog" must {
    "send back message just added" in {
      val probe = TestProbe[PostAdded]()
      val blog: ActorRef[BlogCommand] = spawn(Blog.behavior)
      blog ! AddPost("John Doe", "My message", probe.ref)
      val postAdded = probe.expectMsgType[PostAdded]
      assert(postAdded.author == "John Doe")
      assert(postAdded.content == "My message")
    }
    "post IDs are increasing sequencially" in {
      val probe = TestProbe[PostAdded]()
      val blog: ActorRef[BlogCommand] = spawn(Blog.behavior)
      blog ! AddPost("John Doe", "My message", probe.ref)
      val postAdded1 = probe.expectMsgType[PostAdded]
      blog ! AddPost("John Smith", "Next message", probe.ref)
      val postAdded2 = probe.expectMsgType[PostAdded]
      assert(postAdded2.postId == postAdded1.postId+1)
    }
    "send all previous messages" in {
      val probe1 = TestProbe[PostAdded]()
      val probe2 = TestProbe[Seq[PostAdded]]()
      val blog: ActorRef[BlogCommand] = spawn(Blog.behavior)
      blog ! AddPost("John Doe", "My message", probe1.ref)
      blog ! AddPost("Joe Smith", "Message 2", probe1.ref)
      blog ! GetAllPosts(probe2.ref)
      val seq = probe2.expectMsgType[Seq[PostAdded]]
      assert(seq.length >= 2)
    }

  }
}