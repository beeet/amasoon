package org.books.utils;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.books.service.catalog.AmazonCatalogClient;
import org.books.service.catalog.AmazonException;

public class StandaloneWsClient {

    private static Logger logger = Logger.getLogger(StandaloneWsClient.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Keywords: ");
        String keywords = scanner.nextLine();
        System.out.print("MaxResults:  ");
        String maxResults = scanner.nextLine();
        try {
            AmazonCatalogClient.getInstance().searchBooks(keywords.split(" "), Integer.valueOf(maxResults));
        } catch (AmazonException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

}
