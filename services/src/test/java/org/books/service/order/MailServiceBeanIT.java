package org.books.service.order;

import java.sql.Date;
import java.util.UUID;
import javax.naming.InitialContext;
import org.books.persistence.customer.Customer;
import org.books.persistence.order.Order;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MailServiceBeanIT {

    private static final String JNDI_NAME = "java:global/services/MailService";
    private MailService mailService;

    @BeforeClass
    public void init() throws Exception {
        mailService = (MailService) new InitialContext().lookup(JNDI_NAME);
    }

    @Test
    public void sendMail() throws Exception {
        //arrange
        Order order = new Order();
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setOrderNumber(UUID.randomUUID().toString());
        Customer customer = new Customer();
        customer.setEmail("beatschumacher@bluewin.ch");
        customer.setName("Beat Schumacher");
        order.setCustomer(customer);
        //act
        mailService.sendMail(order);
    }

}
