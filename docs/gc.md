## What is Wasm GC

Chasm supports the [Wasm GC proposal](https://github.com/WebAssembly/gc/blob/main/proposals/gc/Overview.md). This means that *if* your Wasm
binary includes Wasm GC instructions, Chasm is capable of executing them.

Wasm-GC is an extension to the WebAssembly virtual machine that allows garbage-collected languages to compile to it. It adds new
instructions to the virtual machine's specification to create structs and arrays whose lifecycle is managed by the runtime implementing
the specification (i.e., chasm). Historically, garbage-collected languages would have to embed their own garbage collection code in the
Wasm binary, massively inflating its size.

## Chasm's GC

It's important to understand that if your Wasm binary contains no Wasm-GC instructions, then the following strategies will have no effect.
There will be no heap checks or pauses as your program will use linear memory instead.

Chasm supports three [GCStrategies](../config/src/commonMain/kotlin/io/github/charlietap/chasm/config/GCStrategy.kt):

* **Arena (Default)**

This is the default mode of operation. In this mode, chasm will rewrite the bytecode of your Wasm functions to include a single
unconditional pause at the end of the function's execution, removing all GC objects which are no longer referenced. It's recommended
to use this mode unless you run into out of memory issues as the alternative (Traditional) requires costly heap checks.

* **Manual**

In this mode, chasm will perform no heap checks and will not pause for garbage collection. It's expected the user monitors the heap size
and manually triggers garbage collection using the [GC public API](../chasm/src/commonMain/kotlin/io/github/charlietap/chasm/embedding/GC.kt).

* **Traditional**

This mode of operation functions like a traditional garbage collector. Chasm will rewrite the bytecode of your functions and insert heap
checks after every allocation. If the heap exceeds the threshold configurable in the `RuntimeConfig`, a pause will run, removing all dead
references.
