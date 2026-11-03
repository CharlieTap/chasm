package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.analysis.NarrowHomeMismatch
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.SlotWriteVisitor
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeSlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.cheapestCut
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.access
import io.github.charlietap.chasm.compiler.backend.analysis.jumpTargets
import io.github.charlietap.chasm.compiler.backend.analysis.loopCrossings
import io.github.charlietap.chasm.compiler.backend.analysis.visitSlotWrites
import io.github.charlietap.chasm.compiler.backend.dex.abi.BINDING_TYPE
import io.github.charlietap.chasm.compiler.backend.dex.abi.HUGE_METHOD_CODE_UNITS
import io.github.charlietap.chasm.compiler.backend.dex.abi.MAX_REGISTERS
import io.github.charlietap.chasm.compiler.backend.dex.abi.chunkName
import io.github.charlietap.chasm.compiler.backend.dex.abi.staticParameters
import io.github.charlietap.chasm.compiler.backend.dex.analysis.FunctionAnalysis
import io.github.charlietap.chasm.compiler.backend.dex.analysis.analyzeFunction
import io.github.charlietap.chasm.compiler.backend.dex.lower.CHUNK_WINDOW_WORDS
import io.github.charlietap.chasm.compiler.backend.dex.lower.Chunk
import io.github.charlietap.chasm.compiler.backend.dex.lower.ENTRY_BITS
import io.github.charlietap.chasm.compiler.backend.dex.lower.Emitter
import io.github.charlietap.chasm.compiler.backend.dex.lower.LowerContext
import io.github.charlietap.chasm.compiler.backend.dex.lower.Registers
import io.github.charlietap.chasm.compiler.backend.dex.lower.Unsupported
import io.github.charlietap.chasm.compiler.backend.dex.lower.frameCost
import io.github.charlietap.chasm.compiler.backend.dex.lower.lowerChunkBody
import io.github.charlietap.chasm.compiler.backend.dex.lower.lowerDispatcher
import io.github.charlietap.chasm.compiler.backend.dex.lower.lowerFunctionBody
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexterMethodTooLargeException
import io.github.charlietap.dexter.MemberAccess

internal typealias FunctionLowerer = (ModuleLowering, CirFunction, ClassBuilder, DexMethod) -> String?

internal fun FunctionLowerer(
    module: ModuleLowering,
    function: CirFunction,
    code: ClassBuilder,
    method: DexMethod,
): String? {
    if (module.options.splitInstructions != null) return split(module, function, code, method)
    val inline = attempt(module, function, code, method, inlineAccesses = true)
    val attempt = if (inline is Attempt.TooLarge && inline.inlinedAccesses) {
        attempt(module, function, code, method, inlineAccesses = false)
    } else {
        inline
    }
    return when (attempt) {
        is Attempt.Lowered -> null
        is Attempt.Rejected -> attempt.reason
        is Attempt.TooLarge -> split(module, function, code, method)
    }
}

private sealed interface Attempt {

    class Lowered : Attempt

    class TooLarge(
        val inlinedAccesses: Boolean,
    ) : Attempt

    class Rejected(
        val reason: String,
    ) : Attempt
}

private fun attempt(
    module: ModuleLowering,
    function: CirFunction,
    code: ClassBuilder,
    method: DexMethod,
    inlineAccesses: Boolean,
): Attempt {
    val analysis = analyzeFunction(module, function, inlineAccesses)
    val window = analysis.window
    val layout = { narrowHomes: BooleanArray? ->
        Registers(
            frameSlots = analysis.frameSlots,
            temporaries = window.temporaries,
            windowWords = window.windowWords,
            parameterWords = parameterWords(analysis.type),
            cachedConstants = analysis.cachedConstants.size,
            unsafeMemory = analysis.unsafeMemory,
            inlineMemory = analysis.usesInlineMemory,
            activation = analysis.references.writesThrough,
            savesStack = function.exceptions != null,
            callBuffer = window.callBufferWords > 0,
            narrowHomes = narrowHomes,
        )
    }
    val wide = layout(null)
    if (wide.count > MAX_REGISTERS) return Attempt.TooLarge(inlinedAccesses = false)
    var narrowHomes = true
    while (true) {
        val slotKinds = if (narrowHomes) {
            analyzeSlotKinds(
                function = function,
                parameters = analysis.type.params.types,
                frameSlots = analysis.frameSlots,
                localTypes = analysis.localTypes,
                initialValues = analysis.initialValues,
                references = analysis.references,
                declarations = module.declarations,
                floatingHome = { slot -> wide.home(slot) < 256 },
            )
        } else {
            SlotKinds.wide(analysis.frameSlots)
        }
        val registers = if (narrowHomes) layout(BooleanArray(analysis.frameSlots) { slot -> slotKinds.kind(slot).let { it == SlotKinds.INT || it == SlotKinds.FLOAT } }) else wide
        val body = code.beginStaticMethod(method, registers.count, MemberAccess.Public)
        val context = LowerContext(module, function, analysis, registers, slotKinds, Emitter(body, module.methods))
        try {
            context.lowerFunctionBody()
            body.finish(HUGE_METHOD_CODE_UNITS)
            return Attempt.Lowered()
        } catch (mismatch: NarrowHomeMismatch) {
            body.abort()
            if (!narrowHomes) throw mismatch
            narrowHomes = false
        } catch (unsupported: Unsupported) {
            body.abort()
            return Attempt.Rejected(unsupported.message ?: "unsupported")
        } catch (_: DexterMethodTooLargeException) {
            return Attempt.TooLarge(inlinedAccesses = analysis.usesInlineMemory)
        }
    }
}

private fun parameterWords(
    type: FunctionType,
): Int = staticParameters(type).drop(3).sumOf(::descriptorWords)

private const val FRAME_TOO_LARGE = "frame too large"

private class SplitChunk(
    val method: MethodRef,
    val start: Int,
    val entries: IntArray,
    val cost: Int,
)

private fun split(
    module: ModuleLowering,
    function: CirFunction,
    code: ClassBuilder,
    method: DexMethod,
): String? {
    val analysis = analyzeFunction(module, function, inlineAccesses = true, split = true)
    if (chunkRegisters(function, analysis, null, null).count > MAX_REGISTERS) return FRAME_TOO_LARGE
    val starts = ArrayList<Int>()
    var narrowHomes = true
    while (true) {
        val slotKinds = if (narrowHomes) splitSlotKinds(module, function, analysis) else SlotKinds.wide(analysis.frameSlots)
        val chunks = try {
            splitChunks(module, function, code, method, analysis, slotKinds, starts)
        } catch (mismatch: NarrowHomeMismatch) {
            if (!narrowHomes) throw mismatch
            narrowHomes = false
            continue
        } catch (unsupported: Unsupported) {
            return unsupported.message ?: "unsupported"
        }
        val table = chunks.flatMapIndexed { index, chunk ->
            chunk.entries.mapIndexed { local, entry -> (entry.toLong() shl 32) or ((index.toLong() shl ENTRY_BITS) or local.toLong()) }
        }.sorted().toLongArray()
        val registers = dispatcherRegisters(analysis)
        val cost = frameCost(registers) + chunks.maxOf { it.cost } + (analysis.frameSlots * 8 + 31) / 32
        val body = code.beginStaticMethod(method, registers.count, MemberAccess.Public)
        try {
            LowerContext(module, function, analysis, registers, SlotKinds.wide(analysis.frameSlots), Emitter(body, module.methods))
                .lowerDispatcher(chunks.map { it.method }, table, cost)
        } catch (failure: Throwable) {
            body.abort()
            throw failure
        }
        try {
            body.finish(HUGE_METHOD_CODE_UNITS)
        } catch (_: DexterMethodTooLargeException) {
            return "function needs ${chunks.size} chunks, more than one dispatcher holds"
        }
        module.chunkStarts[function.index.index] = starts.toIntArray()
        return null
    }
}

private fun splitSlotKinds(
    module: ModuleLowering,
    function: CirFunction,
    analysis: FunctionAnalysis,
): SlotKinds {
    val wide = chunkRegisters(function, analysis, null, null)
    return analyzeSlotKinds(
        function = function,
        parameters = analysis.type.params.types,
        frameSlots = analysis.frameSlots,
        localTypes = analysis.localTypes,
        initialValues = analysis.initialValues,
        references = analysis.references,
        declarations = module.declarations,
        floatingHome = { slot -> wide.home(slot) < 256 },
    )
}

private fun splitChunks(
    module: ModuleLowering,
    function: CirFunction,
    code: ClassBuilder,
    method: DexMethod,
    analysis: FunctionAnalysis,
    slotKinds: SlotKinds,
    starts: MutableList<Int>,
): List<SplitChunk> {
    val size = function.instructions.size
    val crossings = loopCrossings(function)
    val targets = function.jumpTargets()
    function.exceptions?.regions?.forEach { region -> region.catches.forEach { handler -> targets[handler.target.index] = true } }
    val narrow = BooleanArray(analysis.frameSlots) { slot -> slotKinds.kind(slot).let { it == SlotKinds.INT || it == SlotKinds.FLOAT } }
    val owner = module.codeClass(function.index)
    val liveness = functionLiveness(module, function, analysis.frameSlots)
    val live = liveness?.let { slots -> slots::liveCount }
    val chunks = ArrayList<SplitChunk>()
    var start = 0
    val forced = module.options.splitInstructions
    var window = forced ?: CHUNK_INSTRUCTIONS
    while (start < size) {
        var limit = minOf(size, start + window)
        while (true) {
            val end = if (limit == size) size else cheapestCut(crossings, start, limit, live)
            val entries = (start until end).filter { position -> position == start || targets[position] }.toIntArray()
            val chunk = Chunk(start, end, entries)
            val chunkMethod = MethodRef(owner, chunkName(method.name, starts.size), CHUNK_PARAMETERS, "I")
            val used = BooleanArray(analysis.frameSlots)
            val constants = BooleanArray(analysis.cachedConstants.size)
            val recording = chunkRegisters(function, analysis, narrow, null, recording = used, constants = constants)
            val probe = code.beginStaticMethod(module.methods.of(chunkMethod), recording.count, MemberAccess.Public)
            try {
                LowerContext(module, function, analysis, recording, slotKinds, Emitter(probe, module.methods), chunk).lowerChunkBody()
            } finally {
                probe.abort()
            }
            val registers = chunkRegisters(function, analysis, narrow, used)
            val body = code.beginStaticMethod(module.methods.of(chunkMethod), registers.count, MemberAccess.Public)
            try {
                val spills = writtenSlots(module, function, analysis, start, end)
                if (liveness != null && !chunk.leavesAnywhere) {
                    val needed = BooleanArray(analysis.frameSlots)
                    for (target in chunk.exits) {
                        if (target >= size) continue
                        for (slot in 0 until analysis.frameSlots) if (liveness.isLive(target, slot)) needed[slot] = true
                    }
                    for (slot in 0 until analysis.frameSlots) spills[slot] = spills[slot] && needed[slot]
                }
                val usesMemory = analysis.usesInlineMemory && (start until end).any { position ->
                    (function.instructions[position] as? MemoryInstruction)?.access()?.memory == module.inlineMemory
                }
                val protects = function.exceptions?.regions.orEmpty().any { region -> region.start.index < end && region.end.index > start }
                val reloads = chunkReloads(module, function, analysis, start, end, entries, used, spills, protects)
                val lowered = Chunk(start, end, entries, spills, constants, usesMemory, protects, reloads)
                LowerContext(module, function, analysis, registers, slotKinds, Emitter(body, module.methods), lowered)
                    .lowerChunkBody()
            } catch (failure: Throwable) {
                body.abort()
                throw failure
            }
            val fits = try {
                body.finish(HUGE_METHOD_CODE_UNITS)
                true
            } catch (_: DexterMethodTooLargeException) {
                false
            }
            if (fits) {
                starts += start
                chunks += SplitChunk(chunkMethod, start, entries, frameCost(registers))
                if (forced == null) window += window / 4
                start = end
                break
            }
            if (end - start == 1) throw Unsupported("instruction $start too large for a method")
            limit = start + maxOf(1, (end - start) / 2)
            window = limit - start
        }
    }
    return chunks
}

private fun writtenSlots(
    module: ModuleLowering,
    function: CirFunction,
    analysis: FunctionAnalysis,
    start: Int,
    end: Int,
): BooleanArray {
    val written = BooleanArray(analysis.frameSlots)
    val visitor = WrittenSlots(written)
    for (position in start until end) visitSlotWrites(function.instructions[position], function, module.declarations, visitor)
    function.exceptions?.regions?.forEach { region ->
        if (region.start.index >= end || region.end.index <= start) return@forEach
        region.catches.forEach { handler -> handler.payloadSlots.forEach { slot -> written[slot] = true } }
        analysis.references.slots.forEach { slot -> written[slot] = true }
    }
    return written
}

private fun chunkReloads(
    module: ModuleLowering,
    function: CirFunction,
    analysis: FunctionAnalysis,
    start: Int,
    end: Int,
    entries: IntArray,
    held: BooleanArray,
    spills: BooleanArray,
    protects: Boolean,
): BooleanArray {
    if (protects) return held
    val slots = analysis.frameSlots
    val liveness = rangeLiveness(module, function, slots, start, end) { slot -> spills[slot] && held[slot] }
    return BooleanArray(slots) { slot -> held[slot] && entries.any { entry -> liveness.isLive(entry - start, slot) } }
}

private class WrittenSlots(
    private val written: BooleanArray,
) : SlotWriteVisitor {
    override fun value(
        slot: Int,
        type: ValueType,
    ) {
        written[slot] = true
    }

    override fun opaque(
        slot: Int,
        reference: Boolean,
    ) {
        written[slot] = true
    }

    override fun copy(
        source: Operand,
        destination: Int,
    ) {
        written[destination] = true
    }
}

private fun chunkRegisters(
    function: CirFunction,
    analysis: FunctionAnalysis,
    narrowHomes: BooleanArray?,
    held: BooleanArray?,
    recording: BooleanArray? = null,
    constants: BooleanArray? = null,
): Registers {
    val window = analysis.window
    return Registers(
        frameSlots = analysis.frameSlots,
        temporaries = window.temporaries,
        windowWords = window.windowWords,
        parameterWords = 2,
        cachedConstants = analysis.cachedConstants.size,
        unsafeMemory = analysis.unsafeMemory,
        inlineMemory = analysis.usesInlineMemory,
        activation = analysis.references.writesThrough,
        savesStack = function.exceptions != null,
        callBuffer = window.callBufferWords > 0,
        narrowHomes = narrowHomes,
        held = held,
        recording = recording,
        chunk = true,
        constantsUsed = constants,
    )
}

private fun dispatcherRegisters(
    analysis: FunctionAnalysis,
): Registers = Registers(
    frameSlots = 0,
    temporaries = 0,
    windowWords = CHUNK_WINDOW_WORDS,
    parameterWords = parameterWords(analysis.type),
    cachedConstants = 0,
    unsafeMemory = false,
    inlineMemory = false,
    activation = false,
    savesStack = true,
    callBuffer = false,
    chunk = true,
)

private val CHUNK_PARAMETERS = listOf(BINDING_TYPE, CONTEXT_TYPE, "I", "I", "I")

private const val CHUNK_INSTRUCTIONS = 600
