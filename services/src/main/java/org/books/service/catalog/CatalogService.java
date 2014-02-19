package org.books.service.catalog;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Remote;
import org.books.persistence.catalog.Book;

@Remote
public interface CatalogService extends Serializable {

    public void addBook(Book book) throws BookAlreadyExistsException;

    public Book findBook(String isbn) throws BookNotFoundException;

    public List<Book> searchBooks(String... keywords);

    public List<Book> searchBooks(int maxResults, String... keywords);
}
