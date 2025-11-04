package io.dscope.samples.yaml.pip3a4;

import io.dscope.rosettanet.interchange.purchaseorderrequest.v02_05.PurchaseOrderRequestType;
import io.dscope.rosettanet.system.standarddocumentheader.v01_23.DocumentHeader;
import io.dscope.rosettanet.system.standarddocumentheader.v01_23.DocumentHeaderType;
import io.dscope.rosettanet.system.standarddocumentheader.v01_23.DocumentIdentification;
import io.dscope.rosettanet.system.standarddocumentheader.v01_23.DocumentIdentificationType;
import io.dscope.rosettanet.system.standarddocumentheader.v01_23.DocumentInformation;
import io.dscope.rosettanet.system.standarddocumentheader.v01_23.DocumentInformationType;

/**
 * Utility helper for extracting identifier values from unmarshalled RosettaNet payloads.
 */
public final class DocumentIdentifierHelper {
    /**
     * Public no-arg constructor required for Camel's {@code #class:} bean instantiation.
     */
    public DocumentIdentifierHelper() {
    }

    /**
     * Extracts the proprietary document identifier from the unmarshalled payload.
     *
     * @param body the current exchange body
     * @return the identifier when present, otherwise {@code null}
     */
    public String extractIdentifier(Object body) {
        if (!(body instanceof PurchaseOrderRequestType request)) {
            return null;
        }

        DocumentHeader headerElement = request.getDocumentHeader();
        if (headerElement == null) {
            return null;
        }

        DocumentHeaderType headerType = headerElement.getValue();
        if (headerType == null) {
            return null;
        }

        DocumentInformation informationElement = headerType.getDocumentInformation();
        if (informationElement == null) {
            return null;
        }

        DocumentInformationType informationType = informationElement.getValue();
        if (informationType == null) {
            return null;
        }

        DocumentIdentification identificationElement = informationType.getDocumentIdentification();
        if (identificationElement == null) {
            return null;
        }

        DocumentIdentificationType identificationType = identificationElement.getValue();
        if (identificationType == null) {
            return null;
        }

        return identificationType.getIdentifier();
    }
}
