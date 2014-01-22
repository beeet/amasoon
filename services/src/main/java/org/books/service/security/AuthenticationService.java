package org.books.service.security;

import javax.ejb.Remote;

@Remote
public interface AuthenticationService {

    public void register(String username, String password)
            throws UserAlreadyExistsException;

    public void authenticate(String username, String password)
            throws UserNotFoundException, InvalidPasswordException;

    public void changePassword(String username, String oldPassword, String newPassword)
            throws UserNotFoundException, InvalidPasswordException;
}
