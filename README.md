# logl: A simple Java logging framework for libraries

[![Build Status](https://circleci.com/gh/ConsenSys/logl.svg?style=shield)](https://circleci.com/gh/ConsenSys/logl) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/ConsenSys/logl/blob/master/LICENSE)

Logl aims to provide a very simple logging API for use inside libraries and
other distributable components. It does not use global singletons,
XML configuration files, fancy classloader tricks, complex introspection, or
anything else that can create challenges when integrating libraries into
larger applications.

The API for logl is available by including the org.logl:api library (logl-api.jar).

For applications using libraries that depend on logl, there are bridges
available to route logging done through logl into your favourite application
logging framework. Logl also includes some very lightweight logging
implementations for simple use cases.


## Using logl in a library

Using logl in a library is quite straight foward: require an instance of
`org.logl.LoggerProvider` be supplied (at instantiation) to all library
objects that need to perform logging. If you wish to provide a default,
use `LoggerProvider.nullProvider()` (or `Logger.nullLogger()`). E.g.:

```java
public class MyService {

    private final Logger logger;

    MyService() {
        this.logger = Logger.nullLogger();
    }

    MyService(LoggerProvider loggerProvider) {
        this.logger = loggerProvider.getLogger(getClass());
    }
}
```


## Using a library that supports logl

When encountering a library that supports logl, your application can should
provide an appropriate instance of a `LoggerProvider` when instantiating
library objects. There are `LoggerProvider` instances available for all
major logging frameworks as well as some simple ones provided by `logl-logl`.

```java
public class MyApplication {

  public static void main(String[] args) {
    LoggerProvider logProvider = SimpleLogger.toPrintWriter(stderr);
    MyService service = new MyService(logProvider);
    // do something with the service
  }
}
```

See the [API documentation](https://consensys.github.io/logl/docs/java/latest/)
for details, specifically [SimpleLogger](https://consensys.github.io/logl/docs/java/latest/org/logl/logl/SimpleLogger.html),
[UnformattedLogger](https://consensys.github.io/logl/docs/java/latest/org/logl/logl/UnformattedLogger.html),
[JULLoggerProvider](https://consensys.github.io/logl/docs/java/latest/org/logl/jul/JULLoggerProvider.html),
[Log4j2LoggerProvider](https://consensys.github.io/logl/docs/java/latest/org/logl/log4j2/Log4j2LoggerProvider.html),
and [Slf4jLoggerProvider](https://consensys.github.io/logl/docs/java/latest/org/logl/slf4j/Slf4jLoggerProvider.html).


## Links

- [GitHub project](https://github.com/ConsenSys/logl)
- [Online documentation](https://consensys.github.io/logl/docs/java/latest/)
- [Issue tracker: Report a defect or feature request](https://github.com/ConsenSys/logl/issues/new)
- [StackOverflow: Ask "how-to" and "why-didn't-it-work" questions](https://stackoverflow.com/questions/ask?tags=logl+java)
