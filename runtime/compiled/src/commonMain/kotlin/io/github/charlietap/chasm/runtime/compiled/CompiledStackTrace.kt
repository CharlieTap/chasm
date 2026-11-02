package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.executor.invoker.compiled.compiledEntryClass
import io.github.charlietap.chasm.runtime.diagnostic.CompiledFrames
import io.github.charlietap.chasm.runtime.diagnostic.CompiledTrace
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import java.util.IdentityHashMap

/**
 * Reads compiled frames from the JVM stack trace of a trap. Each run of
 * compiled frames ends at a frame of the interpreter's entry into compiled
 * code; frames of other code inside a run, such as runtime helpers and host
 * functions, are skipped.
 */
class CompiledStackTrace(
    private val store: Store,
    exception: Throwable,
) : CompiledTrace {

    private val elements = exception.stackTrace
    private val entryClass = compiledEntryClass.java.name
    private var cursor = 0

    // Compared by identity: a module instance's own equality walks everything it holds.
    private val compiledModules: List<ModuleInstance> by lazy {
        val seen = IdentityHashMap<ModuleInstance, Unit>()
        val modules = ArrayList<ModuleInstance>()
        store.functions.forEach { function ->
            val module = (function as? FunctionInstance.WasmFunction)?.module ?: return@forEach
            if (module.compiledFrames != null && seen.put(module, Unit) == null) modules += module
        }
        modules
    }

    override fun segment(
        function: FunctionInstance.WasmFunction,
        frame: (address: Int, position: Int) -> Unit,
    ): Boolean {
        val run = ArrayList<StackTraceElement>()
        var reachedEntry = false
        while (cursor < elements.size) {
            val element = elements[cursor]
            if (element.className == entryClass) {
                reachedEntry = true
                while (cursor < elements.size && elements[cursor].className == entryClass) cursor++
                break
            }
            run += element
            cursor++
        }
        emit(run, function.module, frame)
        return reachedEntry
    }

    private fun emit(
        run: List<StackTraceElement>,
        entered: ModuleInstance,
        frame: (address: Int, position: Int) -> Unit,
    ) {
        val owners = arrayOfNulls<ModuleInstance>(run.size)
        val functions = IntArray(run.size)
        var owner = entered
        for (index in run.indices.reversed()) {
            val element = run[index]
            var function = owner.function(element)
            if (function == CompiledFrames.NOT_COMPILED) {
                owner = compiledModules.firstOrNull { module ->
                    function = module.function(element)
                    function != CompiledFrames.NOT_COMPILED
                } ?: continue
            }
            owners[index] = owner
            functions[index] = function
        }
        var previousAddress = -1
        var previousChunk = false
        for (index in run.indices) {
            val module = owners[index] ?: continue
            val frames = module.compiledFrames ?: continue
            val element = run[index]
            val function = functions[index]
            val address = module.functionAddresses.getOrNull(function)?.address ?: continue
            val chunk = frames.chunk(element.methodName)
            // A split function's own method only dispatches to the chunk below it.
            if (!chunk && previousChunk && address == previousAddress) continue
            frame(address, frames.position(function, element.className, element.methodName, element.lineNumber))
            previousAddress = address
            previousChunk = chunk
        }
    }

    private fun ModuleInstance.function(
        element: StackTraceElement,
    ): Int = compiledFrames?.function(element.className, element.methodName) ?: CompiledFrames.NOT_COMPILED
}
