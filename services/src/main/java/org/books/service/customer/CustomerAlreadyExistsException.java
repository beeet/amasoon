package org.books.service.customer;

public class CustomerAlreadyExistsException extends Exception {

    public CustomerAlreadyExistsException() {
        super();
    }

    public CustomerAlreadyExistsException(Exception e) {
        super(e);
    }
}
