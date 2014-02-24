package org.books.persistence.customer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExpirationDateValidator implements ConstraintValidator<ExpirationDateConstraint, Date> {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

    @Override
    public void initialize(ExpirationDateConstraint constr) {
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public boolean isValid(Date expirationDate, ConstraintValidatorContext cvc) {
        if (null == expirationDate) {
            //NULL wird nicht als falsch interpretiert (kein Verstoss)
            return true;
        }
        return sdf.format(expirationDate).compareTo(sdf.format(new java.util.Date())) > -1;
    }
}
