# Spring boot

## Build
mvn clean install -s configuration/settings.xml

## Run
mvn spring-boot:run -s configuration/settings.xml



# Kubernetes

## Build
mvn clean install -Pkubernetes -s configuration/settings.xml

## Push image
mvn k8s:push -Pkubernetes -s configuration/settings.xml

## Deploy
mvn k8s:deploy -Pkubernetes -s configuration/settings.xml

## Undeploy
mvn k8s:undeploy -Pkubernetes -s configuration/settings.xml



# Openshift

## Build
mvn clean install -Popenshift -s configuration/settings.xml

## Deploy
mvn oc:deploy -Popenshift -s configuration/settings.xml


## Undeploy
mvn oc:undeploy -Popenshift -s configuration/settings.xml