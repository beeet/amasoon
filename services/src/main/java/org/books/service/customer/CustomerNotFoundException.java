package org.books.service.customer;

public class CustomerNotFoundException extends Exception {

    public CustomerNotFoundException(Exception e) {
        super(e);
    }

}
