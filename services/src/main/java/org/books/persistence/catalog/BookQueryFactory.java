package org.books.persistence.catalog;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class BookQueryFactory {

    public static CriteriaQuery<Book> findByKeywords(EntityManager em, List<String> keywords) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        cq.distinct(true);
        List<Predicate> andPredicates = new ArrayList<>();
        for (String keyword : keywords) {
            List<Predicate> orPredicates = new ArrayList<>();
            orPredicates.add(cb.like(cb.lower(book.<String>get("title")), "%" + keyword.toLowerCase() + "%"));
            orPredicates.add(cb.like(cb.lower(book.<String>get("authors")), "%" + keyword.toLowerCase() + "%"));
            orPredicates.add(cb.like(cb.lower(book.<String>get("publisher")), "%" + keyword.toLowerCase() + "%"));
            andPredicates.add(cb.or(orPredicates.toArray(new Predicate[orPredicates.size()])));
        }
        cq.where(cb.and(andPredicates.toArray(new Predicate[andPredicates.size()])));
        return cq;
    }
}
