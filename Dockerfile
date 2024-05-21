FROM eclipse/ubuntu_jdk8:latest

# Setup JAVA_HOME, this is useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME
# Set working folder
WORKDIR /app
# Copy files from current path to image
COPY target/springBootRest.jar ./
COPY docker/start_app.sh ./
# Use <root> user to run a command in image console
USER root
# Create a folder log
RUN mkdir -p /log
# This is the command to run when container is starting
CMD /app/start_app.sh