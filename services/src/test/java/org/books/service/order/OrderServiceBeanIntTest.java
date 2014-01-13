package org.books.service.order;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.naming.InitialContext;
import org.books.persistence.catalog.Book;
import org.books.persistence.customer.Address;
import org.books.persistence.customer.CreditCard;
import org.books.persistence.customer.Customer;
import org.books.persistence.order.LineItem;
import org.books.persistence.order.Order;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OrderServiceBeanIntTest {

//    private static final String JNDI_NAME = "java:global/services/OrderService";
//    private OrderService orderService;
//
//    @BeforeClass
//    public void init() throws Exception {
//        orderService = (OrderService) new InitialContext().lookup(JNDI_NAME);
//    }
//
//    @Test
//    public void placeOrder() throws CreditCardExpiredException {
//        Customer customer = createCustomer("Beat Breu", "beat.breu@radsport.ch");
//        Address address = createAddress();
//        CreditCard creditCard = createCreditcard();
//        Book book = createBook();
//        customer.setAddress(address);
//        customer.setCreditCard(creditCard);
//
//        List<LineItem> lineItems = new ArrayList<>();
//        final LineItem lineItem = new LineItem();
//        lineItem.setBook(book);
//        lineItems.add(lineItem);
//
//        orderService.placeOrder(customer, lineItems);
//    }
//
//    @Test(expectedExceptions = CreditCardExpiredException.class)
//    public void placeOrder_CreditCardExpiredException() throws CreditCardExpiredException {
//        Customer customer = createCustomer("Beat Breu", "beat.breu@radsport.ch");
//        Address address = createAddress();
//        CreditCard creditCard = createInvalidCreditCard();
//        Book book = createBook();
//        customer.setAddress(address);
//        customer.setCreditCard(creditCard);
//
//        List<LineItem> lineItems = new ArrayList<>();
//        final LineItem lineItem = new LineItem();
//        lineItem.setBook(book);
//        lineItems.add(lineItem);
//
//        orderService.placeOrder(customer, lineItems);
//    }
//
//    private Customer createCustomer(String name, String email) {
//        Customer customer = new Customer();
//        customer.setEmail(email);
//        customer.setName(name);
//        customer.setAddress(new Address());
//        customer.setCreditCard(new CreditCard());
//        return customer;
//    }
//
//    private Order createOrder(Customer customer, Address address, CreditCard creditcard, Set<LineItem> lineItems) {
//        Order order = new Order();
//        order.setOrderDate(new Date(System.currentTimeMillis()));
//        order.setAmount(BigDecimal.TEN);
//        order.setOrderNumber(UUID.randomUUID().toString());
//        order.setCustomer(customer);
//        order.setAddress(address);
//        order.setCreditCard(creditcard);
//        order.setLineItems(lineItems);
//        order.setStatus(Order.Status.open);
//        order.setAmount(BigDecimal.valueOf(52L));
//        return order;
//    }
//
//    private CreditCard createCreditcard() {
//        CreditCard creditcard = new CreditCard();
//        creditcard.setType(CreditCard.Type.MasterCard);
//        creditcard.setNumber("5411222233334445");
//        creditcard.setExpirationDate(new Date(System.currentTimeMillis() + 10000));
//        return creditcard;
//    }
//
//    private CreditCard createInvalidCreditCard() {
//        CreditCard creditcard = new CreditCard();
//        creditcard.setType(CreditCard.Type.MasterCard);
//        creditcard.setNumber("5411222233334445");
//        creditcard.setExpirationDate(new Date(System.currentTimeMillis() - 10000));
//        return creditcard;
//    }
//
//    private Address createAddress() {
//        Address address = new Address();
//        address.setStreet("Zu Hause 5");
//        address.setZipCode("1234");
//        address.setCity("Musterdorf");
//        address.setCountry("Schweiz");
//        return address;
//    }
//
//    private Book createBook() {
//        Book book = new Book();
//        book.setAuthors("Adam Bien");
//        book.setBinding("lose");
//        book.setIsbn("15646651313212");
//        book.setNumberOfPages(5);
//        book.setPublisher("o'Reily");
//        book.setPublicationDate(new Date(System.currentTimeMillis()));
//        book.setTitle("Java in a Nutshel");
//        return book;
//    }
}
