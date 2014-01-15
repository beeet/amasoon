package org.books.service.order;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import org.books.persistence.catalog.Book;
import org.books.persistence.customer.Address;
import org.books.persistence.customer.CreditCard;
import org.books.persistence.customer.Customer;
import org.books.persistence.order.LineItem;
import org.books.persistence.order.Order;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OrderServiceBeanIT {

    private static final String JNDI_NAME = "java:global/services/OrderService";
    private OrderService orderService;
    private String orderNumber;

    @BeforeClass
    public void init() throws Exception {
        orderService = (OrderService) new InitialContext().lookup(JNDI_NAME);
    }

    @Test
    public void placeOrder() throws Exception {
        //arrange
        Book book = createBook();
        Address address = createAddress();
        CreditCard creditCard = createCreditcard();
        Customer customer = createCustomer("Beat Breu", "beat.breu@radsport.ch");
        customer.setAddress(address);
        customer.setCreditCard(creditCard);
        List<LineItem> lineItems = createLineItems(book);
        //act
        orderNumber = orderService.placeOrder(customer, lineItems);
    }

    @Test(expectedExceptions = EJBException.class, expectedExceptionsMessageRegExp = ".*CreditCardExpiredException.*")
    public void placeOrder_CreditCardExpiredException() throws Exception {
        //arrange
        Book book = createBook();
        Address address = createAddress();
        CreditCard creditCard = createInvalidCreditCard();
        Customer customer = createCustomer("Godi Schmutz", "godi.schmutz@radsport.ch");
        customer.setAddress(address);
        customer.setCreditCard(creditCard);
        List<LineItem> lineItems = createLineItems(book);
        //act
        orderService.placeOrder(customer, lineItems);
    }

    @Test(dependsOnMethods = "placeOrder")
    public void findOrder() throws Exception {
        //act
        Order result = orderService.findOrder(orderNumber);
        //assert
        assertNotNull(result);
    }

    @Test(expectedExceptions = EJBException.class, expectedExceptionsMessageRegExp = ".*OrderNotFoundException.*")
    public void findOrder_OrderNotFound() throws Exception {
        orderService.findOrder("123");
    }

    @Test
    public void getOrders() {
        List<Order> result = orderService.getOrders(new Customer());
        //TODO
    }

    @Test
    public void getOrders_NoOrdersFound() {
        //act
        List<Order> result = orderService.getOrders(new Customer());
        //assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void cancelOrder() throws Exception {
        orderService.cancelOrder(new Order());
        //TODO
    }

    @Test(expectedExceptions = EJBException.class, expectedExceptionsMessageRegExp = ".*OrderNotCancelableException.*")
    public void cancelOrder_OrderIsNotYetPersisted() throws Exception {
        orderService.cancelOrder(new Order());
        //TODO
    }

    private Customer createCustomer(String name, String email) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setName(name);
        customer.setAddress(new Address());
        customer.setCreditCard(new CreditCard());
        return customer;
    }

    private CreditCard createCreditcard() {
        CreditCard creditcard = new CreditCard();
        creditcard.setType(CreditCard.Type.MasterCard);
        creditcard.setNumber("5411222233334445");
        creditcard.setExpirationDate(new Date(System.currentTimeMillis() + 10000));
        return creditcard;
    }

    private CreditCard createInvalidCreditCard() {
        CreditCard creditcard = new CreditCard();
        creditcard.setType(CreditCard.Type.MasterCard);
        creditcard.setNumber("5411222233334445");
        creditcard.setExpirationDate(new Date(System.currentTimeMillis() - 10000));
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
        book.setPrice(new BigDecimal("23.99"));
        return book;
    }

    private List<LineItem> createLineItems(Book book) {
        List<LineItem> lineItems = Lists.newArrayList();
        final LineItem lineItem = new LineItem();
        lineItem.setBook(book);
        lineItem.setQuantity(1);
        lineItems.add(lineItem);
        return lineItems;
    }

}
