package org.books.service.catalog;

import java.util.List;
import javax.ejb.Remote;
import org.books.persistence.catalog.Book;

@Remote
public interface CatalogService {

    public void addBook(Book book) throws BookAlreadyExistsException;

    public Book findBook(String isbn) throws BookNotFoundException;

    public List<Book> searchBooks(String... keywords);

    public List<Book> searchBooks(Integer maxResults, String... keywords);
}
