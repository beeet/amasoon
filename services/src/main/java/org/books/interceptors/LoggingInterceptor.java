package org.books.interceptors;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class LoggingInterceptor {

    @AroundInvoke
    public Object log(InvocationContext context) throws Exception {
        Object proceed = null;
        try {
            proceed = context.proceed();
            Logger.getLogger(classname(context)).log(Level.INFO, methodname(context));
        } catch (Exception e) {
            Logger.getLogger(classname(context)).log(Level.SEVERE, methodname(context), e);
            throw e;
        }
        return proceed;
    }

    private String classname(InvocationContext context) {
        return context.getTarget().getClass().getName();
    }

    private String methodname(InvocationContext context) {
        StringBuilder sb = new StringBuilder(classname(context));
        sb.append(".").append(context.getMethod().getName()).append(" was called");
        return sb.toString();
    }

}
