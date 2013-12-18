package org.amasoon.persistence.customer;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import org.amasoon.persistence.BaseEntity;

@Entity
public class Customer extends BaseEntity {

    private String email;
    private String name;
    @OneToOne(optional = false)
    private Address address;
    @OneToOne(optional = false)
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
