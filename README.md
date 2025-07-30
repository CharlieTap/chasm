<br>

<img src="chasm.svg" width="240" alt="chasm logo"/>


![badge][badge-android]
![badge][badge-jvm]
![badge][badge-ios]
![badge][badge-linux]
![badge][badge-mac]
![badge][badge-windows]

---

Chasm is a WebAssembly runtime built on Kotlin Multiplatform.

The runtime targets the latest wasm [specification](https://webassembly.github.io/spec/core/index.html) and supports all instructions with the exception of VectorInstructions.

Additionally, the runtime supports the following proposals

- [x] Tail Call
- [x] Extended Constant Expressions
- [x] Typed Function References
- [x] Wasm GC
- [x] Multiple Memories
- [x] Exception Handling
- [x] Extended Name Sections

# Setup

```kotlin
dependencies {
    implementation("io.github.charlietap.chasm:chasm:0.9.81")
}
```

# Usage with the Gradle plugin

Chasm provides a Gradle plugin that can generate a typesafe Kotlin
interface from your WebAssembly module. When you apply the plugin to a
Kotlin JVM, Kotlin Multiplatform or Android module the build will read
your `.wasm` binary and produce a class and interface pair with Kotlin functions
for each function marked as exported in your wasm binary.

To use the plugin you need to:

* Add the plugin to your build script.
* Declare your wasm module(s) in the `chasm` extension.
* Add the appropriate chasm runtime dependency (`chasm‑jvm` on JVM and
  Android).

A minimal example using the plugin looks like this:

```kotlin
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.github.charlietap.chasm.gradle") version "0.2.2"
}

chasm {
    modules {
        create("ExampleService") {
            // Path to the wasm binary to generate an interface for
            binary = layout.projectDirectory.file("src/main/resources/example.wasm")
            // Package for the generated class
            packageName = "com.example"
        }
    }
}

dependencies {
    implementation("io.github.charlietap.chasm:chasm-jvm:0.9.81")
}
```

With this configuration a Kotlin class called `ExampleService` will be
generated in the specified package. Each exported function in
`example.wasm` will be exposed as a Kotlin function. The plugin runs on JVM,
Kotlin Multiplatform and Android module.

For more information please see the plugin documentation [here](./docs/plugin.md) and for
a working example please see the example project in `example/` for Android, JVM and
Multiplatform demonstrations of the plugin.

# Usage without the Gradle plugin

If you prefer to work with the low‑level API directly, chasm also
exposes a set of functions for decoding, instantiating and invoking
wasm modules. The following examples demonstrate how to use these
APIs without the plugin.

### Invoking functions

WebAssembly compilations output a [Module](chasm/src/commonMain/kotlin/io/gi
thub/charlietap/chasm/embedding/shapes/Module.kt) encoded as either a
`.wasm` or `.wat` file (currently only `.wasm` binaries are supported):

```kotlin
val wasmFileAsByteArray = ...
val module = module(wasmFileAsByteArray)
```

Once a module has been decoded you'll need to instantiate it, and for
that you'll also need a store:

```kotlin
val store = store()
val instance = instance(store, module)
```

Instances allow you to invoke functions that are exported from the
module:

```kotlin
val result = invoke(store, instance, "fibonacci")
```

### Imports

Modules often depend on [imports](chasm/src/commonMain/kotlin/io/github/char
lietap/chasm/embedding/shapes/Import.kt). Imports can be one of the
following:

- Functions
- Globals
- Memories
- Tables
- Tags

For the most part imports will actually be exports from other modules;
this is the mechanism that wasm uses to share data/behaviour between
modules. However you can also allocate them separately using the
factory functions:

```kotlin
val function = function(store, functionType, hostFunction)
val global = global(store, globalType, initialValue)
val memory = memory(store, memoryType)
val table = table(store, tableType, initialValue)
val tag = tag(store, tagType)
```

Once you have your importables you can pass them in at instantiation
time:

```kotlin
val import = Import(
    "import module name",
    "import entity name",
    function,
)
val instance = instance(store, module, listOf(import))
```

### Host functions

Host functions are Kotlin functions that can be called by wasm programs
at runtime. The majority of host functions represent system calls (for
example WASI APIs).

Allocation of a host function requires a [FunctionType](chasm/src/commonMain
/kotlin/io/github/charlietap/chasm/embedding/shapes/FunctionType.kt). The
function type describes the inputs and outputs of your function so the
runtime can call it and use its results. Once you have defined a
function type you can allocate the host function like so:

```kotlin
val functionType = FunctionType(emptyList(), listOf(ValueType.Number.I32))
val hostFunction = HostFunction { params ->
    println("Hello world")
    listOf(Value.Number.I32(117))
}

val result = function(store, functionType, hostFunction)
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
