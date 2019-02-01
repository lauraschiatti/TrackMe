FROM maven:3.6-jdk-8-alpine

WORKDIR /code

# installing curl and mongodb tools
RUN apk add --update curl mongodb-tools bash && rm -rf /var/cache/apk/*

RUN curl https://raw.githubusercontent.com/eficode/wait-for/master/wait-for --output wait-for-it.sh
RUN chmod +x wait-for-it.sh

# Adding source, compile and package into a fat jar
ADD src /code/src
ADD dbbackup /code/dbbackup
ADD restore_db_and_start.sh /code/restore_db_and_start.sh

ADD pom.xml /code/pom.xml
# Prepare by downloading dependencies
RUN ["mvn", "dependency:resolve"]

# Package the code
RUN ["mvn", "-Dmaven.test.skip=true", "package"]

EXPOSE 5678
