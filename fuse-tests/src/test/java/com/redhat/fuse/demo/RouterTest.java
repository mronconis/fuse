package com.redhat.fuse.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

/**
 * The @ActiveProfiles is a annotation that is used to activate profiles while loading ApplicationContext in Spring integration test.
 */
@ActiveProfiles("test")
/**
 * The @DirtiesContext is a annotation that is used on the test methods to force Spring Testing to automatically reload the CamelContext after each test method.
 */
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
/**
 * The @SpringBootTest is a annotation that tells Spring Boot to look for a main configuration class (one with @SpringBootApplication, for instance) 
 * and use that to start a Spring application context.
 */
@SpringBootTest
/**
 * The @CamelSpringBootRunner annotation bring the functionality of CamelSpringTestSupport to Spring Boot Test based test cases. 
 */
@RunWith(CamelSpringBootRunner.class)
public class RouterTest {

    @Autowired
    public CamelContext camelContext;

    @Produce(uri="direct:foo")
    private ProducerTemplate from;

    @EndpointInject(uri="mock:bar")
    private MockEndpoint mock;

    @Before
    public void attachTestProbes() throws Exception {
        camelContext.getRouteDefinition("foo").adviceWith(camelContext, new AdviceWithRouteBuilder(){
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("direct:bar")
                    .to("mock:bar");
                
                // endpoint was mocked
                assertNotNull(camelContext.hasEndpoint("mock:bar"));
            }
        });
    }

    @Test
    public void testSendBody() throws InterruptedException{
        from.sendBody("Hello World!");

        mock.expectedMessageCount(1);
        mock.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                return exchange.getIn().getBody(String.class).equals("Bye World!");
            }
        });
        mock.assertIsSatisfied();
    }

    @Test
    public void testSendBodyAndHeader() throws Exception {
        from.sendBodyAndHeader("Hello World!", "foo", "bar");

        mock.expectedMessageCount(1);
        mock.expectedHeaderReceived("foo", "bar");
        mock.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                return exchange.getIn().getBody(String.class).equals("Bye World!");
            }
        });
        mock.assertIsSatisfied();
    }
}
