FROM maven:3.6-jdk-8

# Install nodejs
#RUN apt-get update
#RUN apt-get install -y curl
#
#RUN curl -sL https://deb.nodesource.com/setup_11.x | bash -
#RUN apt-get install -y nodejs
#RUN npm --version

WORKDIR /code

RUN curl https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh --output wait-for-it.sh
RUN chmod +x wait-for-it.sh

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /code/src
ADD configuration /code/configuration
ADD migration /code/migration

# Compile front-end
#WORKDIR /code/src/main/resources
#RUN npm install -g @angular/cli
#RUN npm install
#RUN ng build

# Package the code
WORKDIR /code
RUN ["mvn", "-Dmaven.test.skip=true", "package"]

EXPOSE 4567
#EXPOSE 4200
# RUN ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "target/data4help-jar-with-dependencies.jar"]
