package org.books.persistence.customer;

import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.books.persistence.BaseEntity;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
@NamedQuery(name = "Customer.findByEmail", query = "SELECT c FROM Customer c WHERE c.email = :email")
public class Customer extends BaseEntity implements Serializable {

    public static final String FIND_BY_EMAIL = "Customer.findByEmail";
    @NotNull
    private String email;
    private String name;
    @Embedded
    @NotNull
    private Address address;
    @Embedded
    @Valid
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
