package org.fhirvp.ports.impl.fhir.context;

import com.ibm.fhir.client.FHIRClient;
import com.ibm.fhir.client.FHIRClientFactory;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Properties;

public class FHIRClientProducer {
    @Inject
    FHIRClientConfig config;

    @Produces
    public FHIRClient getInstance() throws Exception {
        var clientProperties = new Properties();
        clientProperties.setProperty(FHIRClient.PROPNAME_BASE_URL, config.rest().base().url());
        clientProperties.setProperty(FHIRClient.PROPNAME_BASIC_AUTH_ENABLED, config.basicauth().enabled());
        clientProperties.setProperty(FHIRClient.PROPNAME_CLIENT_USERNAME, config.basicauth().username());
        clientProperties.setProperty(FHIRClient.PROPNAME_CLIENT_PASSWORD, config.basicauth().password());
        clientProperties.setProperty(FHIRClient.PROPNAME_LOGGING_ENABLED, config.logging().enabled());

        return FHIRClientFactory.getClient(clientProperties);
    }
}
