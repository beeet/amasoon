package org.books.service.security;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.books.persistence.security.User;
import org.books.utils.Cryptor;

@Stateless
public class AuthenticationServiceBean implements AuthenticationService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void register(String username, String password) throws UserAlreadyExistsException {
        try {
            findUser(username);
            throw new UserAlreadyExistsException();
        } catch (UserNotFoundException e) {
            em.persist(new User(username, password));
        }
    }

    public User findUser(String name) throws UserNotFoundException {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_NAME, User.class);
        query.setParameter("name", name);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new UserNotFoundException(e);
        }
    }

    @Override
    public void authenticate(String username, String password) throws UserNotFoundException, InvalidPasswordException {
        User user = findUser(username);
        try {
            if (!user.getPassword().equals(Cryptor.hash(password))) {
                throw new InvalidPasswordException();
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new EJBException(ex);
        }
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) throws UserNotFoundException, InvalidPasswordException {
        User user = findUser(username);
        try {
            if (!user.getPassword().equals(Cryptor.hash(oldPassword))) {
                throw new InvalidPasswordException();
            } else {
                user.setPassword(Cryptor.hash(newPassword));
                em.persist(user);
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new EJBException(ex);
        }
    }

}
