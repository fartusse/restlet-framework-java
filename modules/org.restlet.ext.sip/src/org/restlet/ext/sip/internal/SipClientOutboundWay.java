/**
 * Copyright 2005-2010 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL 1.0 (the
 * "Licenses"). You can select the license that you prefer but you may not use
 * this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1.php
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1.php
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

package org.restlet.ext.sip.internal;

import org.restlet.Response;
import org.restlet.data.Parameter;
import org.restlet.engine.connector.ClientOutboundWay;
import org.restlet.engine.connector.Connection;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.engine.header.TagWriter;
import org.restlet.ext.sip.SipRecipientInfo;
import org.restlet.ext.sip.SipRequest;
import org.restlet.util.Series;

/**
 * SIP client outbound way.
 * 
 * @author Thierry Boileau
 */
public class SipClientOutboundWay extends ClientOutboundWay {

    /**
     * Constructor.
     * 
     * @param connection
     *            The parent connection.
     * @param bufferSize
     *            The byte buffer size.
     */
    public SipClientOutboundWay(Connection<?> connection, int bufferSize) {
        super(connection, bufferSize);
    }

    @Override
    protected void addGeneralHeaders(Series<Parameter> headers) {
        if (getMessage().getRequest() instanceof SipRequest) {
            // Put the VIA header at the top most level.
            SipRequest sipRequest = (SipRequest) getMessage().getRequest();

            if (!sipRequest.getSipRecipientsInfo().isEmpty()) {
                for (SipRecipientInfo recipient : sipRequest
                        .getSipRecipientsInfo()) {
                    // Generate one VIA header per recipient
                    headers.add(HeaderConstants.HEADER_VIA,
                            SipRecipientInfoWriter.write(recipient));
                }
            }
        }

        super.addGeneralHeaders(headers);
    }

    @Override
    protected void addRequestHeaders(Series<Parameter> headers) {
        SipRequest sipRequest = null;

        if (getMessage().getRequest() instanceof SipRequest) {
            sipRequest = (SipRequest) getMessage().getRequest();

            if (sipRequest.getCallId() != null) {
                headers.add(SipConstants.HEADER_CALL_ID, sipRequest.getCallId());
            }

            if (sipRequest.getCallSequence() != null) {
                headers.add(SipConstants.HEADER_CALL_SEQ,
                        sipRequest.getCallSequence());
            }

            if (sipRequest.getFrom() != null) {
                headers.add(HeaderConstants.HEADER_FROM,
                        AddressWriter.write(sipRequest.getFrom()));
            }

            if (sipRequest.getTo() != null) {
                headers.add(SipConstants.HEADER_TO,
                        AddressWriter.write(sipRequest.getTo()));
            }

            if (sipRequest.getAlertInfo() != null) {
                headers.add(SipConstants.HEADER_ALERT_INFO,
                        AddressWriter.write(sipRequest.getAlertInfo()));
            }

            if (!sipRequest.getAllowedEventTypes().isEmpty()) {
                headers.add(SipConstants.HEADER_ALLOW_EVENTS, EventTypeWriter
                        .write(sipRequest.getAllowedEventTypes()));
            }

            if (!sipRequest.getCallerInfo().isEmpty()) {
                headers.add(SipConstants.HEADER_CALL_INFO,
                        AddressWriter.write(sipRequest.getCallerInfo()));
            }

            if (!sipRequest.getContacts().isEmpty()) {
                headers.add(SipConstants.HEADER_CONTACT,
                        ContactInfoWriter.write(sipRequest.getContacts()));
            }

            if (sipRequest.getEvent() != null) {
                headers.add(SipConstants.HEADER_EVENT,
                        EventWriter.write(sipRequest.getEvent()));
            }

            if (sipRequest.getMimeVersion() != null) {
                headers.add(SipConstants.HEADER_MIME_VERSION,
                        sipRequest.getMimeVersion());
            }

            if (sipRequest.getOrganization() != null) {
                headers.add(SipConstants.HEADER_ORGANIZATION,
                        sipRequest.getOrganization());
            }

            if (!sipRequest.getRecordedRoutes().isEmpty()) {
                headers.add(SipConstants.HEADER_RECORD_ROUTE,
                        AddressWriter.write(sipRequest.getRecordedRoutes()));
            }

            if (sipRequest.getReplyTo() != null) {
                headers.add(SipConstants.HEADER_REPLY_TO,
                        AddressWriter.write(sipRequest.getReplyTo()));
            }

            if (!sipRequest.getSupported().isEmpty()) {
                headers.add(SipConstants.HEADER_SUPPORTED,
                        OptionTagWriter.write(sipRequest.getSupported()));
            }

            if (!sipRequest.getInReplyTo().isEmpty()) {
                StringBuilder sb = new StringBuilder(sipRequest.getInReplyTo()
                        .get(0));

                for (int i = 1; i < sipRequest.getInReplyTo().size(); i++) {
                    sb.append(",").append(sipRequest.getInReplyTo().get(i));
                }

                headers.add(SipConstants.HEADER_IN_REPLY_TO, sb.toString());
            }

            if (sipRequest.getPriority() != null) {
                headers.add(SipConstants.HEADER_PRIORITY, sipRequest
                        .getPriority().getValue());
            }

            if (!sipRequest.getProxyRequires().isEmpty()) {
                headers.add(SipConstants.HEADER_PROXY_REQUIRE,
                        OptionTagWriter.write(sipRequest.getProxyRequires()));
            }

            if (sipRequest.getReferTo() != null) {
                headers.add(SipConstants.HEADER_REFER_TO,
                        AddressWriter.write(sipRequest.getReferTo()));
            }

            if (!sipRequest.getRequires().isEmpty()) {
                headers.add(SipConstants.HEADER_REQUIRE,
                        OptionTagWriter.write(sipRequest.getProxyRequires()));
            }

            if (!sipRequest.getRoutes().isEmpty()) {
                headers.add(SipConstants.HEADER_ROUTE,
                        AddressWriter.write(sipRequest.getRoutes()));
            }

            if (sipRequest.getSipIfMatch() != null) {
                headers.add(HeaderConstants.HEADER_IF_MATCH,
                        TagWriter.write(sipRequest.getSipIfMatch()));
            }

            if (sipRequest.getSubject() != null) {
                headers.add(SipConstants.HEADER_SUBJECT,
                        sipRequest.getSubject());
            }

            if (sipRequest.getSubscriptionState() != null) {
                headers.add(SipConstants.HEADER_SUBSCRIPTION_STATE,
                        SubscriptionWriter.write(sipRequest
                                .getSubscriptionState()));
            }
        }

        super.addRequestHeaders(headers);
    }

    @Override
    protected void handle(Response response) {
        // TODO Auto-generated method stub

    }

}
