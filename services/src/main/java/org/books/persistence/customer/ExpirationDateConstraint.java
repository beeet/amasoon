package org.books.persistence.customer;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
@NotNull
@Constraint(validatedBy = {ExpirationDateValidator.class})
@Target(value = {METHOD, FIELD, ANNOTATION_TYPE})
@Retention(value = RUNTIME)
@Documented
public @interface ExpirationDateConstraint {

    public String message() default "expiration date is expired";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
