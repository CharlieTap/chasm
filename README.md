<br>

<img src="chasm.svg" width="240" alt="chasm logo"/>


![badge][badge-android]
![badge][badge-jvm]
![badge][badge-ios]
![badge][badge-linux]
![badge][badge-mac]
![badge][badge-windows]

---

Chasm is an experimental WebAssembly runtime built on Kotlin Multiplatform.

The runtime targets the latest wasm [specification](https://webassembly.github.io/spec/core/index.html) and supports all instructions with the exception of VectorInstructions.

Additionally, the runtime supports the following Stage 4 proposals

- [x] Tail Call
- [x] Extended Constant Expressions
- [x] Typed Function References
- [ ] Wasm GC (In progress)

# Setup

```kotlin
dependencies {
    implementation("io.github.charlietap.chasm:chasm:0.1.1")
}
```

# Usage

### Invoking functions

Webassembly compilations output a [Module](./ast/src/commonMain/kotlin/io/github/charlietap/chasm/ast/module/Module.kt)
encoded as either a .wasm or .wat file, currently chasm supports decoding only .wasm binaries

```kotlin
val wasmFileAsByteArray = ...
val result = module(wasmFileAsByteArray)
```

Once a module has been decoded you'll need to instantiate it, and for that you'll need also need a store


```kotlin
val store = store()
val result = instance(store, module)
```

Instances allow you to invoke functions that are exported from the module

```kotlin
val result = invoke(store, instance, "fibonacci")
```

### Imports

Modules often depend on imports, which come in two flavours, either:

- Host functions
- Exported functions from other wasm modules

Both are represented by [ExternalValue](executor/runtime/src/commonMain/kotlin/io/github/charlietap/chasm/executor/runtime/instance/ExternalValue.kt)'s and can be imported at instantiation time

```kotlin
val import = Import(
    "import module name",
    "import entity name",
    externalValue,
)
val result = instance(store, module, listOf(import))
```

### Host functions

Host functions are kotlin functions that can be called by wasm programs at runtime.
The majority of host functions represent system calls, WASI for example is intended to be integrated as imports of host functions.

Allocation of a host function requires a [FunctionType](ast/src/commonMain/kotlin/io/github/charlietap/chasm/ast/type/FunctionType.kt)

The function type describes the inputs and outputs of your function so the runtime can call it and use its results

Once you have function type you can allocate the host function like so

```kotlin
val functionType = FunctionType(ResultType(emptyList()), ResultType(emptyList()))
val hostFunction: HostFunction = {
    println("Hello world")
    emptyList()
}

val result = function(store, funcType, hostFunction)
```

## License

This project is dual-licensed under both the MIT and Apache 2.0 licenses. You can choose which one you want to use the software under.

- For details on the MIT license, please see the [LICENSE-MIT](LICENSE-MIT) file.
- For details on the Apache 2.0 license, please see the [LICENSE-APACHE](LICENSE-APACHE) file.

[badge-android]: http://img.shields.io/badge/-android-7DBC39.svg?style=flat
[badge-jvm]: http://img.shields.io/badge/-jvm-9DA993.svg?style=flat
[badge-linux]: http://img.shields.io/badge/-linux-DBB98C.svg?style=flat
[badge-ios]: http://img.shields.io/badge/-ios-E3E8E9.svg?style=flat
[badge-mac]: http://img.shields.io/badge/-macos-AFA189.svg?style=flat
[badge-windows]: http://img.shields.io/badge/-windows-9C7350.svg?style=flat
