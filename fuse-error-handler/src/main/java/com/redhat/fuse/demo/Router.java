package com.redhat.fuse.demo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;

/**
 * Camel route builder to setup the Camel routes.
 */
@Component
@ImportResource({"classpath:spring/camel-context.xml"})
public class Router extends RouteBuilder {

    // redelivery settings
    private final static int MAXIMUM_REDELIVERIES = 3;
    private final static int BACK_OFF_MULTIPLIER = 2;
    private final static int REDELIVERY_DELAY = 5000;

    @Override
    public void configure() throws Exception {

        errorHandler();

        from("direct:foo")
                .routeId("foo")
                .log("Received: ${body}")
                .to("direct:bar");

        from("direct:bar")
                .routeId("bar")
                .to("log:completed");
    }

    private void errorHandler() {

        // handle recoverable error
        onException(IOException.class).continued(true).useOriginalMessage()
                .asyncDelayedRedelivery()
                .maximumRedeliveries(MAXIMUM_REDELIVERIES)
                .backOffMultiplier(BACK_OFF_MULTIPLIER)
                .redeliveryDelay(REDELIVERY_DELAY)
                .process(extractExcption("error_msg", "error_stack_trace"))
                .log("Unable to recovery error after (" + MAXIMUM_REDELIVERIES + ") attempt");

        // handle irrecoverable error
        errorHandler(deadLetterChannel("direct:deadLetter").useOriginalMessage().maximumRedeliveries(2).useExponentialBackOff());

        from("direct:deadLetter")
                .routeId("onIrrecoverableError")
                .process(extractExcption("error_msg", "error_stack_trace"))
                .log("Irrecoverable error occurred: ${headers.error_msg}")
                .to("log:irrecoverableError");
    }

    public static final Processor extractExcption(String headerMessage, String headerStackTrace) {

        return exchange -> {

            // check if already was set the exception from onException()
            if (exchange.getIn().getHeader(headerMessage) == null) {

                Exception e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

                exchange.getIn().setHeader(headerMessage, e.getMessage());
                exchange.getIn().setHeader(headerStackTrace, e.getStackTrace().toString());
            }
        };
    }
}