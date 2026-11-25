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

The runtime targets the latest wasm specification [Wasm 3.0](https://webassembly.github.io/spec/core/index.html) and supports all instructions with the exception of Memory64 and Vector Instructions.

Additionally, the runtime supports the following proposals

| Proposal                      | Supported |
|-------------------------------|-----------|
| Tail Call                     | ✅        |
| Extended Constant Expressions | ✅        |
| Typed Function References     | ✅        |
| Wasm GC                       | ✅        |
| Multiple Memories             | ✅        |
| Exception Handling            | ✅        |
| Extended Name Sections        | ✅        |
| Wide Arithmetic               | ✅        |


# Usage with the Gradle plugin

Chasm provides a Gradle plugin that can generate a typesafe Kotlin
interface from your WebAssembly module. When you apply the plugin to a
Kotlin JVM, Kotlin Multiplatform or Android module the build will read
your `.wasm` binary and produce a class and interface pair with Kotlin functions
for each function marked as exported in your wasm binary.

To use the plugin you need to:

* Add the plugin to your build script.
* Declare your wasm module(s) in the `chasm` extension.

A minimal example using the plugin looks like this:

```kotlin
plugins {
    ...
    id("io.github.charlietap.chasm.gradle") version "3.2.0-2.5.0"
}

chasm {
    modules {
        create("ExampleService") {
            binary = layout.projectDirectory.file("src/main/resources/example.wasm")
            packageName = "com.example"
        }
    }
}
```

With this configuration a Kotlin class called `ExampleService` will be
generated in the specified package. Each exported function in
`example.wasm` will be exposed as a Kotlin function. The gradle plugin abstracts over a
generic wasm virtual machine which uses Chasm for JVM and native targets whilst leveraging
the embedded wasm vm (V8, Spidermonkey, JavascriptCore etc) on JS targets.

For more information please see the plugin documentation [here](./docs/plugin.md) and for
a working example please see the example project in `example/` for Android, Web, JVM and
Multiplatform demonstrations of the plugin.

# Usage without the Gradle plugin

If you prefer to work with the low‑level API directly, chasm also
exposes a set of functions for decoding, instantiating and invoking
wasm modules. The following examples demonstrate how to use these
APIs without the plugin.

```kotlin
dependencies {
    implementation("io.github.charlietap.chasm:chasm:2.5.0")
}
```

### Invoking functions

WebAssembly compilations output a [Module](embedding-api/src/commonMain/kotlin/io/github/charlietap/chasm/embedding/shapes/Module.kt) encoded as either a
`.wasm` or `.wat` file (currently only `.wasm` binaries are supported):

```kotlin
val wasmFileAsByteArray = ...
val module = module(wasmFileAsByteArray).expect("Failed to decode module")
```

Decoding a module, validating it and instantiating it are suspending
functions; callers outside a coroutine can wrap them in `runBlocking`.
Executing functions and every accessor remain ordinary functions.

`module` validates the binary as it decodes it. If the binary comes from a
source you trust you can skip validation and validate the module later:

```kotlin
val unvalidated = module(wasmFileAsByteArray, validate = false).expect("Failed to decode module")
val validated = validate(unvalidated)
```

Once a module has been decoded you'll need to instantiate it, and for
that you'll also need a store:

```kotlin
val store = store()
val instance = instance(store, module).expect("Failed to instantiate module")
```

Instances allow you to invoke functions that are exported from the
module:

```kotlin
val result = invoke(instance, "fibonacci", listOf(NumberValue.I32(10)))
```

Preparing a function resolves the named export and its runtime function up
front, making invocation faster because it no longer has to resolve the
function. This is useful when either:

- the same function will be called repeatedly, so the preparation cost is
  amortized across those calls; or
- the function is known ahead of time and can be prepared during setup or
  instantiation, moving resolution work out of a later invocation.

Prepare the function once and retain the returned handle:

```kotlin
val fibonacci = prepareFunction(instance, "fibonacci")
    .expect("Failed to prepare fibonacci")

val result = fibonacci(listOf(NumberValue.I32(10)))
```

Prepared functions remain tied to the store and instance used to create them.
Invoking one after either has been dropped returns an execution error.
Ordinary `invoke` remains useful when the export is selected dynamically or
there is no useful opportunity to prepare it ahead of invocation.

### Engines

Chasm runs modules on two engines: an interpreter, and a runtime for
compiled modules, `runtime-compiled`, on the JVM and Android. Modules are
compiled to JVM classes by `chasm-jvm-compiler` or to DEX by
`chasm-dex-compiler`, ahead of time or on the device. Every call above works
on both engines; only the store and, for compiled modules, the module are
created differently:

```kotlin
// interpreter: io.github.charlietap.chasm:chasm
val store = io.github.charlietap.chasm.embedding.store()
val module = module(wasmFileAsByteArray).expect("Failed to decode module")

// compiled: io.github.charlietap.chasm:runtime-compiled, with the module compiled at build time
val module = io.github.charlietap.chasm.compiled.module(ExampleModule.COMPILED).expect("Failed to load module")
val store = io.github.charlietap.chasm.compiled.store(module)
```

A store runs modules on one engine only, so a module made for one engine
cannot be instantiated in a store of another.

Every engine takes the same `RuntimeConfig`: stack traces, interrupts and
the [resource limits](./docs/limits.md) a store enforces. The interpreter
takes it when the store is created, `store(RuntimeConfig(...))`.
A compiled module takes it at compile time, so its store is made from the
module. The interpreter keeps WebAssembly GC objects in Chasm's own
[collected heap](./docs/gc.md), while compiled modules allocate them as
ordinary JVM objects, so some limits behave differently there. Compiling
modules ahead of time, and the contract the compiled classes keep, are
described in [Ahead-of-time compilation](./docs/aot.md). Moving from
`chasm-aot` or `chasm-jit`? See
[Migrating compiled modules to 3.0](./CHANGELOG.md#migrating-compiled-modules-to-30).

### Loading DEX from files

`chasm-dex-compiler` compiles a module to DEX and `runtime-compiled` loads
it on the device with `module(DexInput.directory(path), className)` or
`module(DexInput.jar(path), className)`. Files let ART verify, optimise and
cache the code like any other secondary DEX; `DexInput.memory` code never is.

Android only loads DEX files named `classes.dex`, `classes2.dex`, ..., so
the names cannot say which module they belong to. One directory or jar
therefore holds exactly one module from one compile:

- `DexOutput.directory(path)` writes only into a missing or empty
  directory and fails at stage `OUTPUT` otherwise. A directory that kept an
  older compile could hand its `classes3.dex` to a newer module, and
  clearing it first could delete files a loaded module still uses.
- `DexOutput.jar(path)` writes a temporary jar and renames it over `path`
  once the compile succeeds.
- Every file is made read-only, as Android 14 and later require.

To replace a module, compile it into a new directory (or a new jar, or
rename a new jar over the old one), load it from there, and delete the old
directory only once no module, store or instance loaded from it is still in
use. A directory named after the Wasm module and its compile settings works
as a cache:

```kotlin
val directory = cache.resolve("$wasmHash-$settingsName-$appVersion")
if (!Files.isDirectory(directory)) {
    val staging = Files.createTempDirectory(cache, "compiling")
    compile(wasm, DexOutput.directory(staging), config).expect("compiles")
    Files.move(staging, directory, StandardCopyOption.ATOMIC_MOVE)
}
val module = module(DexInput.directory(directory), config.className).expect("loads")
```

DEX compiled somewhere else, such as on a server, loads the same way once it
is on the device. On Android 14 and later `DexInput.directory` and
`DexInput.jar` refuse a writable DEX file with `ModuleError.Malformed`, so
make each downloaded or copied file read-only (`File.setReadOnly()`) before
loading it, and download into a new directory or a temporary jar that is
renamed into place rather than over files a loaded module still uses.
`DexInput.directory` loads `classes.dex`, `classes2.dex` and so on up to the
first missing number and ignores any other DEX file, so a gap in the numbering
leaves the files after it out and `module()` fails, naming a missing class.

### Imports

Modules often depend on [imports](embedding-api/src/commonMain/kotlin/io/github/charlietap/chasm/embedding/shapes/Importable.kt). Imports can be one of the
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
val instance = instance(store, module, listOf(import)).expect("Failed to instantiate module")
```

### Host functions

Host functions are Kotlin functions that can be called by wasm programs
at runtime. The majority of host functions represent system calls (for
example WASI APIs).

Allocation of a host function requires a [FunctionType](type-system/src/commonMain/kotlin/io/github/charlietap/chasm/type/FunctionType.kt). The
function type describes the inputs and outputs of your function so the
runtime can call it and use its results. Once you have defined a
function type you can allocate the host function like so:

```kotlin
val functionType = FunctionType(
    ResultType(emptyList()),
    ResultType(listOf(ValueType.Number(NumberType.I32))),
)
val hostFunction = HostFunction { _, results ->
    println("Hello world")
    results.writeI32(0, 117)
}

val function = function(store, functionType, hostFunction)
```

A host function receives the positions of its parameters and results rather
than lists of values. Numbers are read and written with `readI32`..`readF64`
and `writeI32`..`writeF64`, references with `readReference` and
`writeReference`. The memories, tables, globals and tags of the calling
instance are available inside the function, for example:

```kotlin
val log = HostFunction { parameters, _ ->
    val message = withMemory(0) {
        readUtf8String(parameters.readI32(0), parameters.readI32(1))
    }
    println(message)
}
```

# Working with modules that contain syscalls

Wasm is a specification of an abstract virtual machine, it does not define an operating system ... for that
there is a separate specification known as [WASI](https://wasi.dev/). Wasm instead defines a system for importing
functions and an operating system is that, a set of well-defined functions.

It works like this, WASI describes the functions and compilers can then produce wasm modules that expect function imports
that match those functions defined in the WASI spec. For example heres a module that uses WASI to get the time:

```wat
(module
  (import "wasi_snapshot_preview1" "clock_time_get" (func $clock_time_get (param i32 i64 i32) (result i32)))

  (memory (export "memory") 1)

  (func (export "get_time")
    i32.const 0
    i64.const 0
    i32.const 0
    call $clock_time_get
    drop
  )
)
```

WASI is an evolving spec (far more nascent than WASM) and we have the following support:

- WASI Preview 1

Full support through the library [WASI Emscripten Host](https://github.com/illarionov/wasi-emscripten-host)

- WASI Preview 2/3

No support as it requires a Phase 1 Wasm proposal called the [Component Model](https://github.com/WebAssembly/component-model)

## Additional Reading

- [Interfacing with Wasm from Kotlin](https://dev.to/charlietap/interfacing-with-wasm-from-kotlin-2k2c)

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
