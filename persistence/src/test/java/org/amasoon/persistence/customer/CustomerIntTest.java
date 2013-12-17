package org.amasoon.persistence.customer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.testng.annotations.Test;

public class CustomerIntTest {

    @Test
    public void insert() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("amasoon");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        final Customer customer = new Customer();
        customer.setEmail("tester@amasoon.org");
        em.persist(customer);
        em.getTransaction().commit();
    }
}
