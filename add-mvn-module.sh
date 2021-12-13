
#export FUSE_ARCHETYPE_VERSION=2.2.0.fuse-sb2-780040-redhat-00002
export FUSE_ARCHETYPE_VERSION=2.2.0.fuse-sb2-790047-redhat-00004

mvn org.apache.maven.plugins:maven-archetype-plugin:2.4:generate \
  -DarchetypeCatalog=https://maven.repository.redhat.com/ga/io/fabric8/archetypes/archetypes-catalog/${FUSE_ARCHETYPE_VERSION}/archetypes-catalog-${FUSE_ARCHETYPE_VERSION}-archetype-catalog.xml
