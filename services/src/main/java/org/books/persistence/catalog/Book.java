package org.books.persistence.catalog;

import java.math.BigDecimal;
import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import org.books.persistence.BaseEntity;

@Entity
@NamedQuery(name = "Book.findByISBN", query = "SELECT b FROM Book b WHERE b.isbn = :isbn")
public class Book extends BaseEntity {

    public final static String findByISBN = "Book.findByISBN";
    public final static String findByKeywords = "Book.findByKeywords";

    @NotNull
    private String isbn;
    @NotNull
    private String title;
    @NotNull
    private String authors;
    @NotNull
    private String publisher;
    @NotNull
    private Date publicationDate;
    private String binding;
    private Integer numberOfPages;
    @NotNull
    private BigDecimal price;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
