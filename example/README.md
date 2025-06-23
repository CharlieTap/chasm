# Chasm example application

---

This example project demonstrates integrating chasm with a barebones Android application, this project exists to both demonstrate
functionality but also serve of an integration test for chasm and its plugin. The project features a few different modules:

- android

- consumer-android-fibonacci
- consumer-jvm-test
- consumer-multiplatform-factorial

### android 🤖

This is really primitive Android application that loads "services" from the di graph, each service is a different web assembly
binary which gets injected by one of the consumer modules.

### consumer-android-fibonacci 🧮

An android library module which creates and injects a FibonacciService, this service wraps a wasm binary that exposes a tail recursive
fibonacci implementation

### consumer-jvm-test 🧮

A kotlin jvm module which creates and injects a TestService, this service wraps a wasm binary that exposes a variety of
different wasm exports. It demonstrates functions with multiple returns, string returns, immutable and mutable globals etc.

### consumer-multiplatform-factorial 🧮

A kotlin multiplatform module which creates and injects a FactorialService, this service wraps a wasm binary which that exposes a
wasm implementation of the factorial function.



