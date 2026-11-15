package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.analysis.hasBit
import io.github.charlietap.chasm.compiler.backend.analysis.setBit
import io.github.charlietap.chasm.compiler.backend.dex.abi.HUGE_METHOD_CODE_UNITS
import io.github.charlietap.chasm.compiler.backend.dex.analysis.FunctionAnalysis
import io.github.charlietap.chasm.compiler.backend.dex.analysis.LivenessGraph
import io.github.charlietap.chasm.compiler.backend.dex.lower.Chunk
import io.github.charlietap.chasm.compiler.backend.dex.lower.Emitter
import io.github.charlietap.chasm.compiler.backend.dex.lower.LowerContext
import io.github.charlietap.chasm.compiler.backend.dex.lower.RegisterHomes
import io.github.charlietap.chasm.compiler.backend.dex.lower.Registers
import io.github.charlietap.chasm.compiler.backend.dex.lower.frameCost
import io.github.charlietap.chasm.compiler.backend.dex.lower.lowerChunkBody
import io.github.charlietap.chasm.compiler.backend.dex.lower.mixedSlots
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.DexterMethodTooLargeException
import io.github.charlietap.dexter.MemberAccess

internal fun interface ChunkEmitter {
    fun emit(
        analysis: FunctionAnalysis,
        splitFacts: SplitFacts,
        homes: RegisterHomes?,
        planned: PlannedChunk,
        method: MethodRef,
        next: Int,
    ): SplitChunk?
}

internal data class SplitChunk(
    val method: MethodRef,
    val entries: IntArray,
    val cost: Int,
)

internal class ProbingChunkEmitter(
    private val module: ModuleLowering,
    private val function: CirFunction,
    private val code: ClassBuilder,
) : ChunkEmitter {
    override fun emit(
        analysis: FunctionAnalysis,
        splitFacts: SplitFacts,
        homes: RegisterHomes?,
        planned: PlannedChunk,
        method: MethodRef,
        next: Int,
    ): SplitChunk? {

        val start = planned.start
        val end = planned.end
        val entries = planned.entries
        val frameSlots = analysis.facts.frameSlots
        val continuations = splitFacts.continuations
        val dexMethod = module.references.of(method)

        val chunk = Chunk(start, end, entries, continuations, next)
        val usedKinds = ByteArray(frameSlots)
        val constants = BooleanArray(analysis.cachedConstants.size)
        val recording = chunkRegisters(function, analysis, homes, null, recording = usedKinds, constants = constants)
        module.constants.beginMethod()
        val probe = code.beginStaticMethod(dexMethod, recording.count, MemberAccess.Public)
        try {
            LowerContext(module, function, analysis, recording, homes, Emitter(probe, module.references, recording = true), chunk).lowerChunkBody()
        } finally {
            probe.abort()
        }

        val registers = chunkRegisters(function, analysis, homes, usedKinds)
        module.constants.beginMethod()
        val body = code.beginStaticMethod(dexMethod, registers.count, MemberAccess.Public, HUGE_METHOD_CODE_UNITS)
        try {

            val protects = function.exceptions?.regions.orEmpty().any { region -> region.start.index < end && region.end.index > start }
            val spills = writtenSlots(analysis, start, end, protects)
            val liveness = splitFacts.liveness
            if (liveness != null && !chunk.leavesAnywhere) {

                val needed = LongArray(splitFacts.graph.words)
                for (target in chunk.exits) {
                    if (target < function.instructions.size) {
                        liveness.addLiveIn(target, needed)
                    }
                }

                for (slot in 0 until frameSlots) spills[slot] = spills[slot] && needed.hasBit(slot)
            }

            val inlineMemory = module.inlineMemory
            val usesMemory = analysis.usesInlineMemory && inlineMemory != null &&
                analysis.facts.table.accesses.usesMemory(start, end, inlineMemory.index)
            val reloads = chunkReloads(splitFacts.graph, start, end, entries, usedKinds, spills, protects)
            val mixed = mixedSlots(registers, homes, spills)

            val lowered = Chunk(start, end, entries, continuations, next, spills, constants, usesMemory, protects, reloads, mixed)
            LowerContext(module, function, analysis, registers, homes, Emitter(body, module.references), lowered).lowerChunkBody()
            body.finish()
            module.constants.finishMethod()
        } catch (_: DexterMethodTooLargeException) {
            return null
        } catch (failure: Throwable) {
            body.abort()
            throw failure
        }

        return SplitChunk(method, entries, frameCost(registers))
    }

    private fun writtenSlots(
        analysis: FunctionAnalysis,
        start: Int,
        end: Int,
        protects: Boolean,
    ): BooleanArray {

        val written = BooleanArray(analysis.facts.frameSlots)
        val table = analysis.facts.table
        for (index in table.writes.start[start] until table.writes.start[end]) written[table.writes.slots[index]] = true

        function.exceptions?.regions?.forEach { region ->
            if (region.start.index >= end || region.end.index <= start) {
                return@forEach
            }
            region.catches.forEach { handler -> handler.payloadSlots.forEach { slot -> written[slot] = true } }
        }
        if (protects) {
            analysis.facts.references.slots.forEach { slot -> written[slot] = true }
        }

        return written
    }
}

private fun chunkReloads(
    graph: LivenessGraph,
    start: Int,
    end: Int,
    entries: IntArray,
    held: ByteArray,
    spills: BooleanArray,
    protects: Boolean,
): BooleanArray? {

    if (protects) {
        return null
    }

    val exit = LongArray(graph.words)
    for (slot in held.indices) {
        if (spills[slot] && held[slot].toInt() != 0) {
            exit.setBit(slot)
        }
    }
    val liveness = graph.solve(start, end, exit)
    val live = LongArray(graph.words)
    for (entry in entries) liveness.addLiveIn(entry, live)

    return BooleanArray(held.size) { slot -> held[slot].toInt() != 0 && live.hasBit(slot) }
}

internal fun chunkRegisters(
    function: CirFunction,
    analysis: FunctionAnalysis,
    homes: RegisterHomes?,
    held: ByteArray?,
    recording: ByteArray? = null,
    constants: BooleanArray? = null,
): Registers {

    val window = analysis.facts.window

    return Registers(
        frameSlots = analysis.facts.frameSlots,
        temporaries = window.temporaries,
        windowWords = window.windowWords,
        parameterWords = 2,
        cachedConstants = analysis.cachedConstants.size,
        unsafeMemory = analysis.unsafeMemory,
        inlineMemory = analysis.usesInlineMemory,
        activation = analysis.facts.references.writesThrough,
        savesStack = function.exceptions != null,
        callBuffer = window.callBufferWords > 0,
        homes = homes,
        held = held,
        recording = recording,
        chunk = true,
        constantsUsed = constants,
    )
}
