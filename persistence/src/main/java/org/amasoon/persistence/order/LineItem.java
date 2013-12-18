package org.amasoon.persistence.order;

import javax.persistence.Entity;
import org.amasoon.persistence.BaseEntity;

@Entity
public class LineItem extends BaseEntity {

    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
