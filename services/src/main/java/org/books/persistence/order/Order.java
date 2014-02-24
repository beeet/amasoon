package org.books.persistence.order;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.books.persistence.BaseEntity;
import org.books.persistence.customer.Address;
import org.books.persistence.customer.CreditCard;
import org.books.persistence.customer.Customer;

@Entity
@Table(name = "ORDERS")
@NamedQueries({
    @NamedQuery(name = "Order.findByNumber",
            query = "SELECT o FROM Order o WHERE o.orderNumber = :orderNumber"),
    @NamedQuery(name = "Order.findByCustomer",
            query = "SELECT o FROM Order o WHERE o.customer = :customer"),})
public class Order extends BaseEntity {

    public static final String FIND_BY_NUMBER = "Order.findByNumber";
    public static final String FIND_BY_CUSTOMER = "Order.findByCustomer";

    public enum Status {

        open, closed, canceled
    }

    private String orderNumber;
    private Date orderDate;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne(optional = false)
    private Customer customer;
    @Embedded
    @NotNull
    private Address address;
    @NotNull
    @Embedded
    private CreditCard creditCard;
    @OneToMany(cascade = CascadeType.ALL)
    private List<LineItem> lineItems = Lists.newArrayList();

    public Order() {
        this.status = Status.open;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isOpen() {
        return Status.open.equals(status);
    }

    public boolean isCanceled() {
        return Status.canceled.equals(status);
    }

    public boolean isClosed() {
        return Status.closed.equals(status);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
