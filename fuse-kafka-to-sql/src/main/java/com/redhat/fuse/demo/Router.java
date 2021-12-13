package com.redhat.fuse.demo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import static java.lang.String.format;

@Component
public class Router extends RouteBuilder {

    public final static String ROOT_ID_CONSUMER = "consumer";

    /**
     * https://camel.apache.org/components/3.17.x/kafka-component.html#_component_option_autoCommitEnable
     */
    public final static boolean CONSUMER_AUTO_COMMIT_ENABLE = false;

    /**
     * https://camel.apache.org/components/3.17.x/kafka-component.html#_endpoint_query_option_breakOnFirstError
     */
    public final static boolean CONSUMER_ALLOW_MANUAL_COMMIT = true;

    /**
     * https://camel.apache.org/components/3.17.x/kafka-component.html#_endpoint_query_option_allowManualCommit
     */
    public final static boolean CONSUMER_BREAK_ON_FIRST_ERROR = false;

    @Value("${input.topic}")
    private String topic;

    @Override
    public void configure() throws Exception {

        String fromKafka = format("kafka:%s?autoCommitEnable=%b&allowManualCommit=%b&breakOnFirstError=%b",
                topic,
                CONSUMER_AUTO_COMMIT_ENABLE,
                CONSUMER_ALLOW_MANUAL_COMMIT,
                CONSUMER_BREAK_ON_FIRST_ERROR);

        from(fromKafka)
                .routeId(ROOT_ID_CONSUMER)
                .log("Message received from Kafka : ${body}")
                .log("    on the topic ${headers[kafka.TOPIC]}")
                .log("    on the partition ${headers[kafka.PARTITION]}")
                .log("    with the offset ${headers[kafka.OFFSET]}")
                .log("    with the key ${headers[kafka.KEY]}")
                .to("sql: INSERT INTO Test(Uuid) values (:#${body})")
                .process(ex -> ex.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class).commitSync())
                .to("micrometer:counter:sample.insert.counter?increment=1")
                .to("log:complete");
    }
}
