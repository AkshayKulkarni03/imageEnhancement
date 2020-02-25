# Image-Enhancement
Baisc image enhancements using [OpenCV](https://opencv-java-tutorials.readthedocs.io/en/latest/) and [IJ](https://github.com/imagej/imagej1/).

## Requirements
For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally
There are several ways to run a this module on your local machine. one way is to execute the `main` method in `com.example.image.enahance.poc.enhance.image.ImageEnhance` while passing arguments to method
1. image file with full path
2. true or false (to perform the face detection in the image).

Alternatively you Maven plugin and run jar from command prompt/terminal like so:

```shell
mvn package
```
u can find fat jar of application run the jar file from command line
```shell
java -jar imag
```
