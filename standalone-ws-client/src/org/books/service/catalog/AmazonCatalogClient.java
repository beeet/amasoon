package org.books.service.catalog;

import com.amazon.webservices.AWSECommerceService;
import com.amazon.webservices.AWSECommerceServicePortType;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.books.persistence.catalog.Book;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        ItemSearch itemSearch = new ItemSearch();
//        itemSearch.setAWSAccessKeyId(null);
//        itemSearch.setAssociateTag(null);
//        amazonServicePortType.itemSearch();
    }

}
