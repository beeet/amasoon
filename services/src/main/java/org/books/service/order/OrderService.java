package org.books.service.order;

import java.util.List;
import javax.ejb.Remote;
import org.books.persistence.customer.Customer;
import org.books.persistence.order.LineItem;
import org.books.persistence.order.Order;

@Remote
public interface OrderService {

    /**
     * Places an order for books on the bookstore.
     *
     * @param customer the customer of the order
     * @param items the order items
     * @return the order number
     * @throws CreditCardExpiredException if the credit card of the customer has
     * expired
     */
    public String placeOrder(Customer customer, List<LineItem> items) throws CreditCardExpiredException;

    /**
     * Finds an order with the specified number.
     *
     * @param number the order number
     * @return the found order
     * @throws OrderNotFoundException if no order with the specified number
     * exists
     */
    public Order findOrder(String number) throws OrderNotFoundException;

    /**
     * Obtains the orders of the specified customer.
     *
     * @param customer the customer
     * @return the list of orders of the customer (may be empty)
     */
    public List<Order> getOrders(Customer customer);

    /**
     * Cancels the specified order.
     *
     * @param order the order to be canceled
     * @throws OrderNotCancelableException if the order is already closed or
     * canceled
     */
    public void cancelOrder(Order order) throws OrderNotCancelableException;
}
