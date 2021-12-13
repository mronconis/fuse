package com.redhat.fuse.demo;

import org.apache.camel.CamelContext;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.component.micrometer.messagehistory.MicrometerMessageHistoryFactory;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.prometheus.PrometheusMeterRegistry;

@Configuration
public class Config {

    @Value("${camel.springboot.name}")
    String appName;

    /**
     * This is useful for declaring common tags (dimensions) for the metrics data that would be collected by Micrometer. 
     * 
     * @return MeterRegistryCustomizer<PrometheusMeterRegistry>
     */
    @Bean
    MeterRegistryCustomizer<PrometheusMeterRegistry> configureMetricsRegistry() {
        return registry -> registry.config().commonTags("appName", appName);
    }

    /**
     * Set the Camel Context to use the MicrometerRouterPolicyFactory, MicrometerMessageHistoryFactory and MetricsRoutePolicyFactory.
     * 
     * Here are the metrics made available to Prometheus:
     * - CamelMessageHistory_seconds_count
     * - CamelMessageHistory_seconds_max
     * - CamelRoutePolicy_seconds_max
     * - CamelRoutePolicy_seconds_count
     * - CamelRoutePolicy_seconds_sum
     * 
     * @return
     */
    @Bean
    public CamelContextConfiguration camelContextConfiguration() {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {
                // This factory allows to add a RoutePolicy for each route which exposes route utilization statistics using codehale metrics.
                camelContext.addRoutePolicyFactory(new MetricsRoutePolicyFactory());
                camelContext.addRoutePolicyFactory(new MicrometerRoutePolicyFactory());
                camelContext.setMessageHistoryFactory(new MicrometerMessageHistoryFactory());
            }
            @Override
            public void afterApplicationStart(CamelContext camelContext) {

            }
        };
    }
}
