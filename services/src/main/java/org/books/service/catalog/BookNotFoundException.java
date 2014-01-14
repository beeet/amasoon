package org.books.service.catalog;

import javax.persistence.NoResultException;

public class BookNotFoundException extends Exception {

    BookNotFoundException(NoResultException e) {
        super(e);
    }

}
