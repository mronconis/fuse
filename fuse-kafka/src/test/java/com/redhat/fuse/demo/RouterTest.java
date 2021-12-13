package com.redhat.fuse.demo;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.*;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext
@MockEndpoints("mock:complete")
@RunWith(CamelSpringBootRunner.class)
@ContextConfiguration(initializers = RouterTest.PropertiesOverrideContextInitializer.class)
public class RouterTest {

    @ClassRule
    public static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka")).withExposedPorts(9092, 9093);

    @Autowired
    public CamelContext camelContext;

    @EndpointInject(uri="mock:complete")
    private MockEndpoint mockComplete;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Before
    public void attachTestProbes() throws Exception {
        camelContext.getRouteDefinition(Router.ROOT_ID_CONSUMER).adviceWith(camelContext, new AdviceWithRouteBuilder(){
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("log:complete").to("mock:complete");
            }
        });
        camelContext.getRouteDefinition(Router.ROOT_ID_PRODUCER).adviceWith(camelContext, new AdviceWithRouteBuilder(){
            @Override
            public void configure() throws Exception {
                interceptSendToEndpoint("log:complete").to("mock:complete");
            }
        });
    }

    @Test
    public void testConsumer() throws InterruptedException, IOException {
        mockComplete.expectedMinimumMessageCount(3);

        KafkaProducer producer = getKafkaProducer();

        producer.send(new ProducerRecord<String, String>(Router.CONSUMER_TOPIC, "Sample 1"));
        producer.send(new ProducerRecord<String, String>(Router.CONSUMER_TOPIC, "Sample 2"));
        producer.send(new ProducerRecord<String, String>(Router.CONSUMER_TOPIC, "Sample 3"));

        mockComplete.assertIsSatisfied();
    }

    @Test
    public void testProducer() throws InterruptedException, IOException {
        mockComplete.expectedMinimumMessageCount(3);

        producerTemplate.sendBody("direct:produce", "Sample 1");
        producerTemplate.sendBody("direct:produce", "Sample 2");
        producerTemplate.sendBody("direct:produce", "Sample 3");

        mockComplete.assertIsSatisfied();
    }

    private KafkaProducer<String, String> getKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public static class PropertiesOverrideContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                configurableApplicationContext,
                "camel.component.kafka.configuration.brokers=" + kafka.getBootstrapServers()
            );
        }
    }
}
