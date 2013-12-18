package org.amasoon.persistence.order;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
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
    Customer c1;
    Customer c2;
    Customer c3;
    Order o1;
    Order o2;
    Order o3;

    @BeforeTest
    public void setup() {
        emf = Persistence.createEntityManagerFactory("bookstore");
        em = emf.createEntityManager();
        c1 = createCustomer("Ferdi Kübler", "ferdi.kübler@radsport.ch");
        c2 = createCustomer("Godi Schmutz", "godi.schmutz@radsport.ch");
        c3 = createCustomer("Beat Breu", "beat.breu@radsport.ch");
        o1 = createOrder(c1);
        o2 = createOrder(c1);
        o3 = createOrder(c2);
        em.getTransaction().begin();
        em.persist(o1);
        em.persist(o2);
        em.getTransaction().commit();

    }

    @AfterTest
    public void tearDown() {
        em.getTransaction().begin();
        em.remove(o1);
        em.remove(o2);
        em.getTransaction().commit();
    }

    private Order createOrder(Customer customer) {
        Order order = new Order();
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setAmount(BigDecimal.TEN);
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomer(customer);
        return order;
    }

    private Customer createCustomer(String email, String name) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setName(name);
        return customer;
    }

    @Test
    public void findByNumber() {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_NUMBER, Order.class);
        query.setParameter("orderNumber", o1.getOrderNumber());
        Order actual = query.getSingleResult();
        assertEquals(o1, actual);
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
        query.setParameter("customer", o1.getCustomer());
        List<Order> actual = query.getResultList();
        assertEquals(2, actual.size());
    }

    @Test
    public void findByCustomer_CustomerNotExist() {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_CUSTOMER, Order.class);
        query.setParameter("customer", c3);
        List<Order> actual = query.getResultList();
        assertEquals(0, actual.size());
    }

}
