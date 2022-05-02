No longer in use

#  SYFO emottak mock
Application that mocks the WS response from emottak

## Technologies used
* Kotlin
* Netty
* Gradle
* Spek


#### Requirements

* JDK 11

## Getting started
#### Running locally
`./gradlew run`


#### Compile and package application
To build locally and run the integration tests you can simply run `./gradlew shadowJar` or on windows 
`gradlew.bat shadowJar`

#### Creating a docker image
Creating a docker image should be as simple as `docker build -t syfosmemottakmock .`

#### Running a docker image
`docker run --rm -it -p 8080:8080 syfosmemottakmock`

## Contact us
### Code/project related questions can be sent to
* Joakim Kartveit, `joakim.kartveit@nav.no`
* Kevin Sillerud, `kevin.sillerud@nav.no`
* Anders Østby, `anders.ostby@nav.no`


### For NAV employees
We are available at the Slack channel #barken
