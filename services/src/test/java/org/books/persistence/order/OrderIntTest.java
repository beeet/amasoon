package org.books.persistence.order;

import org.book.persistence.order.Order;
import org.book.persistence.order.LineItem;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import org.books.persistence.catalog.Book;
import org.books.persistence.customer.Address;
import org.books.persistence.customer.CreditCard;
import org.books.persistence.customer.Customer;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class OrderIntTest {

    EntityManagerFactory emf;
    EntityManager em;
    Customer c1;
    Order o1;
    Order o2;

    @BeforeTest
    public void setup() {
        emf = Persistence.createEntityManagerFactory("bookstore");
        em = emf.createEntityManager();
        c1 = createCustomer("Ferdi Kübler", "ferdi.kübler@radsport.ch");
        o1 = createOrder(c1);
        o2 = createOrder(c1);
        em.getTransaction().begin();
        em.persist(c1);
        em.persist(o1);
        em.persist(o2);
        em.getTransaction().commit();
    }

    @AfterTest
    public void tearDown() {
        em.getTransaction().begin();
        em.remove(em.find(Order.class, o1.getId()));
        em.remove(em.find(Order.class, o2.getId()));
        em.remove(em.find(Customer.class, c1.getId()));
        em.getTransaction().commit();
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
        query.setParameter("customer", c1);
        List<Order> actual = query.getResultList();
        assertEquals(2, actual.size());
    }

    @Test
    public void findByCustomer_CustomerNotExist() {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_CUSTOMER, Order.class);
        query.setParameter("customer", new Customer());
        List<Order> actual = query.getResultList();
        assertEquals(0, actual.size());
    }

    @Test
    public void placeOrder() {
        //arrange
        Customer customer = createCustomer("Beat Breu", "beat.breu@radsport.ch");
        Address address = createAddress();
        CreditCard creditcard = createCreditcard();
        Book book = createBook();

        Set<LineItem> lineItems = new HashSet<>();
        final LineItem lineItem = new LineItem();
        lineItem.setBook(book);
        lineItems.add(lineItem);
        Order order = createOrder(customer, address, creditcard, lineItems);

        //act
        em.getTransaction().begin();
        em.persist(customer);
        em.persist(book);
        em.persist(order);
        em.getTransaction().commit();

        //clean
        em.getTransaction().begin();
        em.remove(em.find(Customer.class, customer.getId()));
        em.remove(em.find(Order.class, order.getId()));
        em.remove(em.find(Book.class, book.getId()));
        em.getTransaction().commit();
    }

    private Order createOrder(Customer customer) {
        Order order = new Order();
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setAmount(BigDecimal.TEN);
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomer(customer);
        order.setAddress(new Address());
        order.setCreditCard(new CreditCard());
        return order;
    }

    private Customer createCustomer(String name, String email) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setName(name);
        customer.setAddress(new Address());
        customer.setCreditCard(new CreditCard());
        return customer;
    }

    private Order createOrder(Customer customer, Address address, CreditCard creditcard, Set<LineItem> lineItems) {
        Order order = new Order();
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setAmount(BigDecimal.TEN);
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomer(customer);
        order.setAddress(address);
        order.setCreditCard(creditcard);
        order.setLineItems(lineItems);
        order.setStatus(Order.Status.open);
        order.setAmount(BigDecimal.valueOf(52L));
        return order;
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

    private Book createBook() {
        Book book = new Book();
        book.setAuthors("Adam Bien");
        book.setBinding("lose");
        book.setIsbn("15646651313212");
        book.setNumberOfPages(5);
        book.setPublisher("o'Reily");
        book.setPublicationDate(new Date(System.currentTimeMillis()));
        book.setTitle("Java in a Nutshel");
        return book;
    }

}
