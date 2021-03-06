package org.books.service.catalog;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.naming.InitialContext;
import org.books.persistence.catalog.Book;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CatalogServiceBeanIT {

    private static final String JNDI_NAME = "java:global/services/CatalogService";
    private CatalogService catalogService;
    private Book book1;
    private Book book2;

    @BeforeClass
    public void init() throws Exception {
        catalogService = (CatalogService) new InitialContext().lookup(JNDI_NAME);
    }

    @Test
    public void addBook() throws Exception {
        //arrange
        book1 = createBook();
        //act
        catalogService.addBook(book1);
    }

    @Test(dependsOnMethods = "addBook")
    public void findBook() throws Exception {
        //act
        Book result = catalogService.findBook(book1.getIsbn());
        //assert
        assertEquals(result.getTitle(), book1.getTitle());
        assertEquals(result.getIsbn(), book1.getIsbn());
        assertEquals(result.getPublisher(), book1.getPublisher());
    }

    @Test(expectedExceptions = BookAlreadyExistsException.class, dependsOnMethods = "findBook")
    public void addBook_AddTwice_BookAlreadyExistsExceptionExpected() throws Throwable {
        catalogService.addBook(book1);
    }

    @Test(expectedExceptions = BookNotFoundException.class)
    public void findBook_BookDoesNotExist_BookNotFoundExceptionExpected() throws Throwable {
        catalogService.findBook("gugus");
    }

    @Test(dependsOnMethods = "addBook", enabled = false)
    public void searchBooks_FindOneBook() throws Exception {
        //arrange
        book2 = createAnotherBook();
        catalogService.addBook(book2);
        //act
        List<Book> foundBooks = catalogService.searchBooks("Java", "Schrager", "Wrox");
        //assert
        Map<String, String> distinctedBooks = new HashMap<>();
        for (Book book : foundBooks) {
            distinctedBooks.put(book.getTitle(), book.getAuthors());
        }
        assertEquals(distinctedBooks.size(), 1);
        assertTrue(distinctedBooks.containsKey("Professional Java JDK 6 Edition Simpson"));
    }

    @Test(dependsOnMethods = "searchBooks_FindOneBook", enabled = false)
    public void searchBooks_FindTwoBooks() throws Exception {
        //act
        List<Book> foundBooks = catalogService.searchBooks("Simpson");
        //assert
        Map<String, String> distinctedBooks = new HashMap<>();
        for (Book book : foundBooks) {
            distinctedBooks.put(book.getTitle(), book.getAuthors());
        }
        assertEquals(distinctedBooks.size(), 2);
        assertTrue(distinctedBooks.containsKey("Professional Java JDK 6 Edition Simpson"));
        assertTrue(distinctedBooks.containsKey("Java for Dummies"));
    }

    @Test(dependsOnMethods = "searchBooks_FindOneBook", enabled = false)
    public void searchBooks_FindNoBooks() throws Exception {
        //act
        List<Book> foundBooks = catalogService.searchBooks("gugus");
        //assert
        assertTrue(foundBooks.isEmpty());
    }

    private static Book createBook() {
        Book book = new Book();
        book.setIsbn(UUID.randomUUID().toString());
        book.setTitle("Professional Java JDK 6 Edition Simpson");
        book.setAuthors("W. Clay Richardson, Donald Avondolio, Scot Schrager, Mark W. Mitchell, Jeff Scanlon");
        book.setPublisher("Wrox");
        book.setBinding("Paperback");
        book.setNumberOfPages(150);
        book.setPrice(BigDecimal.valueOf(23, 99));
        Calendar c = Calendar.getInstance();
        c.set(2008, 02, 02);
        book.setPublicationDate(new Date(c.getTimeInMillis()));
        return book;
    }

    private static Book createAnotherBook() {
        Book book = new Book();
        book.setIsbn(UUID.randomUUID().toString());
        book.setTitle("Java for Dummies");
        book.setAuthors("Bart Simpson");
        book.setPublisher("Apress");
        book.setBinding("Paperback");
        book.setNumberOfPages(1150);
        book.setPrice(BigDecimal.valueOf(64, 99));
        Calendar c = Calendar.getInstance();
        c.set(2006, 02, 02);
        book.setPublicationDate(new Date(c.getTimeInMillis()));
        return book;
    }
}
