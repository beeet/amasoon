package org.books.service.security;

import javax.persistence.NoResultException;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(NoResultException e) {
        super(e);
    }

}
