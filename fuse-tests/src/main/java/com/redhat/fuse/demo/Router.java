package com.redhat.fuse.demo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ImportResource;

/**
 *  Camel route builder to setup the Camel routes.
 * 
 */
@Component
@ImportResource({"classpath:spring/camel-context.xml"})
public class Router extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("direct:foo")
            .routeId("foo")
            .log("${body}")
            .transform(constant("Bye World!"))
            .to("direct:bar");

        from("direct:bar")
            .routeId("bar")
            .log("${body}");

    }
}