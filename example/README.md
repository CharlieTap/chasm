# Chasm example application

---

This example project demonstrates integrating chasm with a barebones Android application, this project exists to both demonstrate
functionality but also serve of an integration test for chasm and its plugin. The project features a few different modules:

- android

- consumer-android
- consumer-jvm
- consumer-multiplatform

### android 🤖

This is really primitive Android application that loads "services" from the di graph, each service is a different web assembly
binary which gets injected by one of the consumer modules.

### consumer-android 🧮

An android library module which creates and injects a FibonacciService, this service wraps a wasm binary that exposes a tail recursive
fibonacci implementation

### consumer-jvm 🧮

A kotlin jvm module which creates and injects a TestService, this service wraps a wasm binary that exposes a variety of
different wasm exports. It demonstrates functions with multiple returns, string returns, immutable and mutable globals etc.

### consumer-multiplatform 🧮

A kotlin multiplatform module which creates and injects services that can be used by Android, JS, and WasmJS consumers. It wraps wasm
binaries for factorial, string encoding, and interop coverage across numeric values, multiple returns, strings, and globals.


