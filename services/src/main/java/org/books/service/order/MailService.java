package org.books.service.order;

import javax.ejb.Remote;
import org.books.persistence.order.Order;

@Remote
public interface MailService {

    public void sendMail(Order order, MessageBuilder.MailType type);

}
