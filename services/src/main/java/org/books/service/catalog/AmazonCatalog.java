package org.books.service.catalog;

import java.util.List;
import javax.ejb.Remote;
import org.books.persistence.catalog.Book;

@Remote
public interface AmazonCatalog {

    /**
     * Searches for books in the Amazon catalog which are associated with the
     * specified keywords.
     *
     * @param keywords the keywords
     * @param maxResults the maximum number of books to be returned
     * @return the list of matching books (may be empty)
     * @throws AmazonException if an unexpected error occurs
     */
    public List<Book> searchBooks(String[] keywords, int maxResults) throws AmazonException;
}
