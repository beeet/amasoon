package org.amasoon.persistence.order;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.amasoon.persistence.BaseEntity;
import org.amasoon.persistence.catalog.Book;

@Entity
public class LineItem extends BaseEntity {

    private Integer quantity = 0;

    @ManyToOne(optional = false)
    private Book book;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
