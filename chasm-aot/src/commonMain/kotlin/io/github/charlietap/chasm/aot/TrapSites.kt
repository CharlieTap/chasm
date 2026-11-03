@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.diagnostic.TrapSiteTables
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmFunctionBodyOffsets
import io.github.charlietap.chasm.runtime.compiled.CHUNK_CLASS_PREFIX
import io.github.charlietap.chasm.runtime.compiled.CODE_CLASS_PREFIX
import io.github.charlietap.chasm.runtime.compiled.COMPILED_ENTRY_CLASS
import io.github.charlietap.chasm.runtime.compiled.FUNCTION_METHOD_PREFIX
import io.github.charlietap.chasm.runtime.diagnostic.FunctionTrapSites

// A shrinker that renames the compiled function methods or drops their line numbers leaves traps unattributable, and one
// that merges other classes into the entry into compiled code, or inlines its invoke, blurs where compiled runs begin.
internal fun shrinkerRules(
    moduleClass: String,
    config: CompileConfig,
): String {
    val module = "-keep class $moduleClass { public static *** INSTANCE; }\n"
    if (!config.stacktraces) return module
    val packageName = config.packageName
    return module + """
        -keepattributes LineNumberTable
        -keep class $packageName.$CODE_CLASS_PREFIX*, $packageName.$CHUNK_CLASS_PREFIX* { static *** $FUNCTION_METHOD_PREFIX*(...); }
        -keep,allowobfuscation,allowshrinking class $COMPILED_ENTRY_CLASS { *** invoke(...); }
    """.trimIndent() + "\n"
}

internal typealias TrapSitesFactory = (ModuleConfig, ByteArray, Module, CirModule) -> Map<Int, FunctionTrapSites>

/** Where each compiled instruction came from, keyed by function index; a function whose body cannot be located is left out. */
internal fun TrapSitesFactory(
    config: ModuleConfig,
    binary: ByteArray,
    module: Module,
    code: CirModule,
): Map<Int, FunctionTrapSites> = TrapSiteTables(module, code, WasmFunctionBodyOffsets(config, binary))
    .mapValues { (_, table) -> FunctionTrapSites(chunkStarts = IntArray(0), offsets = table.offsets, memories = table.memories) }
