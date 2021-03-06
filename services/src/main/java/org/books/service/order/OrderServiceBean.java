package org.books.service.order;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import org.books.persistence.catalog.Book;
import org.books.persistence.customer.CreditCard;
import org.books.persistence.customer.Customer;
import org.books.persistence.order.LineItem;
import org.books.persistence.order.Order;
import org.books.persistence.order.Order.Status;
import org.books.service.catalog.BookAlreadyExistsException;
import org.books.service.catalog.BookNotFoundException;
import org.books.service.catalog.CatalogService;
import org.books.service.customer.CustomerNotFoundException;
import org.books.service.customer.CustomerService;

@Stateless(name = "OrderService")
public class OrderServiceBean implements OrderService {

    @EJB
    private MailService mailService;
    @EJB
    private CatalogService catalogService;
    @EJB
    private CustomerService customerService;
    @Resource(lookup = "jms/orderQueueFactory")
    private ConnectionFactory connectionFactory;
    @Resource(lookup = "jms/orderQueue")
    private Queue orderQueue;
    @PersistenceContext
    EntityManager em;

    @Override
    public String placeOrder(Customer customer, List<LineItem> items) throws CreditCardExpiredException {
        try {
            customer = customerService.findCustomer(customer.getEmail());
        } catch (CustomerNotFoundException ex) {
        }
        final CreditCard creditCard = customer.getCreditCard();
        validateCreditcard(creditCard);
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setCustomer(customer);
        order.setAddress(customer.getAddress());
        order.setCreditCard(creditCard);
        order.setLineItems(storeBooksInLocalDbIfAbsent(items));
        order.setAmount(summarizeTotalOrderAmount(items));
        em.persist(order);
        em.flush();

        sendMessageToOrderProcessor(order.getId());
        mailService.sendMail(order, MessageBuilder.MailType.OrderPlaced);
        return order.getOrderNumber();
    }

    private List<LineItem> storeBooksInLocalDbIfAbsent(List<LineItem> items) {
        List<LineItem> lineItemsWithPersistedBooks = new ArrayList<>();
        Book book;
        for (LineItem item : items) {
            book = item.getBook();
            try {
                book = catalogService.findBook(book.getIsbn());
            } catch (BookNotFoundException e) {
                try {
                    catalogService.addBook(book);
                    book = catalogService.findBook(book.getIsbn());
                } catch (BookAlreadyExistsException | BookNotFoundException ex) {
                    Logger.getLogger(OrderServiceBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            item.setBook(book);
            lineItemsWithPersistedBooks.add(item);
        }
        return lineItemsWithPersistedBooks;
    }

    private void validateCreditcard(final CreditCard creditCard) throws CreditCardExpiredException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<CreditCard>> violations = factory.getValidator().validate(creditCard);
        if (!violations.isEmpty()) {
            throw new CreditCardExpiredException();
        }
    }

    @Override
    public Order findOrder(String number) throws OrderNotFoundException {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_NUMBER, Order.class);
        query.setParameter("orderNumber", number);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            throw new OrderNotFoundException(e);
        }
    }

    @Override
    @RolesAllowed("employee")
    public List<Order> getOrders(Customer customer) {
        TypedQuery<Order> query = em.createNamedQuery(Order.FIND_BY_CUSTOMER, Order.class);
        query.setParameter("customer", customer);
        return query.getResultList();
    }

    @Override
    @RolesAllowed("manager")
    public void cancelOrder(Order order) throws OrderNotCancelableException {
        order = em.merge(order);
        if (!order.isOpen()) {
            throw new OrderNotCancelableException();
        }
        order.setStatus(Status.canceled);
        em.persist(order);
        em.flush();
    }

    private BigDecimal summarizeTotalOrderAmount(List<LineItem> items) {
        BigDecimal amount = BigDecimal.ZERO;
        for (LineItem item : items) {
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            amount = amount.add(item.getBook().getPrice().multiply(quantity));
        }
        return amount;
    }

    private void sendMessageToOrderProcessor(Integer orderId) {
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MapMessage message = session.createMapMessage();
            message.setInt("orderId", orderId);
            session.createProducer(orderQueue).send(message);
        } catch (JMSException ex) {
            Logger.getLogger(OrderServiceBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (JMSException ex) {
                Logger.getLogger(OrderServiceBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
