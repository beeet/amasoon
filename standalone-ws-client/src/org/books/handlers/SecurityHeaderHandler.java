package org.books.handlers;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.books.utils.Credentials;
import org.books.utils.SignatureGenerator;

public class SecurityHeaderHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String NAMESPACE = "aws";

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {

        boolean isOutboundMessage = ((Boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();

        if (isOutboundMessage) {
            try {
                SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                SOAPMessage message = context.getMessage();
                SOAPHeader header = message.getSOAPHeader();
                if (header == null) {
                    header = envelope.addHeader();
                }
                header.addNamespaceDeclaration(NAMESPACE, "http://security.amazonaws.com/doc/2007-01-01/");
                Credentials credentials = SignatureGenerator.on("ItemSearch");
                SOAPElement awsAccessKeyIdElement = header.addChildElement("AWSAccessKeyId", NAMESPACE);
                awsAccessKeyIdElement.addTextNode(credentials.getAccessKeyId());
                SOAPElement timestampElement = header.addChildElement("Timestamp", NAMESPACE);
                timestampElement.addTextNode(credentials.getTimestamp());
                SOAPElement signatureElement = header.addChildElement("Signature", NAMESPACE);
                signatureElement.addTextNode(credentials.getSignature());

            } catch (SOAPException ex) {
                Logger.getLogger(SecurityHeaderHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return isOutboundMessage;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

}
