package org.books.utils;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.books.persistence.catalog.Book;
import org.books.service.catalog.AmazonCatalogClient;
import org.books.service.catalog.AmazonException;

public class StandaloneWsClient {

    private static final Logger logger = Logger.getLogger(StandaloneWsClient.class.getName());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Keywords: ");
        String keywords = scanner.nextLine();
        System.out.print("MaxResults:  ");
        String maxResults = scanner.nextLine();
        try {
            List<Book> books = AmazonCatalogClient.getInstance().searchBooks(keywords.split(" "), convertAndVerifyMaxResult(maxResults));
            printOut(books);
        } catch (AmazonException ex) {
            logger.log(Level.SEVERE, "Amazon wasn't happy with our request. Try it again!", ex);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, "MaxResult needs to be a digit between 1 and 100.");
        }
    }

    private static void printOut(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("Bummer! No books found with the given parameters. Try it again!");
        }
        int count = 0;
        for (Book book : books) {
            System.out.println(++count + " : " + book);
        }
    }

    private static Integer convertAndVerifyMaxResult(String input) throws IllegalArgumentException {
        try {
            Integer output = Integer.valueOf(input);
            if (output < 1 || output > 100) {
                throw new IllegalArgumentException();
            }
            return output;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
