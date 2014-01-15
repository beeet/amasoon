package org.books.service.order;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
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
import javax.validation.ConstraintViolationException;
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
        order.setOrderNumber(UUID.randomUUID().toString()); // Todo: Order Number
        order.setCustomer(customer);
        order.setAddress(customer.getAddress());
        order.setCreditCard(customer.getCreditCard());
        order.setLineItems(Lists.newArrayList(items));
        order.setOrderDate(new Date(System.currentTimeMillis()));
        try {
            em.persist(order);
            em.flush();
            return order.getOrderNumber();
        } catch (ConstraintViolationException e) {
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
        //Order foundOrder = em.find(Order.class, order);$
        //TODO was wenn die Order noch nicht gespeichert wurde????
        //auch hier sollte ein query nicht nötig sein. wenn noch nicht gespeichert, dann einfach verwerfen
        if (!order.isOpen()) {
            throw new OrderNotCancelableException();
        }
        order.setStatus(Status.canceled);
        em.persist(order);//TODO muss concurrent modification abgefangen werden?
        em.flush();
        sendMessageToQueue(order.getId());
    }

    private BigDecimal summarizeTotalOrderAmount(List<LineItem> items) {
        BigDecimal amount = BigDecimal.ZERO;
        for (LineItem item : items) {
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            amount = amount.add(item.getBook().getPrice().multiply(quantity));
        }
        return amount;
    }

    private void sendMessageToQueue(Integer orderId) {
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
            if (null != connection) {
                //connection.close();
            }
        }
    }

}
