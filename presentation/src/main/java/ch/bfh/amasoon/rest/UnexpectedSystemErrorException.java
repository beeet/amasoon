package ch.bfh.amasoon.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class UnexpectedSystemErrorException extends WebApplicationException {

    public UnexpectedSystemErrorException() {
        super(Status.INTERNAL_SERVER_ERROR);
    }

    public UnexpectedSystemErrorException(String msg) {
        super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(msg).build());
    }
}
