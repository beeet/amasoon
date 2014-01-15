package org.books.service.catalog;

import com.google.common.collect.Iterables;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import javax.ejb.EJBException;
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
        try {
            catalogService.addBook(book1);
        } catch (EJBException ejbException) {
            throw ejbException.getCause();
        }
    }

    @Test(expectedExceptions = BookNotFoundException.class)
    public void findBook_BookDoesNotExist_BookNotFoundExceptionExpected() throws Throwable {
        try {
            catalogService.findBook("gugus");
        } catch (EJBException ejbException) {
            throw ejbException.getCause();
        }
    }

    @Test(dependsOnMethods = "addBook")
    public void searchBooks_FindOneBook() throws Exception {
        //arrange
        book2 = createAnotherBook();
        catalogService.addBook(book2);
        //act
        List<Book> foundBooks = catalogService.searchBooks("Java", "Schrager", "Wrox");
        //assert
        Book result = Iterables.getOnlyElement(foundBooks);
        assertEquals(result.getTitle(), book1.getTitle());
        assertEquals(result.getIsbn(), book1.getIsbn());
        assertEquals(result.getPublisher(), book1.getPublisher());
    }

    @Test(dependsOnMethods = "searchBooks_FindOneBook")
    public void searchBooks_FindTwoBooks() throws Exception {
        //act
        List<Book> foundBooks = catalogService.searchBooks("Java");
        //assert
        assertEquals(foundBooks.size(), 2);
    }

    @Test(dependsOnMethods = "searchBooks_FindOneBook")
    public void searchBooks_FindNoBooks() throws Exception {
        //act
        List<Book> foundBooks = catalogService.searchBooks("gugus");
        //assert
        assertTrue(foundBooks.isEmpty());
    }

    private static Book createBook() {
        Book book = new Book();
        book.setIsbn(UUID.randomUUID().toString());
        book.setTitle("Professional Java JDK 6 Edition");
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
