package org.books.service.order;

import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.books.persistence.customer.Customer;
import org.books.persistence.order.LineItem;
import org.books.persistence.order.Order;
import org.books.persistence.order.Order.Status;

@Stateless(name = "OrderService")
public class OrderServiceBean implements OrderService {

    @PersistenceContext
    EntityManager em;

    @Override
    public String placeOrder(Customer customer, List<LineItem> items) throws CreditCardExpiredException {
        Order order = new Order();//TODO inject 
        order.setAmount(summarizeTotalOrderAmount(items));
        order.setOrderNumber(UUID.randomUUID().toString()); // Todo: Order Number
        order.setCustomer(customer);
        order.setAddress(customer.getAddress());
        order.setCreditCard(customer.getCreditCard());
        order.setLineItems(Sets.newHashSet(items));
        //TODO @PostConstruct >>> order.setStatus(Order.Status.open);
        order.setOrderDate(new Date(System.currentTimeMillis()));
        try {
            em.persist(order);
            return order.getOrderNumber();
        } catch (Exception e) {
            //TODO catch ValidationException o.ä.
            throw new CreditCardExpiredException(e);
        }
    }

    @Override
    public Order findOrder(String number) throws OrderNotFoundException {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_NUMBER, Order.class);
        query.setParameter("number", number);
        try {
            return query.getSingleResult();
        } catch (NoResultException nre) {
            throw new OrderNotFoundException();
        }
    }

    @Override
    public List<Order> getOrders(Customer customer) {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_CUSTOMER, Order.class);
        query.setParameter("number", customer);
        return query.getResultList();
    }

    @Override
    public void cancelOrder(Order order) throws OrderNotCancelableException {
        //TODO: if the order is already closed or canceled throw exception
        order.setStatus(Status.canceled);
        em.persist(em.merge(order));
    }

    private BigDecimal summarizeTotalOrderAmount(List<LineItem> items) {
        BigDecimal amount = BigDecimal.ZERO;
        for (LineItem item : items) {
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            amount = amount.add(item.getBook().getPrice().multiply(quantity));
        }
        return amount;
    }

}
