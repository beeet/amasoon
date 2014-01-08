package org.books.service.catalog;

import java.util.List;
import javax.ejb.Stateless;
import org.books.persistence.catalog.Book;

@Stateless
public class CatalogServiceBean implements CatalogService{

    @Override
    public void addBook(Book book) throws BookAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Book findBook(String isbn) throws BookNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Book> searchBooks(String... keywords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
