# Ahead-of-time compilation in Chasm

`chasm-class-compiler` compiles a WebAssembly module to JVM classes, usually at
build time. `runtime-compiled` runs those classes, on the JVM or on Android,
with the same embedding API as the interpreter: `instance` and `invoke` are
unchanged, and only the module and the store are created differently.

```kotlin
dependencies {
    implementation("io.github.charlietap.chasm:runtime-compiled:3.0.0")
}
```

`chasm-class-compiler` (`io.github.charlietap.chasm:chasm-class-compiler`) is only
needed where modules are compiled, such as a build step; an app that runs
modules compiled at build time needs only `runtime-compiled`. The same runtime
also loads DEX compiled by `chasm-dex-compiler`; see
[Loading DEX from files](../README.md#loading-dex-from-files).

## Compiling a module

```kotlin
val config = CompileConfig(
    className = "com.example.parser.ParserModule",
    target = CompileTarget.HOTSPOT,
    runtime = RuntimeConfig(
        stacktraces = true,
        limits = Limits(
            cpu = CpuLimits.Metered(10_000_000.fuel),
            memory = MemoryLimits(linearMemory = 64.MB, stack = 8.MB),
        ),
    ),
)

val compiled = compile(Path("src/main/wasm/parser.wasm"), ClassOutput.jar(Path("build/chasm/parser.jar")), config)
    .expect("parser compiles")
```

`compile` is a suspending function. It takes the module as a `Path` or a
`ByteArray`, an output and a `CompileConfig`:

- `className` is the binary name of the entry class, the class the app refers
  to. It must be a Java class name and may not contain `$`.
- `target` is `CompileTarget.HOTSPOT` (the default) or `CompileTarget.ART`.
  Each target sizes methods against the limit of the VM that runs them; see
  [Method size](#method-size). A module runs only on the target it was
  compiled for.
- `runtime` is the `RuntimeConfig` the module runs with; see
  [Settings](#settings).

It returns a `ChasmResult`. On success, the `CompiledModuleInfo` describes
what was compiled: the config, the module's imports and exports, the runtime
ABI and compiler version, a SHA-256 hash of the input, every class and
resource written, the shrinker rules and, with stack traces, where the trap
diagnostics live. It exists only as the return value; nothing in the output
repeats it. On failure, the `CompileError` names the stage that failed
(`CONFIGURATION`, `INPUT`, `DECODE`, `VALIDATION`, `GENERATION` or `OUTPUT`)
and, where it can, the functions that caused it.

## Running a compiled module

The entry class exposes itself as `COMPILED`:

```kotlin
val module = module(ParserModule.COMPILED).expect("parser loads")
val store = store(module)
val instance = instance(store, module, imports).expect("parser instantiates")
val result = invoke(instance, "parse", listOf(NumberValue.I32(42)))
```

## Settings

Every setting is fixed when the module is compiled: stack traces, interrupts
and the [resource limits](./limits.md), fuel included. `store(module)` makes a
store with the settings the module was compiled with, so a metered module's
store already holds the fuel it was compiled with. There is no way to change
them when the module is loaded.

A module whose memory or table starts above its limits fails to compile with a
`CONFIGURATION` error, rather than failing to instantiate later. A module with
a 64-bit memory or table fails to compile with a `GENERATION` error.

A store can instantiate other compiled modules, for example to link them, only
when their fuel, interrupts and every limit match the store's. Otherwise
instantiation fails with `Unlinkable`, naming the first setting that differs.
Stack traces need not match: each module reports traps with its own setting.

Unset and set values are different settings: a store with an unset `stack`
does not accept a module compiled with an explicit `stack`, even when it
equals the platform default.

Structs, arrays and exceptions are ordinary JVM or ART objects; see
[compiled modules in the GC documentation](./gc.md#in-compiled-modules).

## Outputs

`ClassOutput` is where a compilation writes. Chasm provides three:

- `ClassOutput.jar(path)` writes one jar.
- `ClassOutput.directory(path)` writes class files and resources under a
  directory, such as the classes directory of a build.
- `ClassOutput.memory()` keeps the bytes in memory, in `classFiles` and
  `resources`, for tests and for tools that handle the bytes themselves.

These three only write once a compilation succeeds. A failed compilation
leaves a jar, a directory or a memory output as it was before, except that a
directory output may keep subdirectories it created, empty. Each can be
reused for further compilations: a directory or memory output adds to what it
holds, while a jar output replaces the jar.

A jar holds at most 65,534 entries, so that it never needs the Zip64 format,
which Android's build cannot read. A module that needs more fails with an
`OUTPUT` error; write it with `ClassOutput.directory(...)` instead.

A build tool can pass its own destination by implementing `ClassOutput`.
`classFile` receives each class by its binary name, such as
`com.example.parser.ParserModule$Code0`, and `resource` receives each resource
by its path. Classes and resources arrive as they are generated, and there is
no call when the compilation ends: a failure is reported only by the returned
error, and what was already received is the implementation's to discard. Only
the three outputs above are written on success, and only when they are passed
to `compile` directly; one wrapped inside your own `ClassOutput` is never
written.

### What a compilation writes

Everything is named after the entry class, so modules compiled into the same
package do not collide. For `com.example.parser.ParserModule` with stack
traces:

```text
com/example/parser/ParserModule.class
com/example/parser/ParserModule$Code0.class
com/example/parser/ParserModule.chasmdata
com/example/parser/ParserModule.sites
META-INF/proguard/com.example.parser.ParserModule.pro
```

Every other generated class is nested under the entry class with `$`. The
`.chasmdata` resource holds the module's data segments and is only written when
the module has some. The `.sites` resource and the shrinker rules, which keep
the code a stack trace needs, are only written with stack traces; the rules are
also returned as `CompiledModuleInfo.shrinkerRules`.

## Method size

There is no setting. On `HOTSPOT`, a method closes at 8,000 bytes of bytecode,
above which HotSpot never JIT-compiles it. On `ART`, a method closes at
5,150 estimated DEX code units, which keeps it under the 10,000 units ART
compiles after D8 or R8.

A function too large for one method is split into pieces, called through a
dispatcher method. On `ART` the dispatcher is held to the same limit as
every other method, and a function whose dispatcher would pass it fails to
compile with a `GENERATION` error. On `HOTSPOT` the dispatcher only has to stay
under 32,767 bytes.

## Stack traces after shrinking

R8 renumbers the lines of the methods it keeps, which the `.sites` resource
relies on. After R8 has run, `retraceTrapLines(mapping)` reads its mapping file
and returns a table for each module compiled with stack traces, keyed by the
path the app's assets must carry it at, the same path as
`CompiledModuleInfo.diagnostics.retracedLines`.

## Loading compiled classes at runtime

A server that runs WebAssembly it only sees at runtime can compile it into its
own local cache and load it from there with `ClassInput`:

```kotlin
val jar = cache.resolve("recipes-v35.jar")
compile(appWasm, ClassOutput.jar(jar), CompileConfig(className = "apps.Recipes", runtime = runtimeConfig)).expect("compiles")

val module = module(ClassInput.jar(jar), "apps.Recipes").expect("loads")
val store = store(module)
```

`ClassInput.jar(path)`, `ClassInput.directory(path)` and
`ClassInput.memory(classFiles, resources)` read the classes and their
resources from the same jar, directory or maps. Chasm builds the class loader
for them, and `module()` checks what it loaded:

- The entry class is named exactly `CompileConfig.className`, implements
  `CompiledModule`, and exposes `public static final COMPILED`.
- Every class the module was compiled with comes from the input, and none is
  missing. An entry class that a class on the runtime's own class path already
  defines is refused.
- The module was compiled for this runtime and this VM, and every resource it
  needs is present.

Any of these failing is a `ModuleError.Malformed` naming the class, path or
resource. Data segments are memory-mapped from a directory input and read as
a stream from a jar input. `ClassInput.memory` copies its maps
and arrays, so later changes to them do not reach the module.

Unloading needs nothing from the caller: dropping the module, its stores and
instances lets the loader and its classes be collected. Chasm keeps no global
references to loaded modules.

`module(loader, className)` loads the entry class from a class loader the
caller built instead. Chasm then checks nothing about where the classes come
from, and reads resources through the entry class's own loader.
