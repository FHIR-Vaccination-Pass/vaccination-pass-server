package org.fhirvp.ports.impl.fhir;

import com.ibm.fhir.client.FHIRClient;
import com.ibm.fhir.client.FHIRClientFactory;
import lombok.Getter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Properties;

@ApplicationScoped
public class FHIRClientProvider {
    @Getter
    FHIRClient fhirClient;

    @Inject
    FHIRClientProvider(FHIRClientConfig config) throws Exception {
        var clientProperties = new Properties();
        clientProperties.setProperty(FHIRClient.PROPNAME_BASE_URL, config.rest().base().url());
        clientProperties.setProperty(FHIRClient.PROPNAME_BASIC_AUTH_ENABLED, config.basicauth().enabled());
        clientProperties.setProperty(FHIRClient.PROPNAME_CLIENT_USERNAME, config.basicauth().username());
        clientProperties.setProperty(FHIRClient.PROPNAME_CLIENT_PASSWORD, config.basicauth().password());
        clientProperties.setProperty(FHIRClient.PROPNAME_LOGGING_ENABLED, config.logging().enabled());

        this.fhirClient = FHIRClientFactory.getClient(clientProperties);
    }
}
