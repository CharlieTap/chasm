# Changelog

## 3.0.0

### Migrating compiled modules to 3.0

Chasm's compiled tiers are now split by what they produce, not by when they
compile. Three artifacts replace `chasm-aot`, `runtime-aot` and `chasm-jit`:

| 3.0 artifact | Replaces | Does |
| --- | --- | --- |
| `chasm-jvm-compiler` | `chasm-aot` | compiles a module to JVM classes, at build time or on a server |
| `chasm-dex-compiler` | the compiler inside `chasm-jit` | compiles a module to DEX, on a server, in a build or on the device |
| `runtime-compiled` | `runtime-aot` and the runtime inside `chasm-jit` | loads and runs either, on the JVM (classes) or on Android (classes and DEX) |

Ahead of time or just in time is now only a question of when you call a
compiler. An app that runs modules compiled elsewhere needs `runtime-compiled`
alone; the compilers depend on no runtime.

`runtime-compiled` also contains what used to be published separately as
`embedding-compiled` and `compiled`: the zero-copy functions such as
`unsafeBorrowBytes` and `unsafeAllocateArray` keep their packages and now come
from `runtime-compiled`. `module(...)` for decoded modules, previously in
`embedding-module`, is part of `chasm`, in the same package. Remove those
three dependencies if you declared them.

#### Packages

| Before | After |
| --- | --- |
| `io.github.charlietap.chasm.aot.compile`, `CompileConfig`, `CompileTarget`, `ClassOutput`, `CompiledModuleInfo`, `CompileError`, `retraceTrapLines` | `io.github.charlietap.chasm.jvm` |
| `io.github.charlietap.chasm.aot.module`, `store`, `CompiledModule` | `io.github.charlietap.chasm.compiled` |
| `io.github.charlietap.chasm.jit.store` | gone; see [JIT users](#jit-users) |
| (new) DEX compilation: `compile`, `CompileConfig`, `DexOutput` | `io.github.charlietap.chasm.dex` |
| (new) loading from files: `ClassInput` (JVM), `DexInput` (Android) | `io.github.charlietap.chasm.compiled` |

#### Changes for every compiled module

- `CompileTarget.JVM` is now `CompileTarget.HOTSPOT` and
  `CompileTarget.ANDROID` is now `CompileTarget.ART`. A module runs only on the
  target it was compiled for; `module()` refuses one compiled for the other
  with `ModuleError.Malformed`.
- Both compilers' `compile` functions are `suspend`. `chasm-jvm-compiler`
  runs the work on `Dispatchers.Default` and can be cancelled while it runs;
  `chasm-dex-compiler` also runs its work on `Dispatchers.Default`, so it is
  safe to call from Android's main thread. Callers outside a coroutine can
  wrap the call in `runBlocking`.
- Both compilers refuse a module with a 64-bit memory or table when they
  compile it, with a `GENERATION` error, "memory64 is unsupported". `chasm-aot`
  compiled such a module and it failed at `instance()`.
- Classes compiled by `chasm-aot` do not load in `runtime-compiled`: compile
  them again with `chasm-jvm-compiler`.

#### JIT users

`chasm-jit` compiled a module every time it was instantiated, with the
settings of the store. Compiling is now a separate, explicit step:

- Settings move from `store(config)` to `CompileConfig.runtime`. Every setting
  is fixed when the module is compiled.
- `store(module)` replaces `store(config)`: the store takes its settings from
  the module.
- A module decoded with `module(bytes)` from `chasm` runs only on the
  interpreter; a compiled store accepts only modules that `runtime-compiled`
  loaded.

Before, on the device:

```kotlin
// io.github.charlietap.chasm:chasm-jit
val store = io.github.charlietap.chasm.jit.store(RuntimeConfig(stacktraces = true))
val module = module(wasmBytes).expect("decodes")
val instance = instance(store, module, imports).expect("instantiates")
```

After, compiling on the device into memory:

```kotlin
// io.github.charlietap.chasm:chasm-dex-compiler and io.github.charlietap.chasm:runtime-compiled
val config = CompileConfig(className = "app.Guest", runtime = RuntimeConfig(stacktraces = true))
val output = DexOutput.memory()
compile(wasmBytes, output, config).expect("compiles")

val module = module(DexInput.memory(output.dexFiles, output.resources), config.className).expect("loads")
val store = store(module)
val instance = instance(store, module, imports).expect("instantiates")
```

ART neither optimises nor caches DEX loaded from memory. To let it, compile
into a directory or jar once and load from there on later runs; see
[DEX from files](#dex-from-files).

#### Classpath loading

Before, with `chasm-aot` at build time and `runtime-aot` in the app:

```kotlin
val config = io.github.charlietap.chasm.aot.CompileConfig(className = "com.example.ParserModule", target = CompileTarget.ANDROID)
compile(wasm, ClassOutput.directory(classesDirectory), config).expect("compiles")

val module = io.github.charlietap.chasm.aot.module(ParserModule.COMPILED).expect("loads")
val store = io.github.charlietap.chasm.aot.store(module)
```

After, with `chasm-jvm-compiler` at build time and `runtime-compiled` in the
app:

```kotlin
val config = io.github.charlietap.chasm.jvm.CompileConfig(className = "com.example.ParserModule", target = CompileTarget.ART)
compile(wasm, ClassOutput.directory(classesDirectory), config).expect("compiles")

val module = io.github.charlietap.chasm.compiled.module(ParserModule.COMPILED).expect("loads")
val store = io.github.charlietap.chasm.compiled.store(module)
```

#### Server-side jar loading

Before, the server built the class loader itself:

```kotlin
compile(appWasm, ClassOutput.jar(jar), CompileConfig(className = "apps.Recipes", runtime = runtimeConfig)).expect("compiles")

val loader = URLClassLoader(arrayOf(jar.toUri().toURL()), CompiledModule::class.java.classLoader)
val compiled = loader.loadClass("apps.Recipes").getField("COMPILED").get(null) as CompiledModule
val module = module(compiled).expect("loads")
val store = store(module)
loader.close()
```

After, `ClassInput` builds the loader, reads the resources from the same jar
and checks that every generated class came from it:

```kotlin
compile(appWasm, ClassOutput.jar(jar), CompileConfig(className = "apps.Recipes", runtime = runtimeConfig)).expect("compiles")

val module = module(ClassInput.jar(jar), "apps.Recipes").expect("loads")
val store = store(module)
```

There is nothing to close: the loader is collected with the module, its stores
and instances. `module(loader, className)` remains for class loaders you build
yourself.

#### DEX from files

`DexOutput.directory`, `DexOutput.jar`, `DexInput.directory` and
`DexInput.jar` hold exactly one module each, because Android only loads DEX
files named `classes.dex`, `classes2.dex`, ... and those names cannot say
which module a file belongs to:

- `DexOutput.directory` refuses a directory that is not empty. Compile a new
  version into a new directory, for example one named after the Wasm module's
  hash and the compile settings, and delete the old one only once no module,
  store or instance loaded from it is still in use.
- `DexOutput.jar` writes a temporary jar and renames it over the old one.
- Android 14 and later load DEX only from read-only files. Both outputs make
  every file read-only; DEX you download or copy yourself must be made
  read-only before `DexInput` loads it, or `module()` fails with
  `ModuleError.Malformed`.

The README's [Loading DEX from files](./README.md#loading-dex-from-files)
section has a caching example.

#### Generated bindings

`tooling-bindgen`'s `ExecutionTier.Jit` and `ExecutionTier.Aot(compiledModule)`
are replaced by one `ExecutionTier.Compiled(module)`, where `module` is
`CompiledSource.Generated(className)` for a module compiled into the same
build, or `CompiledSource.Loaded()` for bindings whose factory takes a module
the app has already loaded with `runtime-compiled`.
