# Data4Help
In this folder all the code for Data4Help is included.

## How to work on the back-end?
The following are the steps needed to work on the back-end:

1. You will need [JDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html) >= 8
1. Install [Apache Maven](https://maven.apache.org/what-is-maven.html)
1. Install [MongoDB](https://docs.mongodb.com/manual/installation/) and [Redis](https://redis.io/download)
1. Open a terminal and from this folder, run: `mvn compile`
1. *[Optional]* To run the service:
    1. Make sure that MongoDB and Redis services are running.
    1. Execute the following line: `mvn -X exec:java -Dexec.mainClass=avila.schiatti.virdi.Main -e`. You will be able to access the site/services by accessing to http://127.1.1.1:4567
    1. In order to be able to have the front end, you should follow the steps of the front-end section, without running it.

### How to run the test cases?
*To be done*

## How to work on the front-end?
In order to work on the front-end, follow this steps:

1. Install [Nodejs & NPM](https://nodejs.org/es/download/)
1. Install [AngularCLI](https://cli.angular.io/) by running the following line: `npm install -g @angular/cli`
1. Go to `src/main/resources` folder and install the package dependencies running: `npm install`
1. Build the project by running `ng build`.
1. To run the front-end, you can run: `ng serve`. You will be able to access the front-end by accessing to http://127.1.1.1:4200 (*You won't have access to the back-end services, so probably the front-end alone is not useful*)

### How to run the test cases?
*To be done*