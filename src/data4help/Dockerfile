FROM maven:3.6-jdk-8

# Install nodejs
RUN apt-get update
RUN apt-get install -y curl

RUN curl -sL https://deb.nodesource.com/setup_11.x | bash -
RUN apt-get install -y nodejs
RUN npm --version

WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /code/src
ADD configuration /code/configuration

# Compile front-end
WORKDIR /code/src/main/resources
RUN npm install -g @angular/cli
RUN ng build

# Package the code
WORKDIR /code
RUN ["mvn", "package"]

EXPOSE 4567
CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "target/data4help-jar-with-dependencies.jar"]
