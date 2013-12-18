package org.amasoon.persistence.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.amasoon.persistence.BaseEntity;

@Entity
public class Order extends BaseEntity {

    private String number;
    private Timestamp orderDate;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Status status;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Timestamp getDate() {
        return orderDate;
    }

    public void setDate(Timestamp date) {
        this.orderDate = date;
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

    public enum Status {

        open, closed, canceled
    }

}
