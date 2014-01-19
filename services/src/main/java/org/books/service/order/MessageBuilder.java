package org.books.service.order;

import com.google.common.base.Preconditions;
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
    private final MailType type;

    public MessageBuilder(final Session session, final Order order, final MailType type) {
        this.session = session;
        this.order = order;
        this.type = type;
    }

    public enum MailType {

        OrderPlaced(
                "Confirmation of order (%s)",
                "Thanks for your order.",
                "You'll be notified as soon as the order is processed."),
        OrderProcessed(
                "Order was processed (%s)",
                "Your order was processed successfully.",
                "It will be delivered within the next 24 hours.");

        private final String subject;
        private final String bodyInitial;
        private final String bodyFinal;

        private MailType(String subject, String bodyInitial, String bodyFinal) {
            this.subject = subject;
            this.bodyInitial = bodyInitial;
            this.bodyFinal = bodyFinal;
        }

        public String getSubject() {
            return subject;
        }

        public String getBodyInitial() {
            return bodyInitial;
        }

        public String getBodyFinal() {
            return bodyFinal;
        }
    }

    public Message build() throws MessagingException {
        final String email = order.getCustomer().getEmail();
        Preconditions.checkNotNull(email);
        Message message = new MimeMessage(session);
        message.setHeader("X-Mailer", "JavaMail");
        message.setFrom(new InternetAddress("do-not-reply@amasoon.org", false));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setSubject(String.format(type.getSubject(), order.getOrderNumber()));
        message.setSentDate(new Date());
        message.setText(createMessageBody(order));
        return message;
    }

    private String createMessageBody(Order order) {
        StringBuilder sb = new StringBuilder(512);
        sb.append("Hello ".concat(order.getCustomer().getName()));
        sb.append(MailServiceBean.NEWLINE).append(MailServiceBean.NEWLINE);
        sb.append(type.getBodyInitial());
        sb.append(MailServiceBean.NEWLINE);
        sb.append(type.getBodyFinal());
        sb.append(MailServiceBean.NEWLINE).append(MailServiceBean.NEWLINE);
        return sb.toString();
    }

}
