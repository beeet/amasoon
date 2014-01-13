package org.books.service.catalog;

import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
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
        em.persist(em.merge(book));///TODO wrap exception
    }

    @Override
    public Book findBook(String isbn) throws BookNotFoundException {
        TypedQuery<Book> query = em.createNamedQuery("Book.findByISBN", Book.class);
        query.setParameter("isbn", isbn);
        Book book = query.getSingleResult();
        if (null == book) {
            throw new BookNotFoundException();
        }
        return book;
    }

    @Override
    public List<Book> searchBooks(String... keywords) {
        CriteriaQuery<Book> expr = BookQueryFactory.findByKeywords(em, Arrays.asList(keywords));
        TypedQuery<Book> query = em.createQuery(expr);
        return query.getResultList();
    }

}
