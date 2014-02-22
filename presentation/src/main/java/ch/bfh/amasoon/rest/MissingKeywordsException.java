package ch.bfh.amasoon.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class MissingKeywordsException extends WebApplicationException {

    public MissingKeywordsException() {
        super(Status.BAD_REQUEST);
    }

    public MissingKeywordsException(String msg) {
        super(Response.status(Status.BAD_REQUEST).entity(msg).build());
    }
}
