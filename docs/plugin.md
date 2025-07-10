# Chasm Gradle Plugin

The chasm gradle plugin creates a kotlin typesafe interface from a wasm binary which hides
the complexities of chasms lower level virtual machine api.

## Modes

The plugin has the notion of a mode, of which there exists two:

- **Consumers** (The default)
- **Producers**

Consumers **consume** wasm modules that are created externally, maybe by other
languages, toolchains or frameworks.

Producers are kotlin multiplatform modules which have the wasm target enabled, effectively they
**produce** wasm modules internally as part of their compilation.


## Consumer Configuration

For consumers, you'll need to take the externally produced wasm binary and place it inside the module
where you plan to generate the kotlin interface. Now where you place the binary, is really dependent
on how you're planning to use it at runtime. Some people use chasm to execute code from different languages
and simply bake the binary into the application. Others download the binary remotely so they can update
their applications on the fly. Others do a mixture of both.

If your intention is to bake the binary into modules resources/assets at all then it would be best to place
it in one of the following locations

- If you're using the kotlin.jvm plugin place it in `src/main/resources`
- If you're using the kotlin.multiplatform plugin place it in `src/commonMain/resources`
- If you're using any of the android plugins place it in `src/main/assets`, you can also place different binaries in variant directories
if you want to slightly different interfaces for different variants

If your intention is to only ever download a remote binary then place the binary in `src/main/wasm`, as this will prevent
it leaking into your compiled artifacts.

Here's an example of a configuration for a wasm binary will be baked into the module

```kotlin
chasm {
    modules {
        create("FibonacciService") {
            binary = layout.projectDirectory.file("src/main/assets/fibonacci.wasm")
            packageName = "com.test.chasm"
        }
    }
}
```

Given the above configuration the plugin will then read the binary found at `src/main/assets/fibonacci.wasm` and generate an
interface and implementation from its metadata, for example

```kotlin
package com.test.chasm

import kotlin.Int

public interface FibonacciService {
  public fun fibonacci(p0: Int): Int
}
```

## Codegen config

You can control the output of the codegen using the [CodegenConfig](../chasm-gradle-plugin/src/main/kotlin/io/github/charlietap/chasm/gradle/CodegenConfig.kt) object

```kotlin
chasm {
    modules {
        create("") {
            binary = ...
            packageName = ...
            codegenConfig = CodegenConfig(
                generateTypesafeGlobalProperties = true,
            )
        }
    }
}
```
### `generateTypesafeGlobalProperties`

default = false

When set chasm will generate properties for globals in the interface it generates, if the global is mutable then chasm will generate a var
else it will create a val.

For example the following wat

```wat
    (global $mutable_global (export "mutable_global") (mut i32) (i32.const 117))
    (global $immutable_global (export "immutable_global") i32 (i32.const 117))
```

will become

```kotlin
  var mutableGlobal: Int
  val immutableGlobal: Int
```

## Producer config

Producers are kotlin multiplatform modules that use the wasm target to turn kotlin code into wasm binaries which chasm can then generate
an interface for. You can find an example of a producer module in the [example project](../example/producer/build.gradle.kts)

Typical configuration for a producer will involve enabling the wasm target and other targets you would like to share
the code generated classes with. For example, say we want to integrate chasm codegen in an android application you would enable both
the wasm target and the jvm target

```kotlin
kotlin {
    jvm()
    wasmWasi {
        binaries.executable()
    }
}
```

You'll also need to add this config to ensure the wasm binaries created use the latest opcodes for exception handling, by default the
kotlin wasm compiler generates legacy opcodes.

```kotlin
tasks.withType<KotlinJsCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll("-Xwasm-use-new-exception-proposal")
    }
}
```

Then configure the chasm plugin:

```kotlin
chasm {
    mode = Mode.PRODUCER
    modules {
        create("FooService") {
            packageName = "com.foo.bar"
        }
    }
}
```

Now any dependant JVM or android modules will be able to see the classes code generated as part of the producer modules
compilation. Code generation itself happens on Gradle sync or by manually running the task:

```shell
./gradlew codegenModuleWasmWasiFooService
```



