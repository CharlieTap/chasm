package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.type.FunctionType

class References internal constructor(
    val slots: IntArray,
    val writesThrough: Boolean,
    val isReference: BooleanArray,
    val rootIndex: IntArray,
    val publication: RootPublication = RootPublication.EVERY_WRITE,
    val refreshes: RootRefreshes = RootRefreshes.ALWAYS,
) {
    fun publishesAfter(position: Int): Boolean {
        return writesThrough && publication.requiresPublicationAfter(position)
    }

    fun publishes(
        position: Int,
        slot: Int,
    ): Boolean {
        return writesThrough && isReference[slot] && publication.requiresPublication(position, rootIndex[slot])
    }

    companion object {
        fun none(frameSlots: Int): References {
            return References(IntArray(0), writesThrough = false, BooleanArray(frameSlots), IntArray(frameSlots))
        }
    }
}

fun analyzeReferences(
    table: InstructionTable,
    function: CirFunction,
    type: FunctionType,
    frameSlots: Int,
    analysis: ModuleAnalysis,
): References {

    if (!analysis.publishesRegisters) {
        return References.none(frameSlots)
    }

    val references = BooleanArray(frameSlots)
    function.exceptions?.regions?.forEach { region ->
        region.catches.forEach { handler -> handler.payloadSlots.forEach { references[it] = true } }
    }
    type.params.types.forEachIndexed { index, parameter -> if (collectable(parameter)) references[index] = true }
    val writes = table.writes
    var copies = 0
    for (index in 0 until writes.count) {
        if (writes.references[index]) {
            references[writes.slots[index]] = true
        }
        if (writes.sources[index] != SlotWriteTable.NOT_A_COPY) {
            copies++
        }
    }

    val sources = IntArray(copies)
    val destinations = IntArray(copies)
    copies = 0
    for (index in 0 until writes.count) {
        if (writes.sources[index] == SlotWriteTable.NOT_A_COPY) {
            continue
        }
        sources[copies] = writes.sources[index]
        destinations[copies++] = writes.slots[index]
    }
    val graph = PropagationGraph(
        nodes = frameSlots,
        sources = sources,
        destinations = destinations,
    )
    graph.spread(references)

    val slots = references.indices.filter { references[it] }.toIntArray()
    val rootIndex = IntArray(frameSlots).also { index -> slots.forEachIndexed { root, slot -> index[slot] = root } }
    val unpublished = References(slots, writesThrough = false, references, rootIndex)
    val writesThrough = slots.isNotEmpty() &&
        function.instructions.any { instruction -> instruction.mayCollectWhileFrameLive(analysis.mayCollect) }
    if (!writesThrough) {
        return unpublished
    }

    val flow = flowGraph(table, function)
    val needs = analyzeRootNeeds(table, flow, function, analysis.mayCollect, rootIndex, references, slots.size)
    if (needs != null && needs.isEmpty()) {
        return unpublished
    }

    val publication = analyzeRootPublication(flow, function, analysis.mayCollect, needs)
    val refreshes = analyzeRootRefreshes(table, flow, function, type, rootIndex, references, publication)
    return References(slots, writesThrough = true, references, rootIndex, publication, refreshes)
}
