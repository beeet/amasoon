package org.books.persistence.customer;

import java.sql.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CustomerIT {

    EntityManagerFactory emf;
    EntityManager em;
    Customer customer;

    @BeforeTest
    public void setup() {
        emf = Persistence.createEntityManagerFactory("bookstoretest");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        customer = new Customer();
        customer.setEmail("ueli@bundesrat.ch");
        customer.setName("Ueli Maurer");
        customer.setAddress(createAddress());
        customer.setCreditCard(createCreditcard());
        em.persist(customer);
        em.getTransaction().commit();
    }

    @AfterTest
    public void tearDown() {
        em.getTransaction().begin();
        em.remove(customer);
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

    @Test
    public void creationOfCustomer() {
        Customer cus = createCustomer(createAddress(), createCreditcard());
        em.getTransaction().begin();
        em.persist(cus);
        em.getTransaction().commit();

        em.getTransaction().begin();
        em.remove(cus);
        em.getTransaction().commit();
    }

    private Customer createCustomer(Address address, CreditCard creditcard) {
        Customer cus = new Customer();
        cus.setEmail("eee@mail.ch");
        cus.setName("Tester Test");
        cus.setAddress(address);
        cus.setCreditCard(creditcard);
        return cus;
    }

    private CreditCard createCreditcard() {
        CreditCard creditcard = new CreditCard();
        creditcard.setType(CreditCard.Type.MasterCard);
        creditcard.setNumber("5411222233334445");
        creditcard.setExpirationDate(new Date(System.currentTimeMillis()));
        return creditcard;
    }

    private Address createAddress() {
        Address address = new Address();
        address.setStreet("Zu Hause 5");
        address.setZipCode("1234");
        address.setCity("Musterdorf");
        address.setCountry("Schweiz");
        return address;
    }

}
