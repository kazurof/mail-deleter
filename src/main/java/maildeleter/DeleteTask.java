package maildeleter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

public class DeleteTask implements Runnable {
  static final Logger LOGGER = LogManager.getLogger(DeleteTask.class);
  static int maxId = 0;
  int id;
  Message[] target;
  Folder trash;
  Folder inbox;

  public DeleteTask(Message[] target, Folder trash, Folder inbox) {
    this.target = target;
    this.trash = trash;
    this.inbox = inbox;
    maxId++;
    this.id = maxId;
  }


  @Override
  public void run() {
    try {
      LOGGER.info("task started id " + id);

      String indexMsg = String.format("id %d / %d ,  %d", id, maxId, target.length);
      try {
        String representMsg = target[0].getSentDate() + " <-> " + target[0].getSubject();
        inbox.copyMessages(target, trash);
        String progressMsg = indexMsg + " | " + representMsg;
        LOGGER.info("task finished " + progressMsg);
      } catch (MessagingException e) {
        LOGGER.error("failed  " + indexMsg, e);
        Main.failed.incrementAndGet();
      }
    } catch (Exception e) {
      LOGGER.error("exception at Task  ", e);
    }

  }
}
