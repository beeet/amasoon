package ch.bfh.amasoon.presenter;

import ch.bfh.amasoon.commons.MessageFactory;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.books.persistence.catalog.Book;
import org.books.persistence.order.LineItem;
import org.books.service.order.CreditCardExpiredException;
import org.books.service.order.OrderNotCancelableException;
import org.books.service.order.OrderNotFoundException;
import org.books.service.order.OrderService;

@Named
@SessionScoped
public class OrderBean implements Serializable {

    private static final String PLACE_ORDER_FAILED = "ch.bfh.amasoon.presenter.OrderBean.PLACE_ORDER_FAILED";
    private static final String NO_BOOK_FOUND = "ch.bfh.amasoon.NO_BOOK_FOUND";
    private static final String ORDER_NOT_CANCELABLE = "ch.bfh.amasoon.ORDER_NOT_CANCELABLE";
    @EJB
    private OrderService orderService;
    @Inject
    private CustomerBean customerBean;
    private List<LineItem> lineItems = new ArrayList<>();
    private String orderNumber = "";

    public void addToCart(Book book) {
        boolean isAdditionalBook = true;
        for (LineItem lineItem : lineItems) {
            if (book.getIsbn().equals(lineItem.getBook().getIsbn())) {
                lineItem.setQuantity(lineItem.getQuantity() + 1);
                isAdditionalBook = false;
            }
        }
        if (isAdditionalBook) {
            LineItem lineItem = new LineItem();
            lineItem.setBook(book);
            lineItem.setQuantity(1);
            lineItems.add(lineItem);
        }
    }

    public void removeFromCart(LineItem lineItemToRemove) {
        for (LineItem lineItem : lineItems) {
            if (lineItem.equals(lineItemToRemove)) {
                lineItems.remove(lineItem);
                break;
            }
        }
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public BigDecimal getLineItemPrice(LineItem lineItem) {
        return lineItem.getBook().getPrice().multiply(new BigDecimal(lineItem.getQuantity()));
    }

    public BigDecimal getTotalPrice() {
        BigDecimal totalPrice = new BigDecimal(0.0);
        for (LineItem lineItem : lineItems) {
            totalPrice = totalPrice.add(getLineItemPrice(lineItem));
        }
        return totalPrice;
    }

    public int getTotalBooksAdded() {
        int numberOfBooks = 0;
        for (LineItem lineItem : lineItems) {
            numberOfBooks += lineItem.getQuantity();
        }
        return numberOfBooks;
    }

    public boolean isCartEmpty() {
        return lineItems.isEmpty();
    }

    public String placeOrder() {
        try {
            orderNumber = orderService.placeOrder(customerBean.getCustomer(), lineItems);
            lineItems.clear();
            return "orderConfirmation";
        } catch (CreditCardExpiredException ex) {
            MessageFactory.error(PLACE_ORDER_FAILED);
        }
        return null;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void cancelOrder(String orderNumber) {
        try {
            orderService.cancelOrder(orderService.findOrder(orderNumber));
        } catch (OrderNotFoundException ex) {
            MessageFactory.error(NO_BOOK_FOUND, orderNumber);
        } catch (OrderNotCancelableException ex) {
            MessageFactory.error(ORDER_NOT_CANCELABLE, orderNumber);
        }
    }

}
