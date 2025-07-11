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

# Module Configuration

Each module declared in the extensions DSL can be individually configured, the following configuration options are available:

### `binary: File`

A file pointing to the wasm binary the codegen should use to generate the kotlin interface, this option is only used when in
`Mode.CONSUMER`

### `packageName: String`

The package name for each of the code generated files

### `initializers: Set<String>`

This property takes a set of strings, each string should be the name of a function appearing in the wasm binary. Each initializer
function will be called once and only once on instantiation of the module.


## Function configuration

Each function within a module is configurable through the Gradle plugin extension you can specify parameter names and String
encoding strategies.

### Configuring parameters

For example say you have the following wasm function:

```wat
   (func $multiple_param_function (export "multiple_param_function") (param i32 f64) (result f64)
     local.get 0
     f64.convert_i32_s
     local.get 1
     f64.mul
   )
```

You can now configure the names of the params through the gradle extension

```kotlin
chasm {
    modules {
        create("FooService") {
            function("multiple_param_function") {
                intParam("a")
                doubleParam("b")
                doubleReturnType()
            }
        }
    }
}
```

### Configuring string encoding

For example say you have a function in your wasm module which returns a string encoded in two integers:

```wat
   (func $string_function (export "string_function") (result i32 i32)
     i32.const 1
     i32.const 18
   )
```

You can now tell the codegen to produce a string by providing a function definition in the gradle plugin extension

```kotlin
chasm {
    modules {
        create("FooService") {
            function("string_function") {
                stringReturnType()
            }
        }
    }
}
```

Different compilers use different strategies to encode strings into memory, the codegen supports the following strategies:

```kotlin
enum class StringEncodingStrategy {
    /**
     * The pointer and length of the string are encoded in two I32s
     */
    POINTER_AND_LENGTH,

    /**
     * The pointer of the string is encoded in an I32 and end of the String is the first null byte
     * encountered after that pointer
     */
    NULL_TERMINATED,

    /**
     * The pointer of the string is encoded in an I32 and length of the string is encoded in an integer (4 bytes)
     * found at that pointer with the bytes of the string following
     */
    LENGTH_PREFIXED,

    /**
     * The pointer and length of the string are encoded in a single I64
     */
    PACKED_POINTER_AND_LENGTH,
}
```

By default codegen will assume pointer and length encoding, but the encoding can optionally given in the configuration

```kotlin
chasm {
    modules {
        create("FooService") {
            function("string_function") {
                stringReturnType(StringEncodingStrategy.NULL_TERMINATED)
            }
        }
    }
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
### `generateTypesafeGlobalProperties: Boolean`

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





