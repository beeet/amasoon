package org.books.service.catalog;

import com.amazon.webservices.AWSECommerceService;
import com.amazon.webservices.AWSECommerceServicePortType;
import com.amazon.webservices.Item;
import com.amazon.webservices.ItemAttributes;
import com.amazon.webservices.ItemSearch;
import com.amazon.webservices.ItemSearchRequest;
import com.amazon.webservices.ItemSearchResponse;
import com.amazon.webservices.Items;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.books.persistence.catalog.Book;
import org.books.utils.CredentialsLoader;

public class AmazonCatalogClient implements AmazonCatalog {

    private static Logger logger = Logger.getLogger(AmazonCatalogClient.class.getName());

    private static AmazonCatalogClient instance;
    private AWSECommerceServicePortType amazonServicePortType;

    public static AmazonCatalogClient getInstance() {
        if (instance == null) {
            instance = new AmazonCatalogClient();
        }
        return instance;
    }

    public AmazonCatalogClient() {
        AWSECommerceService service = new AWSECommerceService();
        amazonServicePortType = service.getAWSECommerceServicePort();
    }

    @Override
    public List<Book> searchBooks(String[] keywords, int maxResults) throws AmazonException {
        logger.info("AmazonCatalogService called - Keywords: " + Arrays.toString(keywords) + " - maxResults: " + maxResults);
        ItemSearchResponse response = amazonServicePortType.itemSearch(getItemSearch(keywords));
        List<Book> books = new ArrayList<>();
        for (Items items : response.getItems()) {
            for (Item item : items.getItem()) {
                Book book = new Book();
                ItemAttributes itemAttributes = item.getItemAttributes();
                book.setIsbn(itemAttributes.getISBN());
                book.setAuthors(listToString(itemAttributes.getAuthor()));
                book.setBinding(itemAttributes.getBinding());
                book.setNumberOfPages(itemAttributes.getNumberOfPages().intValue());
//                book.setPrice(itemAttributes.getListPrice());
//                book.setPublicationDate(itemAttributes.getPublicationDate());
                book.setPublisher(itemAttributes.getPublisher());
                book.setTitle(itemAttributes.getTitle());
                books.add(book);
            }
        }
        return books;
    }

    private ItemSearch getItemSearch(String[] keywords) {
        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setSearchIndex("Books");
        itemSearchRequest.getResponseGroup().add("ItemAttributes");
        itemSearchRequest.setKeywords(getKeywordsString(keywords));
        ItemSearch itemSearch = new ItemSearch();
        itemSearch.setAssociateTag(new CredentialsLoader().getAssociateTag());
        itemSearch.getRequest().add(itemSearchRequest);
        return itemSearch;
    }

    private String getKeywordsString(String[] keywords) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String keyword : keywords) {
            if (first) {
                sb.append(keyword);
                first = false;
            } else {
                sb.append(" ").append(keyword);
            }
        }
        return sb.toString();
    }

    private String listToString(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String s : strings) {
            if (first) {
                sb.append(s);
                first = false;
            } else {
                sb.append(",").append(s);
            }
        }
        return sb.toString();
    }

}
