package org.books.service.catalog;

import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import org.books.persistence.catalog.Book;
import org.books.persistence.catalog.BookQueryFactory;

@Stateless(name = "CatalogService")
public class CatalogServiceBean implements CatalogService {

    @PersistenceContext
    EntityManager em;

    @Override
    public void addBook(Book book) throws BookAlreadyExistsException {
        try {
            findBook(book.getIsbn());
            throw new BookAlreadyExistsException();
        } catch (BookNotFoundException e) {
            em.persist(em.merge(book));
        }
    }

    @Override
    public Book findBook(String isbn) throws BookNotFoundException {
        TypedQuery<Book> query = em.createNamedQuery("Book.findByISBN", Book.class);
        query.setParameter("isbn", isbn);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new BookNotFoundException(e);
        }
    }

    @Override
    public List<Book> searchBooks(String... keywords) {
        CriteriaQuery<Book> expr = BookQueryFactory.findByKeywords(em, Arrays.asList(keywords));
        TypedQuery<Book> query = em.createQuery(expr);
        return query.getResultList();
    }

}
