package org.books.service.order;

public class OrderNotFoundException extends Exception {

    public OrderNotFoundException() {
        super();
    }

    public OrderNotFoundException(Exception e) {
        super(e);
    }

}
