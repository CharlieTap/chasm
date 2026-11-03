package io.github.charlietap.chasm.compiler.frontend.diagnostic

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.Provenance

class TrapSiteTable(
    val offsets: IntArray,
    val memories: IntArray,
)

typealias TrapSiteTables = (Module, CirModule, List<IntArray?>) -> Map<Int, TrapSiteTable>

fun TrapSiteTables(
    module: Module,
    code: CirModule,
    bodyOffsets: List<IntArray?>,
): Map<Int, TrapSiteTable> {
    val tables = HashMap<Int, TrapSiteTable>()
    code.functions.forEachIndexed { defined, function ->
        val provenance = function.provenance ?: return@forEachIndexed
        val body = module.functions.getOrNull(defined)?.body?.instructions ?: return@forEachIndexed
        val offsets = bodyOffsets.getOrNull(defined) ?: return@forEachIndexed
        tables[function.index.index] = trapSiteTable(body, provenance, offsets) ?: return@forEachIndexed
    }
    return tables
}

fun trapSiteTable(
    body: List<Instruction>,
    provenance: Provenance,
    offsets: IntArray,
): TrapSiteTable? {
    if (offsets.size != body.size) return null
    val count = provenance.sourceStart.size
    val siteOffsets = IntArray(count)
    val memories = IntArray(count)
    for (position in 0 until count) {
        val source = trapSourceIndex(body, provenance.sourceStart[position], provenance.sourceEnd[position])
        siteOffsets[position] = offsets.getOrElse(source) { NO_SOURCE_INDEX }
        memories[position] = trapSiteMemories(body.getOrNull(source))
    }
    return TrapSiteTable(siteOffsets, memories)
}
