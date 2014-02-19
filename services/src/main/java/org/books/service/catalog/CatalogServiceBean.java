package org.books.service.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.books.persistence.catalog.Book;

@Stateless(name = "CatalogService")
public class CatalogServiceBean implements CatalogService {

    @PersistenceContext
    EntityManager em;

    @EJB
    AmazonCatalog amazonCatalog;

    @Resource(name = "maxResults")
    private Integer maxResults;

    @Override
    public void addBook(Book book) throws BookAlreadyExistsException {
        try {
            findBook(book.getIsbn());
            throw new BookAlreadyExistsException();
        } catch (BookNotFoundException e) {
            em.persist(book);
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
        return this.searchBooks(maxResults, keywords);
    }

    @Override
    public List<Book> searchBooks(int maxResults, String... keywords) {
        List<Book> results = new ArrayList<>();
        try {
            results = amazonCatalog.searchBooks(keywords, maxResults);
        } catch (AmazonException ex) {
            Logger.getLogger(CatalogServiceBean.class.getName()).log(Level.SEVERE, null, ex);

        }
        return results;
    }

}
