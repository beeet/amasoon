package org.books.service.customer;

import java.sql.Date;
import java.util.Random;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import org.books.persistence.customer.Address;
import org.books.persistence.customer.CreditCard;
import org.books.persistence.customer.Customer;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CustomerServiceBeanIT {

    private static final String JNDI_NAME = "java:global/services/CustomerService";
    private CustomerService customerService;
    private Customer customer;

    @BeforeClass
    public void init() throws Exception {
        customerService = (CustomerService) new InitialContext().lookup(JNDI_NAME);
    }

    @Test
    public void addCustomer() throws Exception {
        customer = createCustomer();
        customerService.addCustomer(customer);
    }

    @Test(dependsOnMethods = "addCustomer")
    public void findCustomer() throws Exception {
        //act
        Customer result = customerService.findCustomer(customer.getEmail());
        //assert
        assertEquals(result.getName(), customer.getName());
        assertEquals(result.getEmail(), customer.getEmail());
    }

    @Test(expectedExceptions = CustomerAlreadyExistsException.class, dependsOnMethods = "findCustomer")
    public void addCustomer_CustomerAlreadyExistsException() throws Throwable {
        Customer foundCustomer = customerService.findCustomer(customer.getEmail());
        try {
            customerService.addCustomer(foundCustomer);
        } catch (EJBException ejbException) {
            throw ejbException.getCause();
        }
    }

    @Test(expectedExceptions = CustomerNotFoundException.class)
    public void findCustomer_CustomerNotFoundException() throws CustomerAlreadyExistsException, CustomerNotFoundException, Throwable {
        try {
            customerService.findCustomer("notexisting@address.com");
        } catch (EJBException ejbException) {
            throw ejbException.getCause();
        }
    }

    @Test(dependsOnMethods = "findCustomer")
    public void updateCustomer() throws Exception {
        // arrange
        Customer foundCustomer = customerService.findCustomer(customer.getEmail());
        // act
        foundCustomer.setName("Updated Customer");
        foundCustomer.getAddress().setCountry("USA");
        foundCustomer.getCreditCard().setType(CreditCard.Type.Visa);
        customerService.updateCustomer(foundCustomer);
        // assert
        Customer updatedCustomer = customerService.findCustomer(customer.getEmail());
        assertEquals("Updated Customer", updatedCustomer.getName());
        assertEquals("USA", updatedCustomer.getAddress().getCountry());
        assertEquals(CreditCard.Type.Visa, updatedCustomer.getCreditCard().getType());
    }

    private Customer createCustomer() {
        Random randomGenerator = new Random();
        Customer newCustomer = new Customer();
        newCustomer.setAddress(createAddress());
        newCustomer.setCreditCard(createCreditcard());
        newCustomer.setEmail(randomGenerator.nextInt(10000000) + "@address.com");
        newCustomer.setName("New Customer");
        return newCustomer;
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
