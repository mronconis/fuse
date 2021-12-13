package com.redhat.fuse.demo;

import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;

import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

@ActiveProfiles("test")
/**
 * Notice that we use @DirtiesContext on the test methods to force Spring Testing to automatically reload the CamelContext after each test method.
 */
@DirtiesContext
@SpringBootTest
@RunWith(CamelSpringBootRunner.class)
public class RouterTest {

    final static Logger LOG = LoggerFactory.getLogger(RouterTest.class);

    int redeliveryAttempt = 0, maxRedeliveryAttempt = 3, maxFailureAttempt = 3;

    @Autowired
    public CamelContext camelContext;

    @Produce(uri="direct:foo")
    private ProducerTemplate from;

    @EndpointInject(uri="mock:completed")
    private MockEndpoint mockCompleted;

    @EndpointInject(uri="mock:irrecoverableError")
    private MockEndpoint mockIrrecoverableError;

    @Before
    public void attachTestProbes() throws Exception {
        camelContext.getRouteDefinition("onIrrecoverableError").adviceWith(camelContext, new AdviceWithRouteBuilder(){
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("log:irrecoverableError").to("mock:irrecoverableError");
            }
        });
        camelContext.getRouteDefinition("foo").adviceWith(camelContext, new AdviceWithRouteBuilder(){
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("direct:bar")
                        .process(new Processor(){
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                if (++redeliveryAttempt <= maxFailureAttempt) {
                                    if (redeliveryAttempt == 1)
                                        LOG.info("Throw mocked IOException");
                                    else
                                        LOG.info("Throw mocked IOException (redelivery={})", redeliveryAttempt-1);
                                    throw new IOException("Mock IOException");
                                }
                            }
                        });
                interceptSendToEndpoint("log:completed").to("mock:completed");
            }
        });
    }

    @Test
    public void testSuccess() throws Exception {

        from.sendBody("Hello world!");

        mockCompleted.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                assertEquals(maxRedeliveryAttempt, redeliveryAttempt-1);
            }
        });

        //mockCompleted.expectedBodyReceived();

        MockEndpoint.assertIsSatisfied(mockCompleted);
    }

    @Test
    public void testFailure() throws Exception {
        maxFailureAttempt = maxRedeliveryAttempt + 1;

        from.sendBody("Hello world!");

        mockIrrecoverableError.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                assertEquals(maxFailureAttempt, redeliveryAttempt);
            }
        });

        //mockIrrecoverableError.expectedMinimumMessageCount(1);

        MockEndpoint.assertIsSatisfied(mockIrrecoverableError);
    }
}
