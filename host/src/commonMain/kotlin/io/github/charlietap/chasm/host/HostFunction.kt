package io.github.charlietap.chasm.host

/** Chasm's borrowed WebAssembly stack storage, with one [Long] per value. */
typealias HostStack = LongArray

/** Base slot of the parameters. Parameter `n` is stored at this slot plus `n`. */
typealias HostParameters = Int

/** Base slot of the results. Result `n` must be written at this slot plus `n`. */
typealias HostResults = Int

/** Opaque identity of a WebAssembly module instance calling a host function. */
interface HostModuleInstance

/**
 * A WebAssembly host function.
 *
 * WebAssembly describes values as being pushed onto and popped from a stack.
 * Chasm normally assigns those values fixed slots in a function's frame when
 * it compiles the module. A host function receives the base slot of its
 * [parameters] and the base slot where its [results] must be written.
 *
 * Parameters and results use their declared order. If a function has the type
 * `(i32, i64) -> (f32, f64)`, its slots are addressed like this:
 *
 * ```
 * parameters + 0    i32 parameter 0
 * parameters + 1    i64 parameter 1
 *
 * results + 0       f32 result 0
 * results + 1       f64 result 1
 * ```
 *
 * Although a traditional interpreter would pop the last parameter first, the
 * indexes passed to the helpers are not reversed. Read the first declared
 * parameter with `parameters.readI32(0)`, then the second with
 * `parameters.readI64(1)`. Write results in the same way with
 * `results.writeF32(0, value)` and `results.writeF64(1, value)`.
 *
 * Parameter and result ranges may overlap, so read any parameters before
 * overwriting the same slots with results. The [HostStack] is borrowed only
 * for this invocation. Chasm performs no slot validation or
 * marshalling.
 *
 * See [readI32], [readI64], [readF32], [readF64], [readRawReference],
 * [writeI32], [writeI64], [writeF32], [writeF64], and [writeRawReference] for
 * the available parameter and result accessors.
 */
fun interface HostFunction {

    context(
        _: HostStack,
        _: HostModuleInstance,
        _: HostResources,
    )
    fun invoke(parameters: HostParameters, results: HostResults)
}
