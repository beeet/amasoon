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
            em.flush();
            return order.getOrderNumber();
        } catch (Exception e) {
            //TODO catch ValidationException o.ä.
            throw e;//new CreditCardExpiredException();
        }
    }

    @Override
    public Order findOrder(String number) throws OrderNotFoundException {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_NUMBER, Order.class);
        query.setParameter("number", number);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
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
        //Order foundOrder = em.find(Order.class, order);$
        //TODO was wenn die Order noch nicht gespeichert wurde????
        //auch hier sollte ein query nicht nötig sein. wenn noch nicht gespeichert, dann einfach verwerfen
        if (order.isCanceled() || order.isClosed()) {
            throw new OrderNotCancelableException();
        }
        order.setStatus(Status.canceled);
        em.persist(em.merge(order));//TODO muss concurrent modification abgefangen werden?
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
