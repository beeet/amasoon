package org.books.service.catalog;

import com.google.common.collect.Iterables;
import java.util.List;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import org.books.persistence.catalog.Book;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CatalogServiceBeanIT {

    private static final String JNDI_NAME = "java:global/services/CatalogService";
    private CatalogService catalogService;
    private Book book;

    @BeforeClass
    public void init() throws Exception {
        catalogService = (CatalogService) new InitialContext().lookup(JNDI_NAME);
    }

    @Test
    public void addBook() throws Exception {
        book = createBook();
        catalogService.addBook(book);
    }

    @Test(dependsOnMethods = "addBook")
    public void findBook() throws BookNotFoundException {
        Book result = catalogService.findBook(book.getIsbn());
        Assert.assertEquals(result.getTitle(), book.getTitle());
        Assert.assertEquals(result.getIsbn(), book.getIsbn());
        Assert.assertEquals(result.getPublisher(), book.getPublisher());
    }

    @Test(expectedExceptions = BookAlreadyExistsException.class, dependsOnMethods = "findBook")
    public void addBook_AddTwice_BookAlreadyExistsExceptionExpected() throws Exception, Throwable {
        try {
            catalogService.addBook(book);
        } catch (EJBException ejbException) {
            throw ejbException.getCause();
        }
    }

    @Test(expectedExceptions = BookNotFoundException.class)
    public void findBook_BookDoesNotExist_BookNotFoundExceptionExpected() throws Exception, Throwable {
        try {
            catalogService.findBook("gugus");
        } catch (EJBException ejbException) {
            throw ejbException.getCause();
        }
    }

    @Test(dependsOnMethods = "addBook")
    public void searchBooks_FindOneBook() throws Exception {
        //arrange
        catalogService.addBook(createAnotherBook());
        //act
        List<Book> foundBooks = catalogService.searchBooks("Java", "Schrager", "Wrox");
        //assert
        Assert.assertEquals(foundBooks.size(), 1);
        Book result = Iterables.getFirst(foundBooks, null);
        Assert.assertEquals(result.getTitle(), book.getTitle());
        Assert.assertEquals(result.getIsbn(), book.getIsbn());
        Assert.assertEquals(result.getPublisher(), book.getPublisher());
    }

    @Test(dependsOnMethods = "searchBooks_FindOneBook")
    public void searchBooks_FindTwoBooks() throws Exception {
        //act
        List<Book> foundBooks = catalogService.searchBooks("Java");
        //assert
        Assert.assertEquals(foundBooks.size(), 2);
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
