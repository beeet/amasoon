package org.books.persistence.catalog;

import java.math.BigDecimal;
import java.sql.Date;

public class Book {

    private String isbn;
    private String title;
    private String authors;
    private String publisher;
    private Date publicationDate;
    private String binding;
    private Integer numberOfPages;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.isbn);
        sb.append(" | ");
        sb.append(this.title);
        sb.append(" | ");
        sb.append(this.authors);
        sb.append(" | ");
        sb.append(this.binding);
        sb.append(" | ");
        sb.append(this.numberOfPages);
        sb.append(" | ");
        sb.append(this.publisher);
        sb.append(" | ");
        sb.append(this.publicationDate);
        sb.append(" | ");
        sb.append(this.price);
        return sb.toString();
    }

}
