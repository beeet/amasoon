package org.books.service.catalog;

import com.amazon.webservices.AWSECommerceService;
import com.amazon.webservices.AWSECommerceServicePortType;
import com.amazon.webservices.Item;
import com.amazon.webservices.ItemAttributes;
import com.amazon.webservices.ItemSearch;
import com.amazon.webservices.ItemSearchRequest;
import com.amazon.webservices.ItemSearchResponse;
import com.amazon.webservices.Items;
import java.math.BigDecimal;
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
        ItemSearchResponse response = amazonServicePortType.itemSearch(getItemSearch(keywords));
        List<Book> books = new ArrayList<>();
        for (Items items : response.getItems()) {
            for (Item item : items.getItem()) {
                ItemAttributes itemAttributes = item.getItemAttributes();
                if (containsRequiredAttributes(itemAttributes)) {
                    try {
                        books.add(createBook(itemAttributes));
                    } catch (ParseException ex) {
                        Logger.getLogger(AmazonCatalogClient.class.getName()).log(Level.INFO, null, ex);
                    }
                }
            }
        }
        return books;
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

    private ItemSearch getItemSearch(String[] keywords) {
        ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
        itemSearchRequest.setSearchIndex("Books");
        itemSearchRequest.getResponseGroup().add("ItemAttributes");
        itemSearchRequest.setKeywords(getKeywordsString(keywords));
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

}
