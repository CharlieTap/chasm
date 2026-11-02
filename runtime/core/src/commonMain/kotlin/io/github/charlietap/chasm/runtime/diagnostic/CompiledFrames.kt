package io.github.charlietap.chasm.runtime.diagnostic

import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.store.Store

/** Maps a module's compiled methods, as a platform stack trace names them, back to its Wasm functions. */
interface CompiledFrames {

    /** The Wasm function index [methodName] of [className] was compiled from, or [NOT_COMPILED]. */
    fun function(
        className: String,
        methodName: String,
    ): Int

    /** True when [methodName] holds part of a function that was split into several methods. */
    fun chunk(
        methodName: String,
    ): Boolean

    /** The compiled instruction [line] of [methodName] in [className] marks, or -1 when it is not known. */
    fun position(
        function: Int,
        className: String,
        methodName: String,
        line: Int,
    ): Int

    /** Starts reading the compiled frames a trap's [exception] passed through. */
    fun trace(
        store: Store,
        exception: Throwable,
    ): CompiledTrace

    companion object {
        const val NOT_COMPILED = -1
    }
}

/** The compiled frames of one trap, read in order from the innermost entry into compiled code. */
interface CompiledTrace {

    /**
     * Reports the frames compiled code ran between the interpreter entering
     * [function] and the next entry inward, innermost first. Returns false
     * when the platform trace ended before reaching that entry.
     */
    fun segment(
        function: FunctionInstance.WasmFunction,
        frame: (address: Int, position: Int) -> Unit,
    ): Boolean
}
