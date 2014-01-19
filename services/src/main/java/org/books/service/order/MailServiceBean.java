package org.books.service.order;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import org.books.persistence.order.Order;

@Singleton(name = "MailService")
@Startup
public class MailServiceBean implements MailService {

    public static final char NEWLINE = '\n';
    @Resource(lookup = "mail/bookstore")
    private Session mailSession;

    @Override
    public void sendMail(Order order, MessageBuilder.MailType type) {
        try {
            Transport.send(new MessageBuilder(mailSession, order, type).build());
            Logger.getLogger(MailServiceBean.class.getName()).log(Level.INFO, "Mail sent", order);
        } catch (MessagingException ex) {
            Logger.getLogger(MailServiceBean.class.getName()).log(Level.SEVERE, "Encountered an error", ex);
        }

    }

}
