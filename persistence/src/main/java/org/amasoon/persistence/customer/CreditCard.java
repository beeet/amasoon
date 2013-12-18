package org.amasoon.persistence.customer;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.amasoon.persistence.BaseEntity;

@Entity
public class CreditCard extends BaseEntity {

    public enum Type {

        MasterCard, Visa
    }

    @Enumerated(EnumType.STRING)
    private Type type;
    private String number;
    private Timestamp expirationDate;

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

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

}
