package org.amasoon.persistence.order;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.amasoon.persistence.BaseEntity;
import org.amasoon.persistence.customer.Address;
import org.amasoon.persistence.customer.CreditCard;
import org.amasoon.persistence.customer.Customer;

@Entity
@Table(name = "ORDERS")
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
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Customer customer;
    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Address address;
    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private CreditCard creditCard;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<LineItem> lineItems = new HashSet<>();

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

    public Set<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(Set<LineItem> lineItems) {
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

    public void setStatus(Status status) {
        this.status = status;
    }

}
