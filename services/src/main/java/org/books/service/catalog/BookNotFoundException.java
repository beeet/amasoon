package org.books.service.catalog;

public class BookNotFoundException extends Exception {

    BookNotFoundException() {
        super();
    }

    BookNotFoundException(Exception e) {
        super(e);
    }

}
