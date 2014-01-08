package org.books.service.customer;

import javax.ejb.Remote;
import org.books.persistence.customer.Customer;

@Remote
public interface CustomerService {

    public void addCustomer(Customer customer) throws CustomerAlreadyExistsException;

    public Customer findCustomer(String email) throws CustomerNotFoundException;

    public void updateCustomer(Customer customer);
}
