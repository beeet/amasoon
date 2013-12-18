package org.amasoon.persistence;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class BaseEntity implements Serializable{

    @Id
    @GeneratedValue
    protected Integer id;
    @Version
    protected Integer version;

    public Integer getId() {
        return id;
    }
    
}
