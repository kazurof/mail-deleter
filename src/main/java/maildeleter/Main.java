package maildeleter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class Main {
  static final Logger LOGGER = LogManager.getLogger(Main.class);

  /**
   * your email address.
   */
  static final String EMAIL = "youremail@gmail.example.com";
  /**
   * your email password.
   */
  static final String PASSWD = "yourpassword";

  /**
   * folder which have mail you want to moveto trash folder.
   */
  static final String TARGET_FOLDER = "foldernametodelete";
  /**
   * name of trash folder. This may differ as your environment or language setting of gmail.
   */
  static final String TRASH_FOLDER = "[Gmail]/Trash";

  /**
   * how many mail will be processed for each task.
   */
  static final int UNIT = 2;
  /**
   * how many task will be processed for one execution.
   */
  static final int NUM_OF_TASKS = 3;
  /**
   * how many thread will be prepared to delete mails.
   */
  static final int THREAD_POOL_SIZE = 3;

  /**
   * setting from https://support.google.com/mail/answer/7126229
   */
  static final String IMAP_HOST = "imap.gmail.com";
  /**
   * setting from https://support.google.com/mail/answer/7126229
   */
  static final int IMAP_PORT = 993;

  /** number of tasks which was ended in fail.*/
  static AtomicInteger failed = new AtomicInteger(0);

  public static void main(String[] args) throws Exception {
    LOGGER.info("process start.");

    ExecutorService es = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    LOGGER.info("reading mail and creating tasks.");
    List<DeleteTask> list = prepareMessages(UNIT, NUM_OF_TASKS);
    try {
      LOGGER.info("start to submit!" + list.size());
      list.stream().forEach(m -> {
        es.submit(m);
      });
    } finally {
      es.shutdown();
      es.awaitTermination(100, TimeUnit.DAYS);
    }

    LOGGER.info("process end.");
    LOGGER.info("THREAD_POOL_SIZE " + THREAD_POOL_SIZE);
    LOGGER.info("NUM_OF_TASKS " + NUM_OF_TASKS);
    LOGGER.info("UNIT " + UNIT);
    LOGGER.info("failed " + failed.get());
    LOGGER.info("planed num of deleted " + NUM_OF_TASKS * UNIT);
  }


  static List<DeleteTask> prepareMessages(int unit, int numOfMessageArray) throws Exception {

    Store store = prepareStore();
    Folder inbox = store.getFolder(TARGET_FOLDER);
    inbox.open(Folder.READ_ONLY);

    List<DeleteTask> result = new ArrayList<>(numOfMessageArray);
    for (int i = 0; i < numOfMessageArray; i++) {
      int firstPos = (i * unit) + 1;
      int lastPos = (i + 1) * unit;
      Folder trash = store.getFolder(TRASH_FOLDER);
      result.add(new DeleteTask(inbox.getMessages(firstPos, lastPos), trash, inbox));
    }
    return result;
  }

  static Store prepareStore() throws MessagingException {
    Properties props = new Properties();
    props.setProperty("mail.store.protocol", "imaps");

    Session session = Session.getInstance(props, null);
    Store store = session.getStore();
    store.connect(IMAP_HOST, IMAP_PORT, EMAIL, PASSWD);
    return store;
  }
}
