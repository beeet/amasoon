package org.amasoon.persistence.customer;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import org.amasoon.persistence.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class Customer extends BaseEntity {

    public static final String FIND_BY_EMAIL = "Customer.findByEmail";
    @NotNull
    private String email;
    private String name;
    @Embedded
    @NotNull
    private Address address;
    @Embedded
    @NotNull
    private CreditCard creditCard;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
