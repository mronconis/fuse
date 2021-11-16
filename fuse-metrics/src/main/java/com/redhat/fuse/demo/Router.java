package com.redhat.fuse.demo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class Router extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        rest()
            .get("/demo/{client-id}")
            //.to("micrometer:timer:route_timer" + "?" + "action=start" + "&" + "routeName=dummy")
            //.to("micrometer:counter:route_counter" + "?" + "routeName=dummy")
            .to("direct:main");
            //.to("micrometer:timer:route_timer" + "?" + "action=stop" + "&" + "routeName=dummy");

        from("direct:main")
            .routeId("dummy")
            .log("got request from client: ${header.client-id}");
    }
}