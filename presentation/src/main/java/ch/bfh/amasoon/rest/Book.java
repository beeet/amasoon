package ch.bfh.amasoon.rest;

import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "book")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"isbn", "title", "authors", "publisher", "publicationDate", "binding", "numberOfPages", "price"})
public class Book {

    @XmlElement(name = "isbn")
    private String isbn;
    @XmlElement(name = "title")
    private String title;
    @XmlElement(name = "authors")
    private String authors;
    @XmlElement(name = "publisher")
    private String publisher;
    @XmlElement(name = "date")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date publicationDate;
    @XmlElement(name = "binding")
    private String binding;
    @XmlElement(name = "pages")
    private Integer numberOfPages;
    @XmlElement(name = "price")
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
