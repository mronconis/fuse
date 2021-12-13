

# Documentation

WIP... Spring boot 2 and JUnit4

Maven users will need to add the following dependency to their pom.xml:
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.camel</groupId>
            <artifactId>camel-test-spring</artifactId>
            <scope>test</scope>
        </dependency>
```


# Spring boot

## Build
```
mvn clean install -s ../configuration/settings.xml
```

## Run
```
mvn spring-boot:run -s ../configuration/settings.xml
```


# Kubernetes

## Build
```
mvn clean install -Pkubernetes -s ../configuration/settings.xml
```

## Push image
```
mvn k8s:push -Pkubernetes -s ../configuration/settings.xml
```

## Deploy
```
mvn k8s:deploy -Pkubernetes -s ../configuration/settings.xml
```

## Undeploy
```
mvn k8s:undeploy -Pkubernetes -s ../configuration/settings.xml
```


# Openshift

## Build
```
mvn clean install -Popenshift -s ../configuration/settings.xml
```

## Deploy
```
mvn oc:deploy -Popenshift -s ../configuration/settings.xml
```

## Undeploy
```
mvn oc:undeploy -Popenshift -s ../configuration/settings.xml
```


# Test
```
mvn test -s ../configuration/settings.xml
```