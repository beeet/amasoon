package org.books.service.order;

import com.google.common.base.Preconditions;
import java.text.DateFormat;
import java.util.Date;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.books.persistence.order.Order;

class MessageBuilder {
    private final Session session;
    private final Order order;

    public MessageBuilder(final Session session, final Order order) {
        this.session = session;
        this.order = order;
    }

    public Message buid() throws MessagingException {
        final String email = order.getCustomer().getEmail();
        Preconditions.checkNotNull(email);
        Message message = new MimeMessage(session);
        message.setHeader("X-Mailer", "JavaMail");
        message.setFrom(new InternetAddress("do-not-reply@amasoon.org", false));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setSubject("Confirmation of your order " + order.getOrderNumber());
        message.setSentDate(new Date());
        message.setText(createMessageBody(order));
        return message;
    }

    private String createMessageBody(Order order) {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
        StringBuilder sb = new StringBuilder(512);
        sb.append("Hello ".concat(order.getCustomer().getName()));
        sb.append(MailServiceBean.NEWLINE).append(MailServiceBean.NEWLINE);
        sb.append("We confirm your order from ").append(dateFormatter.format(order.getOrderDate()));
        sb.append(MailServiceBean.NEWLINE);
        sb.append("Your order will be delivered in the next 24 hours.");
        sb.append(MailServiceBean.NEWLINE).append(MailServiceBean.NEWLINE);
        return sb.toString();
    }
    
}
