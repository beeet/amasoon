package org.books.persistence.catalog;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class BookIT {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    private Integer book1Id;
    private Integer book2Id;

    @BeforeTest
    public void setUp() {
        try {
            emf = Persistence.createEntityManagerFactory("bookstoretest");
            em = emf.createEntityManager();
            em.getTransaction().begin();
            insertBooks();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
    }

    @AfterTest
    public void tearDown() {
        try {
            em.getTransaction().begin();

            Book book1 = em.find(Book.class, book1Id);
            em.remove(book1);
            Book book2 = em.find(Book.class, book2Id);
            em.remove(book2);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }

        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }

    private void insertBooks() {
        Book book1 = new Book();
        book1.setIsbn("0131872486");
        book1.setTitle("Thinking in Java (4th Edition)");
        book1.setAuthors("Bruce Eckel");
        book1.setPublisher("Prentice Hall PTR");
        book1.setBinding("Paperback");
        book1.setNumberOfPages(1150);
        book1.setPrice(new BigDecimal("64.99"));
        Calendar c = Calendar.getInstance();
        c.set(2006, 02, 02);
        book1.setPublicationDate(new Date(c.getTimeInMillis()));
        em.persist(book1);
        Book book2 = new Book();
        book2.setIsbn("0471777102");
        book2.setTitle("Professional Java JDK 6 Edition");
        book2.setAuthors("W. Clay Richardson, Donald Avondolio, Scot Schrager, Mark W. Mitchell, Jeff Scanlon");
        book2.setPublisher("Wrox");
        book2.setBinding("Paperback");
        book2.setNumberOfPages(130);
        book2.setPrice(new BigDecimal("52.99"));
        book2.setPublicationDate(new Date(c.getTimeInMillis()));

        em.persist(book2);
        book1Id = book1.getId();
        book2Id = book2.getId();
    }

    @Test
    public void findBookByIsbn() {
        Query q = em.createNamedQuery(Book.findByISBN);
        q.setParameter("isbn", "0131872486");

        Book book = (Book) q.getSingleResult();

        assertNotNull(book);
        assertEquals("Thinking in Java (4th Edition)", book.getTitle());
        assertEquals("Bruce Eckel", book.getAuthors());
        assertEquals("Paperback", book.getBinding());
        assertEquals("0131872486", book.getIsbn());
        assertEquals(new Integer("1150"), book.getNumberOfPages());
        assertEquals(new BigDecimal("64.99"), book.getPrice());
        assertEquals("2006-03-02", book.getPublicationDate().toString());
        assertEquals("Prentice Hall PTR", book.getPublisher());
    }

    @Test
    public void findByKeywords() {
        // find title
        List<String> keywords = Arrays.asList("Java", "in");
        Query q = em.createQuery(BookQueryFactory.findByKeywords(em, keywords));
        List<Book> books = q.getResultList();
        assertEquals(books.size(), 1);

        // find author
        keywords = Arrays.asList("Donald", "Mark", "Jeff");
        q = em.createQuery(BookQueryFactory.findByKeywords(em, keywords));
        books = q.getResultList();
        assertEquals(books.size(), 1);

        // find publisher
        keywords = Arrays.asList("pTr");
        q = em.createQuery(BookQueryFactory.findByKeywords(em, keywords));
        books = q.getResultList();
        assertEquals(books.size(), 1);

        // find misc
        keywords = Arrays.asList("4th Edition", "jeff");
        q = em.createQuery(BookQueryFactory.findByKeywords(em, keywords));
        books = q.getResultList();
        assertEquals(books.size(), 0);

        // find misc
        keywords = Arrays.asList("Java");
        q = em.createQuery(BookQueryFactory.findByKeywords(em, keywords));
        books = q.getResultList();
        assertEquals(books.size(), 2);
    }
}
