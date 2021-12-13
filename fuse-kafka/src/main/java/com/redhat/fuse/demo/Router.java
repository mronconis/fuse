package com.redhat.fuse.demo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import static java.lang.String.format;

@Component
@ImportResource({"classpath:spring/camel-context.xml"})
public class Router extends RouteBuilder {

    public final static String ROOT_ID_CONSUMER = "consumer";
    public final static String ROOT_ID_PRODUCER = "producer";

    public final static String CONSUMER_TOPIC = "foo";
    public final static String PRODUCER_TOPIC = "bar";

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

    /**
     * If you send a single message, the batching adds a bit of overhead. But with two messages or more per batch,
     * the batching saves space which reduces network and disk usage.
     *
     * https://camel.apache.org/components/3.17.x/kafka-component.html#_component_option_lingerMs
     */
    public final static int PRODUCER_LINGER_MS = 10;

    /**
     * https://camel.apache.org/components/3.17.x/kafka-component.html#_endpoint_query_option_enableIdempotence
     */
    public final static boolean PRODUCER_ENABLE_IDEMPOTENCE = true;

    /**
     * https://camel.apache.org/components/3.17.x/kafka-component.html#_component_option_requestRequiredAcks
     */
    public final static String PRODUCER_REQUEST_REQUIRED_ACKS = "all";

    /**
     * https://camel.apache.org/components/3.17.x/kafka-component.html#_endpoint_query_option_retries
     */
    public final static int PRODUCER_RETRY = 3;

    /**
     *
     * https://camel.apache.org/components/3.17.x/kafka-component.html#_component_option_compressionCodec
     */
    public final static String PRODUCER_COMPRESSION_CODEC = "snappy";


    @Override
    public void configure() throws Exception {

        String fromKafka = format("kafka:%s?autoCommitEnable=%b&allowManualCommit=%b&breakOnFirstError=%b",
                CONSUMER_TOPIC,
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
                .process(ex -> ex.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class).commitSync())
                .to("log:complete");

        String toKafka = format("kafka:%s?lingerMs=%o&enableIdempotence=%b&requestRequiredAcks=%s&retries=%o&compressionCodec=%s",
                PRODUCER_TOPIC,
                PRODUCER_LINGER_MS,
                PRODUCER_ENABLE_IDEMPOTENCE,
                PRODUCER_REQUEST_REQUIRED_ACKS,
                PRODUCER_RETRY,
                PRODUCER_COMPRESSION_CODEC);

        from("direct:produce")
                .routeId(ROOT_ID_PRODUCER)
                .to(toKafka)
                .log("Message sent to Kafka : ${body}")
                .to("log:complete");
    }
}
