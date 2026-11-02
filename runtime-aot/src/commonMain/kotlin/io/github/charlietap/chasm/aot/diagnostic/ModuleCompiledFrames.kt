@file:OptIn(ExperimentalChasmApi::class, InternalChasmApi::class)

package io.github.charlietap.chasm.aot.diagnostic

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.internal.CHUNK_CLASS_PREFIX
import io.github.charlietap.chasm.aot.internal.CHUNK_METHOD_SEPARATOR
import io.github.charlietap.chasm.aot.internal.CODE_CLASS_PREFIX
import io.github.charlietap.chasm.aot.internal.FUNCTION_METHOD_PREFIX
import io.github.charlietap.chasm.runtime.compiled.CompiledStackTrace
import io.github.charlietap.chasm.runtime.diagnostic.CompiledFrames
import io.github.charlietap.chasm.runtime.diagnostic.CompiledFrames.Companion.NOT_COMPILED
import io.github.charlietap.chasm.runtime.diagnostic.CompiledTrace
import io.github.charlietap.chasm.runtime.store.Store

internal class ModuleCompiledFrames(
    private val module: CompiledModule,
) : CompiledFrames {

    private val classPrefix = module.packageName + "."

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
        // Once a shrinker has renumbered the lines, only its mapping says which line Chasm gave a frame.
        val original = module.trapLines?.original(className.substringAfterLast('.'), methodName, line) ?: line
        if (original <= 0) return -1
        val separator = methodName.indexOf(CHUNK_METHOD_SEPARATOR)
        val start = if (separator < 0) {
            0
        } else {
            val chunk = methodName.substring(separator + CHUNK_METHOD_SEPARATOR.length).toIntOrNull() ?: return -1
            module.trapSites?.function(function)?.chunkStarts?.getOrNull(chunk) ?: return -1
        }
        return start + original - 1
    }

    override fun trace(
        store: Store,
        exception: Throwable,
    ): CompiledTrace = CompiledStackTrace(store, exception)
}
