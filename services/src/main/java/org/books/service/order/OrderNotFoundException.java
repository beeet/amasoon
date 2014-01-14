package org.books.service.order;

import javax.persistence.NoResultException;

class OrderNotFoundException extends Exception {

    public OrderNotFoundException(NoResultException e) {
        super(e);
    }

}
