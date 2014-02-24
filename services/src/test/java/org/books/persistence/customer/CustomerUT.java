package org.books.persistence.customer;

import com.google.common.collect.Iterables;
import java.sql.Date;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CustomerUT {

    private Validator validator;

    @BeforeTest
    public void setupOnce() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validate_IsValid_NoConstraintViolation() {
        //arrange
        CreditCard creditcard = new CreditCard();
        creditcard.setType(CreditCard.Type.MasterCard);
        creditcard.setNumber("5411222233334445");
        creditcard.setExpirationDate(new Date(115, 6, 18));
        //act
        Set<ConstraintViolation<CreditCard>> violations = validator.validate(creditcard);
        //assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validate_IsExpired_ConstraintViolation() {
        //arrange
        CreditCard creditcard = new CreditCard();
        creditcard.setType(CreditCard.Type.MasterCard);
        creditcard.setNumber("5411222233334445");
        creditcard.setExpirationDate(new Date(113, 6, 18));

        //act
        Set<ConstraintViolation<CreditCard>> violations = validator.validate(creditcard);
        //assert
        final ConstraintViolation<CreditCard> violation = Iterables.getOnlyElement(violations);
        assertEquals("expiration date is expired", violation.getMessage());
    }

}
