package ch.bfh.amasoon.rest;

import java.util.List;
import java.util.ArrayList;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ejb.EJB;
import org.books.service.catalog.CatalogService;
import org.books.persistence.catalog.Book;

@Path("catalog")
@RequestScoped
public class RestCatalogServiceResource {

    private static final String URL_SPACE_CHAR = "%20";

    @EJB
    CatalogService catalogService;

    public RestCatalogServiceResource() {
    }

    @GET
    @Produces("application/xml")
    public List<BookDto> searchBooks(@QueryParam("keywords") String keywords, @QueryParam("maxresults") Integer maxResults) {
        List<BookDto> searchResult = new ArrayList<>();
        List<Book> foundBooks = catalogService.searchBooks(maxResults, keywords.split(URL_SPACE_CHAR));
        for (Book book : foundBooks) {
            searchResult.add(convertToDto(book));
        }
        return searchResult;
    }

    private BookDto convertToDto(Book book) {
        BookDto dto = new BookDto();
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
