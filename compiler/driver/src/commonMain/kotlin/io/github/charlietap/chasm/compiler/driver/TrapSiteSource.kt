package io.github.charlietap.chasm.compiler.driver

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.diagnostic.TrapSiteTables
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.runtime.diagnostic.FunctionTrapSites

@InternalChasmApi
typealias FunctionBodyOffsets = (ModuleConfig, ByteArray) -> List<IntArray?>

@InternalChasmApi
class TrapSiteSource(
    private val bodyOffsets: FunctionBodyOffsets,
    private val trapSiteTables: TrapSiteTables,
) {

    fun sites(
        source: SourceModule,
        code: CirModule,
    ): Map<Int, FunctionTrapSites> {

        val binary = source.binary ?: return emptyMap()
        val offsets = bodyOffsets(ModuleConfig(), binary)

        return trapSiteTables(source.module, code, offsets)
            .mapValues { (_, table) -> FunctionTrapSites(chunkStarts = IntArray(0), offsets = table.offsets, memories = table.memories) }
    }
}
