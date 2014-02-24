package org.books.persistence.customer;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class CreditCard implements Serializable {

    public enum Type implements Serializable {

        MasterCard, Visa
    }

    @Enumerated(EnumType.STRING)
    private Type type;
    private String number;
    @Temporal(TemporalType.DATE)
    @ExpirationDateConstraint
    private Date expirationDate;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

}
