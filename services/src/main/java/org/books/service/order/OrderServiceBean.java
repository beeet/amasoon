package org.books.service.order;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import org.books.persistence.customer.CreditCard;
import org.books.persistence.customer.Customer;
import org.books.persistence.order.LineItem;
import org.books.persistence.order.Order;
import org.books.persistence.order.Order.Status;

@Stateless(name = "OrderService")
public class OrderServiceBean implements OrderService {

    @Resource(lookup = "jms/orderQueueFactory")
    private ConnectionFactory connectionFactory;
    @Resource(lookup = "jms/orderQueue")
    private Queue orderQueue;
    @PersistenceContext
    EntityManager em;

    @Override
    public String placeOrder(Customer customer, List<LineItem> items) throws CreditCardExpiredException {
        Order order = new Order();
        order.setAmount(summarizeTotalOrderAmount(items));
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomer(customer);
        order.setAddress(customer.getAddress());
        final CreditCard creditCard = customer.getCreditCard();
        validateCreditcard(creditCard);
        order.setCreditCard(creditCard);
        order.setLineItems(Lists.newArrayList(items));
        order.setOrderDate(new Date(System.currentTimeMillis()));
        em.persist(order);
        em.flush();
        sendMessageToOrderProcessor(order.getId());
        return order.getOrderNumber();

    }

    private void validateCreditcard(final CreditCard creditCard) throws CreditCardExpiredException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<CreditCard>> violations = factory.getValidator().validate(creditCard);
        if (!violations.isEmpty()) {
            throw new CreditCardExpiredException();
        }
    }

    @Override
    public Order findOrder(String number) throws OrderNotFoundException {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_NUMBER, Order.class);
        query.setParameter("orderNumber", number);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new OrderNotFoundException();
        }
    }

    @Override
    public List<Order> getOrders(Customer customer) {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_CUSTOMER, Order.class);
        query.setParameter("customer", customer);
        return query.getResultList();
    }

    @Override
    public void cancelOrder(Order order) throws OrderNotCancelableException {
        if (!order.isOpen()) {
            throw new OrderNotCancelableException();
        }
        order.setStatus(Status.canceled);
        em.persist(order);
        em.flush();
    }

    private BigDecimal summarizeTotalOrderAmount(List<LineItem> items) {
        BigDecimal amount = BigDecimal.ZERO;
        for (LineItem item : items) {
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            amount = amount.add(item.getBook().getPrice().multiply(quantity));
        }
        return amount;
    }

    private void sendMessageToOrderProcessor(Integer orderId) {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MapMessage message = session.createMapMessage();
            message.setInt("orderId", orderId);
            session.createProducer(orderQueue).send(message);
        } catch (JMSException ex) {
            Logger.getLogger(OrderServiceBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (JMSException ex) {
                Logger.getLogger(OrderServiceBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
