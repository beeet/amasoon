package org.books.service.order;

import com.google.common.collect.Lists;
import com.sun.enterprise.security.ee.auth.login.ProgrammaticLogin;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import org.books.persistence.catalog.Book;
import org.books.persistence.customer.Address;
import org.books.persistence.customer.CreditCard;
import org.books.persistence.customer.Customer;
import org.books.persistence.order.LineItem;
import org.books.persistence.order.Order;
import org.books.service.customer.CustomerService;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OrderServiceBeanIT {

    private static final String JNDI_NAME_ORDERSERVICE = "java:global/services/OrderService";
    private static final String JNDI_NAME_CUSTOMERSERVICE = "java:global/services/CustomerService";
    private OrderService orderService;
    private CustomerService customerService;
    private Customer customer;
    private String orderNumber;

    @BeforeClass
    public void init() throws Exception {
        System.setProperty("java.security.auth.login.config", "src/test/resources/appclientlogin.conf");
        new ProgrammaticLogin().login("john", "john".toCharArray());
        orderService = (OrderService) new InitialContext().lookup(JNDI_NAME_ORDERSERVICE);
        customerService = (CustomerService) new InitialContext().lookup(JNDI_NAME_CUSTOMERSERVICE);
    }

    @Test
    public void placeOrder() throws Exception {
        //arrange
        Address address = createAddress();
        CreditCard creditCard = createCreditcard();
        customer = createCustomer("Beat Breu", "beat.breu@radsport.ch");
        customer.setAddress(address);
        customer.setCreditCard(creditCard);
        customerService.addCustomer(customer);
        List<LineItem> lineItems = createLineItems(createBook());
        //act
        orderNumber = orderService.placeOrder(customer, lineItems);
    }

    @Test(expectedExceptions = CreditCardExpiredException.class)
    public void placeOrder_CreditCardExpiredException() throws Throwable {
        //arrange
        Book book = createBook();
        Address address = createAddress();
        CreditCard creditCard = createInvalidCreditCard();
        Customer cust = createCustomer("Godi Schmutz", "godi.schmutz@radsport.ch");
        cust.setAddress(address);
        cust.setCreditCard(creditCard);
        List<LineItem> lineItems = createLineItems(book);
        //act
        orderService.placeOrder(cust, lineItems);
    }

    @Test(dependsOnMethods = "placeOrder")
    public void findOrder() throws Exception {
        //act
        Order result = orderService.findOrder(orderNumber);
        //assert
        assertNotNull(result);
    }

    @Test(expectedExceptions = OrderNotFoundException.class)
    public void findOrder_OrderNotFound() throws Throwable {
        try {
            orderService.findOrder("123");
        } catch (EJBException ejbException) {
            throw ejbException.getCause();
        }
    }

    @Test(dependsOnMethods = "placeOrder")
    public void getOrders() throws Exception {
        //act
        Order order = orderService.findOrder(orderNumber);
        List<Order> result = orderService.getOrders(order.getCustomer());
        //assert
        assertEquals(result.size(), 1);
    }

    @Test(dependsOnMethods = "placeOrder")
    public void getOrders_NoOrdersFound() {
        //act
        List<Order> result = orderService.getOrders(new Customer());
        //assert
        assertTrue(result.isEmpty());
    }

    @Test(dependsOnMethods = "getOrders_NoOrdersFound")
    public void getOrders_AccessGrantedForEmployee() throws Exception {
        //act
        new ProgrammaticLogin().login("mary", "mary".toCharArray());
        Order order = orderService.findOrder(orderNumber);
        List<Order> result = orderService.getOrders(order.getCustomer());
        //assert
        assertEquals(result.size(), 1);
    }

    @Test(dependsOnMethods = "getOrders_AccessGrantedForEmployee", expectedExceptions = EJBAccessException.class)
    public void getOrders_AccessDenied_NotLoggedIn() throws Exception {
        //act
        new ProgrammaticLogin().logout();
        Order order = orderService.findOrder(orderNumber);
        List<Order> result = orderService.getOrders(order.getCustomer());
        //assert
        assertEquals(result.size(), 1);
    }

    @Test
    public void cancelOrder() throws Exception {
        //arrange
        Address address = createAddress();
        CreditCard creditCard = createCreditcard();
        Customer cust = createCustomer("Rudi Rüssel", "rudi.ruessel@radsport.ch");
        cust.setAddress(address);
        cust.setCreditCard(creditCard);
        List<LineItem> lineItems = createLineItems(createBook());
        Order order = createOrder(cust, address, creditCard, lineItems);
        //act
        orderService.cancelOrder(order);
    }

    @Test(expectedExceptions = EJBAccessException.class)
    public void cancelOrder_AccessDenied() throws Exception {
        //arrange
        new ProgrammaticLogin().login("mary", "mary".toCharArray());
        Address address = createAddress();
        CreditCard creditCard = createCreditcard();
        Customer cust = createCustomer("Rudi Rüssel", "rudi.ruessel@radsport.ch");
        cust.setAddress(address);
        cust.setCreditCard(creditCard);
        List<LineItem> lineItems = createLineItems(createBook());
        Order order = createOrder(cust, address, creditCard, lineItems);
        //act
        orderService.cancelOrder(order);
    }

    @Test(dependsOnMethods = {"findOrder", "getOrders"}, expectedExceptions = EJBAccessException.class)
    public void cancelOrder_AccessDenied_NotLoggedIn() throws Exception {
        //arrange
        new ProgrammaticLogin().logout();
        Address address = createAddress();
        CreditCard creditCard = createCreditcard();
        Customer cust = createCustomer("Rudi Rüssel", "rudi.ruessel@radsport.ch");
        cust.setAddress(address);
        cust.setCreditCard(creditCard);
        List<LineItem> lineItems = createLineItems(createBook());
        Order order = createOrder(cust, address, creditCard, lineItems);
        //act
        orderService.cancelOrder(order);
    }

    @Test(expectedExceptions = OrderNotCancelableException.class, enabled = false)
    public void cancelOrder_OrderIsNotYetPersisted() throws Throwable {
        //arrange
        new ProgrammaticLogin().login("john", "john".toCharArray());
        Order order = new Order();
        order.setStatus(Order.Status.closed);
        //act
        try {
            orderService.cancelOrder(order);
        } catch (EJBException ejbException) {
            throw ejbException.getCause();
        }
    }

    private Customer createCustomer(String name, String email) {
        Random randomGenerator = new Random();
        Customer cust = new Customer();
        cust.setEmail(randomGenerator.nextInt(10000000) + email);
        cust.setName(name);
        cust.setAddress(new Address());
        cust.setCreditCard(new CreditCard());
        return cust;
    }

    private CreditCard createCreditcard() {
        CreditCard creditcard = new CreditCard();
        creditcard.setType(CreditCard.Type.MasterCard);
        creditcard.setNumber("5411222233334445");
        creditcard.setExpirationDate(new Date(115, 6, 18));
        return creditcard;
    }

    private CreditCard createInvalidCreditCard() {
        CreditCard creditcard = new CreditCard();
        creditcard.setType(CreditCard.Type.MasterCard);
        creditcard.setNumber("5411222233334445");
        creditcard.setExpirationDate(new Date(113, 6, 18));
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

    private Order createOrder(Customer customer, Address address, CreditCard creditcard, List<LineItem> lineItems) {
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

}
