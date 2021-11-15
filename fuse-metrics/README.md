

# Kubernetes

## Build
mvn clean install -Pkubernetes -s configuration/settings.xml

## Push image
mvn k8s:push -Pkubernetes -s configuration/settings.xml

## Deploy
mvn k8s:deploy -Pkubernetes -s configuration/settings.xml


# Openshift

## Build
mvn clean install -Popenshift -s configuration/settings.xml

## Deploy
mvn oc:deploy -Popenshift -s configuration/settings.xml