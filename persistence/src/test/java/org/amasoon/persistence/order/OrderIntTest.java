package org.amasoon.persistence.order;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.amasoon.persistence.customer.Customer;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class OrderIntTest {

    EntityManagerFactory emf;
    EntityManager em;
    Order order;

    @BeforeTest
    public void setup() {
        emf = Persistence.createEntityManagerFactory("amasoon");
        em = emf.createEntityManager();
        createOrder();
    }

    @AfterTest
    public void tearDown() {
        em.getTransaction().begin();
        em.remove(order);
        em.getTransaction().commit();
    }

    private void createOrder() {
        em.getTransaction().begin();
        Customer customer = new Customer();
        customer.setEmail("ferdi.kübler@radsport.ch");
        customer.setName("Ferdi Kübler");
        order = new Order();
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setAmount(BigDecimal.TEN);
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomer(customer);
        em.persist(order);
        em.getTransaction().commit();
    }

    @Test
    public void findByNumber() {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_NUMBER, Order.class);
        query.setParameter("orderNumber", order.getOrderNumber());
        Order actual = query.getSingleResult();
        assertEquals(order, actual);
    }

    @Test(expectedExceptions = NoResultException.class)
    public void findByNumber_NumberNotExist() {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_NUMBER, Order.class);
        query.setParameter("orderNumber", "123");
        query.getSingleResult();
    }

    @Test
    public void findByCustomer() {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_CUSTOMER, Order.class);
        query.setParameter("customer", order.getCustomer());
        Order actual = query.getSingleResult();
        assertEquals(order, actual);
    }

    @Test(expectedExceptions = NoResultException.class)
    public void findByCustomer_CustomerNotExist() {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_CUSTOMER, Order.class);
        Customer customer = new Customer();
        customer.setEmail("rudi.rüssel@srfdrs.tv");
        customer.setName("Rudi Rüssel");
        query.setParameter("customer", customer);
        query.getSingleResult();
    }

}
