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
