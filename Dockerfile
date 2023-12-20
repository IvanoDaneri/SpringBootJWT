FROM eclipse/ubuntu_jdk8:latest

# Setup JAVA_HOME, this is useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME

WORKDIR /app

COPY target/springBootRest.jar ./
COPY docker/start_app.sh ./
USER root
RUN mkdir -p /log

CMD /app/start_app.sh