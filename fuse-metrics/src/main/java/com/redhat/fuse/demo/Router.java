package com.redhat.fuse.demo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 *  Camel route builder to setup the Camel routes.
 * 
 */
@Component
public class Router extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        rest()
            .get("/demo/{client-id}")
            .to("direct:main");

        from("direct:main")
            .routeId("dummy")
            .to("micrometer:counter:simple.counter?increment=1")
            .log("got request from client: ${header.client-id}");

    }
}