

# Documentation
The **metrics**: component allows you to collect various metrics directly from Camel routes. Supported metric types are counter, summary, and timer. Metrics provides simple way to measure behaviour of your application. 

Maven users will need to add the following dependency to their pom.xml:
```
    <dependency>
        <groupId>org.apache.camel</groupId>
        <artifactId>camel-metrics-starter</artifactId>
    </dependency>
    <dependency>     
        <groupId>org.apache.camel</groupId>     
        <artifactId>camel-micrometer-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
```

# Spring boot

## Build
```
mvn clean install -s configuration/settings.xml
```

## Run
```
mvn spring-boot:run -s configuration/settings.xml
```

# Kubernetes

## Build
```
mvn clean install -Pkubernetes -s configuration/settings.xml
```

## Push image
```
mvn k8s:push -Pkubernetes -s configuration/settings.xml
```

## Deploy
```
mvn k8s:deploy -Pkubernetes -s configuration/settings.xml
```

## Undeploy
```
mvn k8s:undeploy -Pkubernetes -s configuration/settings.xml
```


# Openshift

## Build
```
mvn clean install -Popenshift -s configuration/settings.xml
```

## Deploy
```
mvn oc:deploy -Popenshift -s configuration/settings.xml
```

## Undeploy
```
mvn oc:undeploy -Popenshift -s configuration/settings.xml
```

# Test

## Endpoints

```
http://localhost:8080/camel/demo/user-1
```

# Metrics

## Endpoints

### All metrics

```
http://localhost:8081/actuator/metrics
```

### Custom metric

```
http://localhost:8081/actuator/metrics/simple.counter
```

Output
```
{
   "name":"simple.counter",
   "description":null,
   "baseUnit":null,
   "measurements":[
      {
         "statistic":"COUNT",
         "value":2
      }
   ],
   "availableTags":[
      {
         "tag":"appName",
         "values":[
            "fuse-metrics"
         ]
      },
      {
         "tag":"camelContext",
         "values":[
            "fuse-metrics"
         ]
      }
   ]
}
```

# Prometheus

## Endpoints

```
http://localhost:8081/actuator/prometheus
```

Output
```
...
# HELP simple_counter_total  
# TYPE simple_counter_total counter
simple_counter_total{appName="fuse-metrics",camelContext="fuse-metrics",} 2.0
...
```

## Enable service monitor

```
oc apply -f src/main/resources/prometheus/service-monitor.yaml
```

## Review the target status for your project

```
oc port-forward -n openshift-user-workload-monitoring pod/prometheus-user-workload-0 9090
```

Open http://localhost:9090/targets in a web browser and review the status of the target for your project directly in the Prometheus UI. Check for error messages relating to the target.
