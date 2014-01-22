package org.books.persistence.security;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
@NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE u.name = :name")
public class User implements Serializable {

    public static final String FIND_BY_NAME = "User.findByName";

    private static final long serialVersionUID = 1L;
    @Id
    private String name;
    private String password;

    public User() {

    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @PrePersist
    public void hashPassword() {
        try {
            this.password = hashPassword(this.password);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, "hashPassword", ex);
        }
    }

    public String hashPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (password == null) {
            return null;
        }
        return new String(MessageDigest.getInstance("SHA-256").digest(password.getBytes("UTF-8")));
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the name fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.books.persistence.security.User[ name=" + name + " ]";
    }

}
