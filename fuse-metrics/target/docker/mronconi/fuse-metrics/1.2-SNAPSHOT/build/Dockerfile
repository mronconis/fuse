FROM quay.io/jkube/jkube-java-binary-s2i:0.0.10
ENV JAVA_APP_DIR=/deployments
LABEL org.label-schema.description="Fuse metrics demo" org.label-schema.version=1.2-SNAPSHOT org.label-schema.schema-version=1.0 org.label-schema.build-date=2021-11-15T22:39:08.100576 org.label-schema.name="Fuse metrics demo" org.label-schema.vcs-ref=32c3abdf0a5f1a3d6b955f61aa425a358585590d org.label-schema.vcs-url=git@github.com:mronconis/fuse.git
EXPOSE 8080 8778 9779
COPY /jkube-generated-layer-original/deployments /deployments/
