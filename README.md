<br>

<img src="chasm.svg" width="240" alt="chasm logo"/>


![badge][badge-android]
![badge][badge-jvm]
![badge][badge-ios]
![badge][badge-linux]
![badge][badge-mac]

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

# Setup

```kotlin
dependencies {
    implementation("io.github.charlietap.chasm:chasm:0.9.65")
}
```

# Usage

### Invoking functions

Webassembly compilations output a [Module](chasm/src/commonMain/kotlin/io/github/charlietap/chasm/embedding/shapes/Module.kt)
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

Modules often depend on [imports](chasm/src/commonMain/kotlin/io/github/charlietap/chasm/embedding/shapes/Import.kt), imports can be one of the following:

- Functions
- Globals
- Memories
- Tables
- Tags

For the most part imports will actually be exports from other modules, this is the mechanism that wasm uses to share data/behaviour between modules.
However you can also allocate them separately using the factory functions:

```kotlin
val function = function(store, functionType, hostFunction)
val global = global(store, globalType, initialValue)
val memory = memory(store, memoryType)
val table = table(store, tableType, initialValue)
val tag = tag(store, tagType)
```

Once you have your importables you can pass them in at instantiation time.

```kotlin
val import = Import(
    "import module name",
    "import entity name",
    function,
)
val result = instance(store, module, listOf(import))
```

### Host functions

Host functions are kotlin functions that can be called by wasm programs at runtime.
The majority of host functions represent system calls, WASI for example is intended to be integrated as imports of host functions.

Allocation of a host function requires a [FunctionType](chasm/src/commonMain/kotlin/io/github/charlietap/chasm/embedding/shapes/FunctionType.kt)

The function type describes the inputs and outputs of your function so the runtime can call it and use its results

Once you have defined a function type you can allocate the host function like so

```kotlin
val functionType = FunctionType(emptyList(), listOf(ValueType.Number.I32))
val hostFunction = HostFunction { params ->
    println("Hello world")
    listOf(Value.Number.I32(117))
}

val result = function(store, funcType, hostFunction)
```

### Working with Strings

The WebAssembly type system does not currently support Strings, when you compile functions that give or take strings to WebAssembly
compilers will replace them with pointers. Using these pointers alongside an exported memory will give you the ability to work with
Strings and create high level wrappers for your exported wasm functions.

#### Get access to the memory

For most scenarios you can just look for an exported memory in the exports of an instance:

```kotlin
instance.exports.firstNotNullOf { it.value as? Memory }
```

If you're compiling to wasm-wasi, the wasi standard specifies the export will also have the name "memory"

```kotlin
instance.exports.first { it.name == "memory" && it.value is Memory }.value
```
Chasm supports the multiple memories proposal so it's possible that there exists more than one memory if the compiler supports it.
In this case you'll need to follow the guidance of the compiler.

#### Transforming pointers to strings

Say you have a function like the following which you compile to WebAssembly:

```kotlin
fun concat(input: String): String
```

The compiler will either output:

2 Integers

```wat
(func $concat (param i32 i32) (result i32 i32)
```

This is by far the most common transformation, where the string is replaced with two ints, the first its pointer in memory and the
second is its length in bytes.

Or

1 Integer

```wat
(func $concat (param i32) (result i32)
```

In this case each integer is a pointer, but instead the length is encoded in the bytes where it resides. Either by prefixing the string
with its length in bytes or by terminating the string with a null byte, please follow the guidance of the source compiler for learning
the encoding strategy.

Once you know the encoding you can use one of chasm's many functions for working with memories to read or write the string.

```kotlin
readByte(store, memory, int)
readBytes(store, memory, buffer, srcPointer, bytesToRead, dstPointer)
readUtf8String(store, memory, pointer, stringLengthInBytes)
readNullTerminatedUtf8String(store, memory, pointer)
writeUtf8String(store, memory, pointer, string)
```

#### Calling functions which take Strings

 You will need to write the string to memory before invoking the function:

```kotlin
writeUtf8String(store, memory, pointer, string)
invoke(store, instance, "concat", listOf(pointer, stringLengthInBytes))
```

#### Calling functions which return Strings

```kotlin
val results = invoke(store, instance, "concat", args).expect("")
val pointer = (results[0] as NumberValue.I32).value
val stringLengthInBytes = (results[1] as NumberValue.I32).value
val string = readUtf8String(store, memory, pointer, stringLengthInBytes)
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
