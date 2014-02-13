package org.books.service.catalog;

import com.amazon.webservices.AWSECommerceService;
import com.amazon.webservices.AWSECommerceServicePortType;
import com.amazon.webservices.Errors;
import com.amazon.webservices.Item;
import com.amazon.webservices.ItemAttributes;
import com.amazon.webservices.ItemSearch;
import com.amazon.webservices.ItemSearchRequest;
import com.amazon.webservices.ItemSearchResponse;
import com.amazon.webservices.Items;
import com.amazon.webservices.Request;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.books.persistence.catalog.Book;
import org.books.utils.CredentialProperties;

public class AmazonCatalogClient implements AmazonCatalog {

    private static AmazonCatalogClient instance;
    private final AWSECommerceServicePortType amazonServicePortType;
    private static final int MAX_ITEM_PAGE = 10;
    private static final String RESPONSE_GROUP = "ItemAttributes";
    private static final String SEARCH_INDEX = "Books";

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
        List<Book> books = new ArrayList<>();
        itempageloop:
        for (int i = 1; i <= MAX_ITEM_PAGE; i++) {
            ItemSearchResponse response = amazonServicePortType.itemSearch(getItemSearch(keywords, i));
            checkAmazonResponseForErrors(response);
            for (Items items : response.getItems()) {
                for (Item item : items.getItem()) {
                    if (books.size() < maxResults) {
                        addBookIfItIsValid(item.getItemAttributes(), books);
                    } else {
                        break itempageloop;
                    }
                }
            }
        }
        return books;
    }

    private void addBookIfItIsValid(ItemAttributes itemAttributes, List<Book> books) {
        if (containsRequiredAttributes(itemAttributes)) {
            try {
                books.add(createBook(itemAttributes));
            } catch (ParseException ex) {
                Logger.getLogger(AmazonCatalogClient.class.getName()).log(Level.INFO, "Invalid book received: " + itemAttributes.getTitle(), ex);
            }
        }
    }

    private boolean containsRequiredAttributes(ItemAttributes itemAttributes) {
        // todo: use google guice
        return itemAttributes.getISBN() != null && !itemAttributes.getISBN().equals("")
                && itemAttributes.getAuthor() != null && !itemAttributes.getAuthor().isEmpty()
                && itemAttributes.getBinding() != null && !itemAttributes.getBinding().equals("")
                && itemAttributes.getNumberOfPages() != null
                && itemAttributes.getListPrice() != null && itemAttributes.getListPrice().getAmount() != null
                && itemAttributes.getPublicationDate() != null && !itemAttributes.getPublicationDate().equals("")
                && itemAttributes.getPublisher() != null && !itemAttributes.getPublisher().equals("")
                && itemAttributes.getTitle() != null && !itemAttributes.getTitle().equals("");
    }

    private Book createBook(ItemAttributes itemAttributes) throws ParseException {
        Book book = new Book();
        book.setIsbn(itemAttributes.getISBN());
        book.setAuthors(convertListToString(itemAttributes.getAuthor()));
        book.setBinding(itemAttributes.getBinding());
        book.setNumberOfPages(itemAttributes.getNumberOfPages().intValue());
        book.setPrice(new BigDecimal(itemAttributes.getListPrice().getAmount()).setScale(2).divide(new BigDecimal(100)));
        book.setPublicationDate(convertStringToDate(itemAttributes.getPublicationDate()));
        book.setPublisher(itemAttributes.getPublisher());
        book.setTitle(itemAttributes.getTitle());
        return book;
    }

    private ItemSearch getItemSearch(String[] keywords, int itemPage) {
        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setSearchIndex(SEARCH_INDEX);
        itemSearchRequest.getResponseGroup().add(RESPONSE_GROUP);
        itemSearchRequest.setKeywords(getKeywordsString(keywords));
        itemSearchRequest.setItemPage(BigInteger.valueOf(itemPage));
        ItemSearch itemSearch = new ItemSearch();
        itemSearch.setAssociateTag(new CredentialProperties().getAssociateTag());
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

    private String convertListToString(List<String> strings) {
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

    private java.sql.Date convertStringToDate(String dateString) throws ParseException {
        switch (dateString.length()) {
            case 4:
                dateString = dateString + "-01-01"; //2012 => 2012-01-01
            case 7:
                dateString = dateString + "-01"; //2012-12 => 2012-12-01
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return new java.sql.Date(dateFormat.parse(dateString).getTime());
    }

    private void checkAmazonResponseForErrors(ItemSearchResponse response) throws AmazonException {
        ArrayList<String> errorList = new ArrayList<>();
        for (Items items : response.getItems()) {
            Request request = items.getRequest();
            if (request != null && request.getErrors() != null) {
                for (Errors.Error error : request.getErrors().getError()) {
                    errorList.add(error.getMessage());
                }
            }
        }
        if (errorList.size() > 0) {
            throw new AmazonException(errorList.toString());
        }
    }

}
