package org.books.service.catalog;

import com.google.common.collect.Iterables;
import java.util.List;
import javax.naming.InitialContext;
import org.books.persistence.catalog.Book;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CatalogServiceBeanIntTest {

    private static final String JNDI_NAME = "java:global/services/CatalogService";
    private CatalogService catalogService;

    @BeforeClass
    public void init() throws Exception {
        catalogService = (CatalogService) new InitialContext().lookup(JNDI_NAME);
    }

    @Test
    public void addBook() throws Exception {
        catalogService.addBook(createBook());
    }

    @Test(expectedExceptions = BookAlreadyExistsException.class)
    public void addBook_AddTwice_BookAlreadyExistsExceptionExpected() throws Exception {
        //arrange
        Book book = createBook();
        catalogService.addBook(book);
        //act
        catalogService.addBook(book);
    }

    @Test
    public void findBook() throws Exception {
        //arrange
        Book book = createBook();
        catalogService.addBook(book);
        //act
        Book result = catalogService.findBook(book.getIsbn());
        //assert
        Assert.assertEquals(result, book);
    }

    @Test(expectedExceptions = BookNotFoundException.class)
    public void findBook_BookDoesNotExist_BookNotFoundExceptionExpected() throws Exception {
        catalogService.findBook("gugus");
    }

    @Test
    public void searchBooks_FindOneBook() throws Exception {
        //arrange
        final Book book = createBook();
        catalogService.addBook(book);
        catalogService.addBook(createAnotherBook());
        //act
        List<Book> foundBooks = catalogService.searchBooks("Java,Schrager,Wrox");
        //assert
        Book result = Iterables.getFirst(foundBooks, null);
        Assert.assertEquals(result, book);
    }

    @Test
    public void searchBooks_FindTwoBooks() throws Exception {
        //arrange
        final Book book = createBook();
        catalogService.addBook(book);
        catalogService.addBook(createAnotherBook());
        //act
        List<Book> foundBooks = catalogService.searchBooks("Java");
        //assert
        Assert.assertTrue(foundBooks.size() == 2);
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
