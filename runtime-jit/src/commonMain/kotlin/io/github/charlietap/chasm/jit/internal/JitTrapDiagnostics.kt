@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.jit.internal

import com.github.michaelbull.result.get
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.frontend.DefinedFunctionCompiler
import io.github.charlietap.chasm.compiler.frontend.diagnostic.trapSiteTable
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.declarations.ModuleNames
import io.github.charlietap.chasm.decoder.WasmFunctionBodyOffsets
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.module.moduleNames
import io.github.charlietap.chasm.runtime.diagnostic.FunctionTrapSites

internal class JitTrapDiagnostics(
    private val binary: ByteArray?,
    private val config: ModuleConfig,
    private val chunkStarts: Map<Int, IntArray>,
) {

    private val named: Module? by lazy {
        binary?.let { bytes -> WasmModuleDecoder(config.copy(decodeNameSection = true), bytes).get() }
    }

    private val compiler: DefinedFunctionCompiler? by lazy { named?.let(::DefinedFunctionCompiler) }

    private val sites = HashMap<Int, FunctionTrapSites>()

    val names: ModuleNames? by lazy {
        try {
            named?.let(::moduleNames)
        } catch (failure: RuntimeException) {
            null
        }
    }

    fun functionTrapSites(
        function: Int,
    ): FunctionTrapSites = synchronized(sites) {
        sites.getOrPut(function) {
            val starts = chunkStarts[function] ?: IntArray(0)
            try {
                resolve(function, starts)
            } catch (failure: RuntimeException) {
                FunctionTrapSites(starts, IntArray(0))
            }
        }
    }

    private fun resolve(
        function: Int,
        starts: IntArray,
    ): FunctionTrapSites {
        val module = named ?: return FunctionTrapSites(starts, IntArray(0))
        val bytes = binary ?: return FunctionTrapSites(starts, IntArray(0))
        val defined = module.functions.indexOfFirst { candidate -> candidate.idx.toInt() == function }
        if (defined < 0) return FunctionTrapSites(starts, IntArray(0))
        val provenance = compiler?.compile(defined, recordProvenance = true)?.get()?.provenance
        val offsets = WasmFunctionBodyOffsets(config, bytes, defined)
        val table = if (provenance != null && offsets != null) {
            trapSiteTable(module.functions[defined].body.instructions, provenance, offsets)
        } else {
            null
        }
        return FunctionTrapSites(starts, table?.offsets ?: IntArray(0), table?.memories ?: IntArray(0))
    }
}
