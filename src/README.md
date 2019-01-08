# TrackMe project
The TrackMe project is composed by 3 different systems: Data4Help, AutomatedSOS, and Track4Run.
We decided to develop only Data4Help, which is the leading system, and AutomatedSOS.

## How to run the systems?
There are few steps to run both systems.

1. Install [Docker](https://docs.docker.com/install/) and [Docker Compose](https://docs.docker.com/compose/install/)
1. In a terminal and from this folder run: `sudo docker-compose up` (with `--build` to rebuild the image)
1. Open the browser and you can access to the Data4Help site using the following URL: `http://0.0.0.0:4200`

## How to run the tests image?

1. Install Docker and Docker Compose
1. In a terminal and from this folder, run: `sudo docker-compose -f docker-compose.tests.yml up --build --abort-on-container-exit --exit-code-from d4h_test`
1. This will run all the unit test cases and will exit. If the exit code is `0` and you see `Tests run: XX, Failures: 0, Errors: 0, Skipped: 0`, everything is ok.
