package org.books.interceptors;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class ConstraintViolationInterceptor {

    public static final char NEWLINE = '\n';

    @AroundInvoke
    public Object log(InvocationContext context) throws Exception {
        Object proceed = null;
        try {
            proceed = context.proceed();
        } catch (ConstraintViolationException e) {
            StringBuilder sb = new StringBuilder(512);
            Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
            for (ConstraintViolation<?> violation : violations) {
                sb.append("ConstraintViolations: ").append(NEWLINE);
                sb.append("RootBeanClass: ").append(violation.getRootBeanClass()).append(NEWLINE);
                sb.append("PropertyPath: ").append(violation.getPropertyPath()).append(NEWLINE);
                sb.append("InvalidValue: ").append(violation.getInvalidValue()).append(NEWLINE);
            }
            Logger.getLogger(classname(context)).log(Level.SEVERE, sb.toString());
            throw e;
        }
        return proceed;
    }

    private String classname(InvocationContext context) {
        return context.getTarget().getClass().getName();
    }

}
