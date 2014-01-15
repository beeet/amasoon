package org.books.service.order;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
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
        try {
            em.persist(order);//TODO muss concurrent modification abgefangen werden?
            em.flush();
        } catch (ConstraintViolationException e) {
            throw e;
        }
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
