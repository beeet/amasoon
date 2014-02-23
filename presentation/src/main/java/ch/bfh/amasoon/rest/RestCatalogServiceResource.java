package ch.bfh.amasoon.rest;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.books.persistence.catalog.Book;
import org.books.service.catalog.CatalogService;

@Path("catalog")
@RequestScoped
public class RestCatalogServiceResource {

    private static final String URL_SPACE_CHAR = "%20";

    @EJB
    CatalogService catalogService;

    public RestCatalogServiceResource() {
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public List<ch.bfh.amasoon.rest.Book> searchBooks(@QueryParam("keywords") String keywords, @QueryParam("maxresults") Integer maxResults) {
        List<Book> foundBooks;
        try {
            if (verifyInputAndCheckIfMaxResultIsDefined(keywords, maxResults)) {
                foundBooks = catalogService.searchBooks(keywords.split(URL_SPACE_CHAR));
            } else {
                foundBooks = catalogService.searchBooks(maxResults, keywords.split(URL_SPACE_CHAR));
            }
        } catch (Exception e) {
            throw new UnexpectedSystemErrorException(e.getMessage());
        }
        return convertBooksToDtos(foundBooks);
    }

    private List<ch.bfh.amasoon.rest.Book> convertBooksToDtos(List<org.books.persistence.catalog.Book> books) {
        List<ch.bfh.amasoon.rest.Book> bookDtos = new ArrayList<>();
        for (org.books.persistence.catalog.Book book : books) {
            bookDtos.add(convertToDto(book));
        }
        return bookDtos;
    }

    private boolean verifyInputAndCheckIfMaxResultIsDefined(String keywords, Integer maxResult) {
        if (Strings.isNullOrEmpty(keywords)) {
            throw new MissingKeywordsException("Keywords are missing");
        }
        return maxResult == null;
    }

    private ch.bfh.amasoon.rest.Book convertToDto(org.books.persistence.catalog.Book book) {
        ch.bfh.amasoon.rest.Book dto = new ch.bfh.amasoon.rest.Book();
        dto.setIsbn(book.getIsbn());
        dto.setAuthors(book.getAuthors());
        dto.setBinding(book.getBinding());
        dto.setNumberOfPages(book.getNumberOfPages());
        dto.setPrice(book.getPrice());
        dto.setPublicationDate(book.getPublicationDate());
        dto.setPublisher(book.getPublisher());
        dto.setTitle(book.getTitle());
        return dto;
    }

}
