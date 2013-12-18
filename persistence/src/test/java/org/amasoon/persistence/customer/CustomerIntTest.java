package org.amasoon.persistence.customer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CustomerIntTest {

    EntityManagerFactory emf;
    EntityManager em;
    Customer customer;

    @BeforeTest
    public void setup() {
        emf = Persistence.createEntityManagerFactory("bookstore");
        em = emf.createEntityManager();
        createCustomer();
    }

    @AfterTest
    public void tearDown() {
        em.getTransaction().begin();
        em.remove(customer);
        em.getTransaction().commit();
    }

    private void createCustomer() {
        em.getTransaction().begin();
        customer = new Customer();
        customer.setEmail("ueli@bundesrat.ch");
        customer.setName("Ueli Maurer");
        em.persist(customer);
        em.getTransaction().commit();
    }

    @Test
    public void findByEmail() {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class);
        query.setParameter("email", "ueli@bundesrat.ch");
        Customer actual = query.getSingleResult();
        assertEquals(customer, actual);
    }

    @Test(expectedExceptions = NoResultException.class)
    public void findByEmail_EmailNotExist() {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class);
        query.setParameter("email", "not@exist.ch");
        query.getSingleResult();
    }

}
