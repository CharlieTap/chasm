package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.diagnostic.CompiledFrames
import io.github.charlietap.chasm.runtime.diagnostic.CompiledFrames.Companion.NOT_COMPILED
import io.github.charlietap.chasm.runtime.diagnostic.CompiledTrace
import io.github.charlietap.chasm.runtime.store.Store

@InternalChasmApi
class ModuleCompiledFrames(
    packageName: String,
    private val chunkStarts: (function: Int) -> IntArray?,
    private val originalLine: (className: String, methodName: String, line: Int) -> Int,
) : CompiledFrames {

    private val classPrefix = "$packageName."

    override fun function(
        className: String,
        methodName: String,
    ): Int {
        if (!className.startsWith(classPrefix)) return NOT_COMPILED
        val nameStart = classPrefix.length
        if (!className.startsWith(CODE_CLASS_PREFIX, nameStart) && !className.startsWith(CHUNK_CLASS_PREFIX, nameStart)) return NOT_COMPILED
        if (!methodName.startsWith(FUNCTION_METHOD_PREFIX)) return NOT_COMPILED
        val separator = methodName.indexOf(CHUNK_METHOD_SEPARATOR)
        val end = if (separator < 0) methodName.length else separator
        return methodName.substring(FUNCTION_METHOD_PREFIX.length, end).toIntOrNull() ?: NOT_COMPILED
    }

    override fun chunk(
        methodName: String,
    ): Boolean = CHUNK_METHOD_SEPARATOR in methodName

    override fun position(
        function: Int,
        className: String,
        methodName: String,
        line: Int,
    ): Int {
        val original = originalLine(className, methodName, line)
        if (original <= 0) return -1
        val separator = methodName.indexOf(CHUNK_METHOD_SEPARATOR)
        val start = if (separator < 0) {
            0
        } else {
            val chunk = methodName.substring(separator + CHUNK_METHOD_SEPARATOR.length).toIntOrNull() ?: return -1
            chunkStarts(function)?.getOrNull(chunk) ?: return -1
        }
        return start + original - 1
    }

    override fun trace(
        store: Store,
        exception: Throwable,
    ): CompiledTrace = CompiledStackTrace(store, exception)
}
