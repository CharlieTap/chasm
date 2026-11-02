@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.CHUNK_CLASS_PREFIX
import io.github.charlietap.chasm.aot.internal.CODE_CLASS_PREFIX
import io.github.charlietap.chasm.aot.internal.COMPILED_ENTRY_CLASS
import io.github.charlietap.chasm.aot.internal.FUNCTION_METHOD_PREFIX
import io.github.charlietap.chasm.aot.internal.FunctionTrapSites
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.diagnostic.NO_SOURCE_INDEX
import io.github.charlietap.chasm.compiler.frontend.diagnostic.trapSiteMemories
import io.github.charlietap.chasm.compiler.frontend.diagnostic.trapSourceIndex
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmFunctionBodyOffsets

// A shrinker that renames the compiled function methods or drops their line numbers leaves traps unattributable, and one
// that merges other classes into the entry into compiled code, or inlines its invoke, blurs where compiled runs begin.
internal fun trapDiagnosticRules(
    packageName: String,
): String = """
    -keepattributes LineNumberTable
    -keep class $packageName.$CODE_CLASS_PREFIX*, $packageName.$CHUNK_CLASS_PREFIX* { static *** $FUNCTION_METHOD_PREFIX*(...); }
    -keep,allowobfuscation,allowshrinking class $COMPILED_ENTRY_CLASS { *** invoke(...); }
    """.trimIndent() + "\n"

internal typealias TrapSitesFactory = (ModuleConfig, ByteArray, Module, CirModule) -> Map<Int, FunctionTrapSites>

/** Where each compiled instruction came from, keyed by function index; a function whose body cannot be located is left out. */
internal fun TrapSitesFactory(
    config: ModuleConfig,
    binary: ByteArray,
    module: Module,
    code: CirModule,
): Map<Int, FunctionTrapSites> {
    val bodies = WasmFunctionBodyOffsets(config, binary)
    val sites = HashMap<Int, FunctionTrapSites>()
    code.functions.forEachIndexed { defined, function ->
        val provenance = function.provenance ?: return@forEachIndexed
        val body = module.functions.getOrNull(defined)?.body?.instructions ?: return@forEachIndexed
        val offsets = bodies.getOrNull(defined)?.takeIf { offsets -> offsets.size == body.size } ?: return@forEachIndexed
        val count = provenance.sourceStart.size
        val siteOffsets = IntArray(count)
        val memories = IntArray(count)
        for (position in 0 until count) {
            val source = trapSourceIndex(body, provenance.sourceStart[position], provenance.sourceEnd[position])
            siteOffsets[position] = offsets.getOrElse(source) { NO_SOURCE_INDEX }
            memories[position] = trapSiteMemories(body.getOrNull(source))
        }
        sites[function.index.index] = FunctionTrapSites(chunkStarts = IntArray(0), offsets = siteOffsets, memories = memories)
    }
    return sites
}
