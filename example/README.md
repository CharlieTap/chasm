# Chasm example application

---

This example project demonstrates integrating chasm with a barebones Android application, the project features two modules:

- android
- fibonacci-wasm

### android 🤖

This is really primitive Android application that loads a wasm file from assets, decoding the wasm file into a module and then
instantiating it. The user is able to configure arguments and invoke functions on the wasm module using the user interface.

### fibonacci-wasm 🧮

This is a kotlin multiplatform module with one target: wasm-wasi

The project exports the `fibonacci` function to be bundled in the wasm module generated.

The code specifically avoids making syscalls to avoid generating a wasm file that needs WASI imports,
as chasm does not support WASI yet out of the box.

However, it is possible using chasms import api to provide
WASI imports if you wish to include you're own WASI function implementations.

Running the gradle task:

```shell
compileProductionExecutableKotlinWasmWasi
```

will result in a wasm file being generated under the path:

`./example/fibonacci-wasm/build/compileSync/wasmWasi/main/productionExecutable/kotlin/chasm-example-fibonacci-wasm-wasm-wasi.wasm`


It is this file that is copied into the android application under the path:

`example/android/src/main/assets/chasm-example-fibonacci-wasm-wasm-wasi.wasm`
