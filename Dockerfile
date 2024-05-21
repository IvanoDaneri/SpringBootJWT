FROM eclipse/ubuntu_jdk8:latest

# Setup JAVA_HOME, this is useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME
# Set working image folder
WORKDIR /app
# Copy files from specified path to image
COPY target/springBootRest.jar ./
COPY docker/start_app.sh ./
# Use <root> user to run a command in image console
USER root
# Create a folder app/log (-p does not throw any error if folder already exists)
RUN mkdir -p /log
# This is the first command to run after container is started
CMD /app/start_app.sh