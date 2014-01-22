package org.books.service.security;

import java.util.Random;
import javax.naming.InitialContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AuthenticationServiceBeanIT {

    private static final String JNDI_NAME = "java:global/services/AuthenticationService";
    private AuthenticationService authenticationService;
    private String userName;

    @BeforeClass
    public void init() throws Exception {
        authenticationService = (AuthenticationService) new InitialContext().lookup(JNDI_NAME);
    }

    @Test
    public void register() throws Exception {
        Random random = new Random();
        userName = new Integer(random.nextInt(100000)).toString();
        authenticationService.register(userName, "crypt");
    }

    @Test(dependsOnMethods = "register", expectedExceptions = UserAlreadyExistsException.class)
    public void register_UserExistsAlready() throws Exception {
        authenticationService.register(userName, "crypt");
    }

    @Test(dependsOnMethods = "register")
    public void authenticate() throws Exception {
        authenticationService.authenticate(userName, "crypt");
    }

    @Test(dependsOnMethods = "register", expectedExceptions = InvalidPasswordException.class)
    public void authenticate_InvalidPassword() throws Exception {
        authenticationService.authenticate(userName, "wrong");
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void authenticate_UserNotFound() throws Exception {
        authenticationService.authenticate("gugus", "wrong");
    }

    @Test(expectedExceptions = UserNotFoundException.class)
    public void changePassword_UserNotFound() throws Exception {
        authenticationService.changePassword("gugus", "wrong", "wrong");
    }

    @Test(dependsOnMethods = "register", expectedExceptions = InvalidPasswordException.class)
    public void changePassword_InvalidPassword() throws Exception {
        authenticationService.changePassword(userName, "wrong", "wrong");
    }

    @Test(dependsOnMethods = "register")
    public void changePassword() throws Exception {
        authenticationService.changePassword(userName, "crypt", "crypto");
        authenticationService.authenticate(userName, "crypto");
    }

    @Test(dependsOnMethods = "changePassword")
    public void changePassword_MultipleChanges() throws Exception {
        authenticationService.changePassword(userName, "crypto", "crypt1");
        authenticationService.changePassword(userName, "crypt1", "crypt2");
        authenticationService.changePassword(userName, "crypt2", "crypt3");
        authenticationService.authenticate(userName, "crypt3");
    }

}
