# Ahead-of-time compilation in Chasm

`chasm-aot` compiles a WebAssembly module to JVM classes, usually at build
time. `runtime-aot` runs those classes, on the JVM or on Android, with the same
embedding API as the other engines: `instance` and `invoke` are unchanged, and
only the module and the store are created differently.

```kotlin
dependencies {
    implementation("io.github.charlietap.chasm:runtime-aot:3.0.0")
}
```

`chasm-aot` (`io.github.charlietap.chasm:chasm-aot`) is only needed where
modules are compiled, such as a build step; an app that runs modules compiled
at build time needs only `runtime-aot`.

## Compiling a module

```kotlin
val config = CompileConfig(
    className = "com.example.parser.ParserModule",
    target = CompileTarget.JVM,
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

`compile` takes the module as a `Path` or a `ByteArray`, an output and a
`CompileConfig`:

- `className` is the binary name of the entry class, the class the app refers
  to. It must be a Java class name and may not contain `$`.
- `target` is `CompileTarget.JVM` (the default) or `CompileTarget.ANDROID`.
  Each target sizes methods against the limit of the VM that runs them; see
  [Method size](#method-size).
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
`CONFIGURATION` error, rather than failing to instantiate later.

A store can instantiate other compiled modules, for example to link them, only
when their fuel, interrupts and every limit match the store's. Otherwise
instantiation fails with `Unlinkable`, naming the first setting that differs.
Stack traces need not match: each module reports traps with its own setting.

Unset and set values are different settings: a store with an unset `stack`
does not accept a module compiled with an explicit `stack`, even when it
equals the platform default.

The GC heap follows the [fixed collection policy](./gc.md#collection) of the
interpreter.

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

There is no setting. On `JVM`, a method closes at 8,000 bytes of bytecode,
above which HotSpot never JIT-compiles it. On `ANDROID`, a method closes at
5,150 estimated DEX code units, which keeps it under the 10,000 units ART
compiles after D8 or R8.

A function too large for one method is split into pieces, called through a
dispatcher method. On `ANDROID` the dispatcher is held to the same limit as
every other method, and a function whose dispatcher would pass it fails to
compile with a `GENERATION` error. On `JVM` the dispatcher only has to stay
under 32,767 bytes.

## Stack traces after shrinking

R8 renumbers the lines of the methods it keeps, which the `.sites` resource
relies on. After R8 has run, `retraceTrapLines(mapping)` reads its mapping file
and returns a table for each module compiled with stack traces, keyed by the
path the app's assets must carry it at, the same path as
`CompiledModuleInfo.diagnostics.retracedLines`.

## Loading compiled classes at runtime

A server that runs WebAssembly it only sees at runtime can compile it into its
own local cache and load the entry class with a plain `URLClassLoader`. Chasm
needs no load API for this:

```kotlin
val jar = cache.resolve("recipes-v35.jar")
compile(appWasm, ClassOutput.jar(jar), CompileConfig(className = "apps.Recipes", runtime = runtimeConfig)).expect("compiles")

val loader = URLClassLoader(arrayOf(jar.toUri().toURL()), CompiledModule::class.java.classLoader)
val compiled = loader.loadClass("apps.Recipes").getField("COMPILED").get(null) as CompiledModule
val module = module(compiled).expect("loads")
val store = store(module)

// once the module is no longer needed: drop the store and instances, then
loader.close()
```

The artifact's contract, which Chasm documents and keeps stable:

- The entry class is named exactly `CompileConfig.className`, implements
  `CompiledModule`, and exposes `public static final COMPILED`.
- It reads its resources (data segments, trap sites) through its own class
  loader. Data segments are memory-mapped when that loader serves the
  `.chasmdata` resource from a file on local disk: a `file:` URL, as from a
  classes directory, or a `jar:file:` URL to an uncompressed (stored) entry of a
  jar that is not nested in another archive and needs no Zip64 records, under
  2 GiB. A `URLClassLoader` on a jar written by `ClassOutput.jar` serves such
  URLs, since that output stores `.chasmdata` uncompressed. Otherwise the data
  segments are read as a stream.
- The parent loader must see `runtime-aot`, so `CompiledModule` is the same
  class on both sides.
- Unloading is the consumer's. It owns the loader: dropping the module, store
  and instances and closing the loader lets the classes be collected. Chasm
  keeps no global references to loaded modules.

The one reflective line is the consumer's; `chasm-aot` and `runtime-aot` use
no reflection to load a compiled module.
