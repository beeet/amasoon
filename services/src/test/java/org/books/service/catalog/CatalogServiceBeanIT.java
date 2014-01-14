package org.books.service.catalog;

import com.google.common.collect.Iterables;
import java.util.List;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import org.books.persistence.catalog.Book;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

public class CatalogServiceBeanIT {

    private static final String JNDI_NAME = "java:global/services/CatalogService";
    private CatalogService catalogService;
    private Book book1;
    private Book book2;

    // @BeforeClass
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

    @Test(dependsOnMethods = "findBook", expectedExceptions = EJBException.class, expectedExceptionsMessageRegExp = ".*BookAlreadyExistsException.*")
    public void addBook_AddTwice_BookAlreadyExistsExceptionExpected() throws Exception {
        catalogService.addBook(book1);
    }

    @Test(expectedExceptions = EJBException.class, expectedExceptionsMessageRegExp = ".*BookNotFoundException.*")
    public void findBook_BookDoesNotExist_BookNotFoundExceptionExpected() throws Exception {
        catalogService.findBook("gugus");
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
        book.setIsbn("0471777102");
        book.setTitle("Professional Java JDK 6 Edition");
        book.setAuthors("W. Clay Richardson, Donald Avondolio, Scot Schrager, Mark W. Mitchell, Jeff Scanlon");
        book.setPublisher("Wrox");
        return book;
    }

    private static Book createAnotherBook() {
        Book book = new Book();
        book.setIsbn("0471777103");
        book.setTitle("Java for Dummies");
        book.setAuthors("Bart Simpson");
        book.setPublisher("Apress");
        return book;
    }
}
