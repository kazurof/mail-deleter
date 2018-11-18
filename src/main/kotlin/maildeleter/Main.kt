package maildeleter

import context.newFixedThreadPoolContext
import future.await
import future.future
import kotlinx.coroutines.runBlocking

import org.apache.logging.log4j.LogManager
import java.util.Properties
import java.util.concurrent.atomic.AtomicInteger
import javax.mail.Folder
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Store


/**
 * your email address.
 */
const val EMAIL = "youremail@gmail.example.com"
/**
 * your email password.
 */
const val PASSWD = "yourpassword"

/**
 * folder which have mail you want to moveto trash folder.
 */
const val TARGET_FOLDER = "foldernametodelete"
/**
 * name of trash folder. This may differ as your environment or language setting of gmail.
 */
const val TRASH_FOLDER = "[Gmail]/Trash"

/**
 * how many mail will be processed for each task.
 */
const val UNIT = 3
/**
 * how many task will be processed for one execution.
 */
const val NUM_OF_TASKS = 5
/**
 * how many thread will be prepared to delete mails.
 */
const val THREAD_POOL_SIZE = 2

/**
 * setting from https://support.google.com/mail/answer/7126229
 */
const val IMAP_HOST = "imap.gmail.com"
/**
 * setting from https://support.google.com/mail/answer/7126229
 */
const val IMAP_PORT = 993


object Main {
  var LOGGER = LogManager.getLogger(Main::class.java)

  /** number of tasks which was ended in fail.*/
  internal var failed = AtomicInteger(0)

  @JvmStatic
  fun main(args: Array<String>) {
    LOGGER.info("process start.")

    LOGGER.info("reading mail and creating tasks.")
    doDeleteByCoroutine()

    LOGGER.info("process end.")
    LOGGER.info("THREAD_POOL_SIZE $THREAD_POOL_SIZE")
    LOGGER.info("NUM_OF_TASKS $NUM_OF_TASKS")
    LOGGER.info("UNIT $UNIT")
    LOGGER.info("failed " + failed.get())
    LOGGER.info("planed num of deleted " + NUM_OF_TASKS * UNIT)
  }

}


fun doDeleteByCoroutine() = runBlocking {
  val unit: Int = UNIT
  val numOfMessageArray: Int = NUM_OF_TASKS

  val store = prepareStore()
  val inbox = store.getFolder(TARGET_FOLDER)
  inbox.open(Folder.READ_ONLY)
  Main.LOGGER.info("after  open ")

  val compute = newFixedThreadPoolContext(THREAD_POOL_SIZE, "delete-thread-pool")

  val trash = store.getFolder(TRASH_FOLDER)

  val subs = Array(numOfMessageArray) { i ->
    future(compute) {
      val firstPos = i * unit + 1
      val lastPos = (i + 1) * unit
      doDelete(inbox.getMessages(firstPos, lastPos), trash, inbox, i)
    }
  }
  // await all of them
  subs.forEach { it.await() }
  Main.LOGGER.info("Done all")
}


fun doDelete(target: Array<Message>, trash: Folder, inbox: Folder, id: Int) {

  try {
    Main.LOGGER.info("task started id $id")

    val indexMsg = String.format("id %d / %d ,  %d", id, NUM_OF_TASKS, target.size)
    try {
      val representMsg = target[0].sentDate.toString() + " <-> " + target[0].subject
      inbox.copyMessages(target, trash)
      val progressMsg = "$indexMsg | $representMsg"
      Main.LOGGER.info("task finished $progressMsg")
    } catch (e: MessagingException) {
      Main.LOGGER.error("failed  $indexMsg", e)
      Main.failed.incrementAndGet()
    }

  } catch (e: Exception) {
    Main.LOGGER.error("exception at Task  ", e)
  }

}

fun prepareStore(): Store {
  val props = Properties()
  props.setProperty("mail.store.protocol", "imaps")

  val session = Session.getInstance(props, null)
  val store = session.store
  store.connect(IMAP_HOST, IMAP_PORT, EMAIL, PASSWD)
  return store
}

