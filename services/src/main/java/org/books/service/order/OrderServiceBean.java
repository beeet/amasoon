package org.books.service.order;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import org.books.persistence.customer.CreditCard;
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
        Order order = new Order();
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setAmount(getOrderAmount(items));
        order.setOrderNumber(UUID.randomUUID().toString()); // Todo: Order Number
        order.setCustomer(customer);
        order.setAddress(customer.getAddress());
        order.setCreditCard(checkCreditCard(customer.getCreditCard()));
        order.setLineItems(new HashSet<>(items));
        order.setStatus(Order.Status.open);
        em.persist(order);
        return order.getOrderNumber();
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
        order.setStatus(Status.canceled);
        em.persist(em.merge(order));
    }

    private BigDecimal getOrderAmount(List<LineItem> items) {
        BigDecimal amount = new BigDecimal(0);
        for (LineItem item : items) {
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            amount = amount.add(item.getBook().getPrice().multiply(quantity));
        }
        return amount;
    }

    private CreditCard checkCreditCard(CreditCard creditCard) throws CreditCardExpiredException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        if (df.format(creditCard.getExpirationDate()).compareTo(df.format(new java.util.Date())) < 0) {
            throw new CreditCardExpiredException();
        }
        return creditCard;
    }
}
