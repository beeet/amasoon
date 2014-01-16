package org.books.service.order;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import org.books.persistence.order.Order;

@Stateless(name = "MailService")
public class MailServiceBean implements MailService {

    public static final char NEWLINE = '\n';
    @Resource(lookup = "mail/bookstore")
    private Session mailSession;

    @Override
    public void sendMail(Order order) {
        try {
            Transport.send(new MessageBuilder(mailSession, order).buid());
            Logger.getLogger(MailServiceBean.class.getName()).log(Level.INFO, "Mail sent", order);
        } catch (MessagingException ex) {
            Logger.getLogger(MailServiceBean.class.getName()).log(Level.SEVERE, "Encountered an error", ex);
        }

    }

}
