package org.fhirvp.ports.impl.fhir.context;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "fhirclient")
interface FHIRClientConfig {
    interface Rest {
        interface Base {
            String url();
        }
        Base base();
    }
    Rest rest();

    interface Basicauth {
        String enabled();
        String username();
        String password();
    }
    Basicauth basicauth();

    interface Logging {
        String enabled();
    }
    Logging logging();
}
