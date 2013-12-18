package org.amasoon.persistence.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class BookQueriesTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    private Integer book1Id;
    private Integer book2Id;

    @BeforeTest
    public void setUp() {
        try {
            emf = Persistence.createEntityManagerFactory("amasoon");
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
        em.persist(book1);
        Book book2 = new Book();
        book2.setIsbn("0471777102");
        book2.setTitle("Professional Java JDK 6 Edition");
        book2.setAuthors("W. Clay Richardson, Donald Avondolio, Scot Schrager, Mark W. Mitchell, Jeff Scanlon");
        book2.setPublisher("Wrox");
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
    }

    @Test
    public void findByKeywords() {
        // find title
        List<String> keywords = Arrays.asList("Java", "in");
        Query q = em.createQuery(getCriteriaQuery(keywords));
        List<Object[]> results = q.getResultList();
        List<Book> books = q.getResultList();
        assertEquals(results.size(), 2);

        // find author
        keywords = Arrays.asList("Donald", "Mark", "Jeff");
        q = em.createQuery(getCriteriaQuery(keywords));
        results = q.getResultList();
        books = q.getResultList();
        assertEquals(results.size(), 1);

        // find publisher
        keywords = Arrays.asList("Apress", "pTr");
        q = em.createQuery(getCriteriaQuery(keywords));
        results = q.getResultList();
        books = q.getResultList();
        assertEquals(results.size(), 1);

        // find misc
        keywords = Arrays.asList("4th Edition", "jeff");
        q = em.createQuery(getCriteriaQuery(keywords));
        results = q.getResultList();
        books = q.getResultList();
        assertEquals(results.size(), 2);
    }

    private CriteriaQuery<Book> getCriteriaQuery(List<String> keywords) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        cq.distinct(true);
        List<Predicate> predicates = new ArrayList<>();
        for (String keyword : keywords) {
            predicates.add(cb.like(cb.lower(book.<String>get("title")), "%" + keyword.toLowerCase() + "%"));
            predicates.add(cb.like(cb.lower(book.<String>get("authors")), "%" + keyword.toLowerCase() + "%"));
            predicates.add(cb.like(cb.lower(book.<String>get("publisher")), "%" + keyword.toLowerCase() + "%"));
        }
        cq.where(cb.or(predicates.toArray(new Predicate[predicates.size()])));
        return cq;
    }

}
