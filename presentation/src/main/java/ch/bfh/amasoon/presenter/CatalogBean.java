package ch.bfh.amasoon.presenter;

import ch.bfh.amasoon.commons.MessageFactory;
import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.books.persistence.catalog.Book;
import org.books.service.catalog.CatalogService;
import org.books.service.catalog.SearchException;

@Named
@SessionScoped
public class CatalogBean implements Serializable {

    private static final String NO_BOOKS_FOUND = "ch.bfh.amasoon.NO_BOOKS_FOUND";
    //private final CatalogServiceMock catalogService = CatalogServiceMock.getInstance();
    @EJB
    private CatalogService catalogService;
    private String keywords;
    private List<Book> books;
    private Book selectedBook;

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public List<Book> getBooks() {
        if (books == null) {
            books = new ArrayList<>();
        }
        return books;
    }

    private void setBooks(List<Book> books) {
        this.books = books;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public void searchBooks() {
        if (Strings.isNullOrEmpty(keywords)) {
            getBooks().clear();
        } else {
            try {
                setBooks(catalogService.searchBooks(keywords.split("\\s+")));
            } catch (SearchException ex) {
                Logger.getLogger(CatalogBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (getBooks().isEmpty()) {
                MessageFactory.info(NO_BOOKS_FOUND);
            }
        }
    }

    public String selectBook(Book book) {
        selectedBook = book;
        return "bookDetails";
    }
}
