package org.books.service.customer;

import java.sql.Date;
import javax.naming.InitialContext;
import org.books.persistence.customer.Address;
import org.books.persistence.customer.CreditCard;
import org.books.persistence.customer.Customer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CustomerServiceBeanIntTest {

//    private static final String JNDI_NAME = "java:global/services/CustomerService";
//    private CustomerService customerService;
//
//    @BeforeClass
//    public void init() throws Exception {
//        customerService = (CustomerService) new InitialContext().lookup(JNDI_NAME);
//    }
//
//    @Test
//    public void addCustomer() throws CustomerAlreadyExistsException {
//        customerService.addCustomer(createCustomer());
//    }
//
//    @Test(expectedExceptions = CustomerAlreadyExistsException.class)
//    public void addCustomer_CustomerAlreadyExistsException() throws Exception {
//        Customer newCustomer = createCustomer();
//        customerService.addCustomer(newCustomer);
//        customerService.addCustomer(newCustomer);
//    }
//
//    @Test
//    public void findCustomer() throws CustomerAlreadyExistsException, CustomerNotFoundException {
//        Customer newCustomer = createCustomer();
//        customerService.addCustomer(newCustomer);
//        customerService.findCustomer(newCustomer.getEmail());
//    }
//
//    @Test
//    public void findCustomer_CustomerNotFoundException() throws CustomerAlreadyExistsException, CustomerNotFoundException {
//        Customer newCustomer = createCustomer();
//        customerService.addCustomer(newCustomer);
//        customerService.findCustomer("notexisting@address.com");
//    }
//
//    @Test
//    public void updateCustomer() throws CustomerAlreadyExistsException, CustomerNotFoundException {
//        Customer customer = createCustomer();
//        customerService.addCustomer(customer);
//        customer.setName("Updated Customer");
//        customerService.updateCustomer(customer);
//        Customer updatedCustomer = customerService.findCustomer("email@address.com");
//        Assert.assertEquals("Updated Customer", updatedCustomer.getName());
//    }
//
//    private Customer createCustomer() {
//        Customer newCustomer = new Customer();
//        newCustomer.setAddress(createAddress());
//        newCustomer.setCreditCard(createCreditcard());
//        newCustomer.setEmail("email@address.com");
//        newCustomer.setName("New Customer");
//        return newCustomer;
//    }
//
//    private CreditCard createCreditcard() {
//        CreditCard creditcard = new CreditCard();
//        creditcard.setType(CreditCard.Type.MasterCard);
//        creditcard.setNumber("5411222233334445");
//        creditcard.setExpirationDate(new Date(System.currentTimeMillis()));
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
}
