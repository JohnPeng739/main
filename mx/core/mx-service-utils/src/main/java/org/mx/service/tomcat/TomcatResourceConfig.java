package org.mx.service.tomcat;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.mx.service.rest.ServerStatisticResource;

public class TomcatResourceConfig extends ResourceConfig {
    public TomcatResourceConfig() {
        super();
        super.register(MultiPartFeature.class);
        super.register(ServerStatisticResource.class);
    }
}
