package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.FrameFacts
import io.github.charlietap.chasm.compiler.backend.analysis.NarrowHomeMismatch
import io.github.charlietap.chasm.compiler.backend.analysis.References
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeReferences
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeSlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.form.access
import io.github.charlietap.chasm.compiler.backend.analysis.frameFacts
import io.github.charlietap.chasm.compiler.backend.analysis.jumpTargets
import io.github.charlietap.chasm.compiler.backend.analysis.redundantBoundsChecks
import io.github.charlietap.chasm.compiler.backend.jvm.abi.BATCH_INSTRUCTIONS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MAX_LOCALS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MAX_SHORT_BRANCH_METHOD_BYTES
import io.github.charlietap.chasm.compiler.backend.jvm.abi.chunkClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.lower.CHUNK_ARGUMENTS
import io.github.charlietap.chasm.compiler.backend.jvm.lower.Chunk
import io.github.charlietap.chasm.compiler.backend.jvm.lower.ChunkMethod
import io.github.charlietap.chasm.compiler.backend.jvm.lower.Emitter
import io.github.charlietap.chasm.compiler.backend.jvm.lower.FunctionFacts
import io.github.charlietap.chasm.compiler.backend.jvm.lower.InlineMemory
import io.github.charlietap.chasm.compiler.backend.jvm.lower.Locals
import io.github.charlietap.chasm.compiler.backend.jvm.lower.LowerContext
import io.github.charlietap.chasm.compiler.backend.jvm.lower.Unsupported
import io.github.charlietap.chasm.compiler.backend.jvm.lower.chunkDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.lower.chunkName
import io.github.charlietap.chasm.compiler.backend.jvm.lower.lowerChunkBody
import io.github.charlietap.chasm.compiler.backend.jvm.lower.lowerDispatcher
import io.github.charlietap.chasm.compiler.backend.jvm.lower.lowerFunctionBody
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodTooLargeException
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.IincInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LookupSwitchInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TableSwitchInsnNode
import org.objectweb.asm.tree.VarInsnNode

internal typealias FunctionLowerer = (ModuleLowering, CirFunction, MethodRef) -> FunctionResult

internal class GeneratedMethod(
    val owner: String?,
    val node: MethodNode,
)

internal sealed interface FunctionResult {

    class Lowered(
        val methods: List<GeneratedMethod>,
        val resources: List<Resource>,
    ) : FunctionResult

    class Rejected(
        val reason: String,
    ) : FunctionResult
}

internal fun FunctionLowerer(
    module: ModuleLowering,
    function: CirFunction,
    method: MethodRef,
): FunctionResult {
    val declarations = module.declarations
    val frame = frameFacts(function, declarations)
    val references = analyzeReferences(function, frame.type, frame.frameSlots, declarations, module.analysis.publishesRegisters)
    val lowering = FunctionLowering(module, function, frame, references, method)
    return try {
        val methods = try {
            lowering.lower(narrowSlots = true)
        } catch (_: NarrowHomeMismatch) {
            lowering.lower(narrowSlots = false)
        }
        FunctionResult.Lowered(methods, lowering.resources)
    } catch (unsupported: Unsupported) {
        FunctionResult.Rejected(unsupported.message ?: "unsupported")
    }
}

private class FunctionLowering(
    private val module: ModuleLowering,
    private val function: CirFunction,
    private val frame: FrameFacts,
    private val references: References,
    private val method: MethodRef,
) {
    private val maxMethodBytes = module.options.functionMaxMethodBytes[function.index.index] ?: module.options.maxMethodBytes

    private val inlineMemory = inlineMemory(function.instructions.indices)

    private val facts = FunctionFacts(module, function, frame, references, function.redundantBoundsChecks(module.declarations))

    private fun inlineMemory(
        positions: IntRange,
    ) = module.inlineMemory?.takeIf {
        positions.any { position -> (function.instructions[position] as? MemoryInstruction)?.access()?.memory?.index == 0 }
    }

    val resources: List<Resource> get() = facts.resources

    fun lower(
        narrowSlots: Boolean,
    ): List<GeneratedMethod> {
        recordings.clear()
        val slotKinds = if (narrowSlots) {
            analyzeSlotKinds(
                function = function,
                parameters = frame.type.params.types,
                frameSlots = frame.frameSlots,
                localTypes = frame.localTypes,
                initialValues = frame.initialValues,
                references = references,
                declarations = module.declarations,
            )
        } else {
            SlotKinds.wide(frame.frameSlots)
        }
        val whole = whole(slotKinds)
        val fits = whole?.node?.takeIf { node -> finish(node) <= maxMethodBytes }
        if (fits != null && module.options.splitInstructions == null) return listOf(GeneratedMethod(null, fits))
        val liveness = whole?.takeIf { lowered ->
            lowered.node.instructions.size().toLong() * ((lowered.locals.size + 63) / 64) <= MAX_LIVENESS_WORDS
        }?.let { lowered ->
            slotLiveness(lowered.node, lowered.locals, frame.frameSlots, function.instructions.size, lowered.context::placement)
        }
        val offsets = whole?.let { lowered ->
            IntArray(function.instructions.size + 1) { position -> lowered.context.placement(position).resolvedOffset() }
        }
        return split(slotKinds, liveness, offsets)
    }

    private fun whole(
        slotKinds: SlotKinds,
    ): Whole? {
        val locals = Locals(module.bindingType, method.parameters.drop(3), slotKinds, frame.frameSlots, inlineMemory = inlineMemory)
        if (locals.size > MAX_LOCALS) return null
        val node = methodNode(method.name, method.descriptor)
        val context = LowerContext(facts, slotKinds, locals, Emitter(node, locals.frameTypes))
        context.lowerFunctionBody(frameCost(locals))
        return Whole(node, locals, context)
    }

    private fun split(
        slotKinds: SlotKinds,
        liveness: SlotLiveness?,
        offsets: IntArray?,
    ): List<GeneratedMethod> {
        val targets = function.jumpTargets()
        val size = function.instructions.size
        val crossings = loopCrossings(function)
        val live = liveness?.let { slots -> slots::liveCount }

        fun pack(
            from: Int,
            until: Int,
            budget: Int,
        ): List<Pair<IntRange, LoweredChunk>> {
            val packed = ArrayList<Pair<IntRange, LoweredChunk>>()
            var start = from
            while (start < until) {
                var limit = reach(offsets, start, until, budget)
                while (true) {
                    val end = cheapestCut(crossings, start, limit, live)
                    val range = start until end
                    val entries = range.filter { position -> position == range.first || targets[position] }.toIntArray()
                    val first = chunk(range, entries, slotKinds, liveness, budget)
                    val attempt = if (first.chunk == null && end - start == 1 && budget < maxMethodBytes) {
                        chunk(range, entries, slotKinds, liveness, maxMethodBytes)
                    } else {
                        first
                    }
                    val lowered = attempt.chunk
                    if (lowered != null) {
                        packed += range to lowered
                        start = end
                        break
                    }
                    if (end - start == 1) throw Unsupported("instruction $start too large for a method")
                    val fitting = if (attempt.length > budget) {
                        ((end - start).toLong() * budget * 9 / (attempt.length * 10L)).toInt()
                    } else {
                        (end - start) / 2
                    }
                    limit = start + fitting.coerceIn(1, end - start - 1)
                }
            }
            return packed
        }

        val packed = sortedMapOf<Int, Pair<IntRange, LoweredChunk>>()
        pack(0, size, maxMethodBytes).forEach { piece -> packed[piece.first.first] = piece }
        val chunks: List<Pair<IntRange, LoweredChunk>>
        var continuationIds: IntArray
        while (true) {
            val entered = BooleanArray(size + 1)
            packed.values.forEach { (range, chunk) ->
                entered[range.first] = true
                chunk.exits.forEach { target -> entered[target] = true }
            }
            var next = 0
            continuationIds = IntArray(size + 1) { position -> if (entered[position]) next++ else -1 }
            val lowered = packed.values.map { (range, _) ->
                val entries = range.filter { position -> entered[position] }.toIntArray()
                range to chunk(range, entries, slotKinds, liveness, maxMethodBytes, continuationIds).chunk
            }
            val outgrown = lowered.filter { (_, chunk) -> chunk == null }.map { (range, _) -> range }
            if (outgrown.isEmpty()) {
                chunks = lowered.map { (range, chunk) -> range to checkNotNull(chunk) }
                break
            }
            outgrown.forEach { range ->
                val first = packed.getValue(range.first).second
                if (range.count() == 1) throw Unsupported("instruction ${range.first} too large for a method")
                packed.remove(range.first)
                pack(range.first, range.last + 1, minOf(maxMethodBytes, first.length) - 1).forEach { piece -> packed[piece.first.first] = piece }
            }
        }
        val largestCost = chunks.maxOf { (_, chunk) -> chunk.frameCost }
        val owners = chunkOwners(chunks.map { it.first })
        val methods = chunks.mapIndexed { index, (range, chunk) ->
            chunk.node.name = chunkName(method.name, index)
            ChunkMethod(range.first, range.last + 1, chunk.node.name, owners[index] ?: method.owner, chunk.entries.size)
        }
        val dynamic = chunks.flatMap { (_, chunk) -> chunk.dynamicExits.asList() }.distinct().associateWith { position -> continuationIds[position] }
        val locals = Locals(module.bindingType, method.parameters.drop(3), slotKinds, frame.frameSlots, slots = IntArray(0))
        val dispatcher = methodNode(method.name, method.descriptor)
        val slotFrameCost = (frame.frameSlots * 8 + 31) / 32
        LowerContext(facts, slotKinds, locals, Emitter(dispatcher, locals.frameTypes))
            .lowerDispatcher(methods, frameCost(locals) + largestCost + slotFrameCost, dynamic)
        if (finish(dispatcher) > MAX_SHORT_BRANCH_METHOD_BYTES) {
            throw Unsupported("function needs ${chunks.size} chunks, more than one dispatcher holds")
        }
        return chunks.mapIndexed { index, (_, chunk) -> GeneratedMethod(owners[index], chunk.node) } + GeneratedMethod(null, dispatcher)
    }

    private fun reach(
        offsets: IntArray?,
        start: Int,
        until: Int,
        budget: Int,
    ): Int {
        val bound = minOf(until, start + (module.options.splitInstructions ?: until))
        if (offsets == null) return minOf(bound, start + CHUNK_INSTRUCTIONS)
        var low = start + 1
        var high = bound
        while (low < high) {
            val middle = (low + high + 1) / 2
            if (offsets[middle] - offsets[start] + CHUNK_OVERHEAD_BYTES <= budget) low = middle else high = middle - 1
        }
        return low
    }

    private fun chunkOwners(
        ranges: List<IntRange>,
    ): List<String?> {
        if (function.instructions.size <= BATCH_INSTRUCTIONS) return ranges.map { null }
        var group = 0
        var instructions = 0
        return ranges.map { range ->
            if (instructions > 0 && instructions + range.count() > BATCH_INSTRUCTIONS) {
                group++
                instructions = 0
            }
            instructions += range.count()
            chunkClassDescriptor(module.packagePath, function.index.index, group)
        }
    }

    private fun chunk(
        range: IntRange,
        entries: IntArray,
        slotKinds: SlotKinds,
        liveness: SlotLiveness?,
        limit: Int,
        continuationIds: IntArray? = null,
    ): ChunkAttempt {
        val inlineMemory = inlineMemory(range)
        val recorded = recordings.getOrPut(range) { record(range, entries, slotKinds, liveness != null, inlineMemory) }
        val used = recorded.used
        val exits = recorded.exits
        val slots = used.indices.filter { used[it] }.toIntArray()
        val locals = Locals(module.bindingType, CHUNK_ARGUMENTS, slotKinds, frame.frameSlots, slots = slots, inlineMemory = inlineMemory)
        if (locals.size > MAX_LOCALS) return ChunkAttempt(null, 0)
        val reloads = liveness?.let { live ->
            BooleanArray(frame.frameSlots) { slot -> used[slot] && entries.any { entry -> live.isLive(entry, slot) } }
        }
        val spills = liveness?.let { live ->
            val written = checkNotNull(recorded.written)
            BooleanArray(frame.frameSlots) { slot -> written[slot] && exits.any { exit -> live.isLive(exit, slot) } }
        }
        val node = methodNode(CHUNK_PLACEHOLDER, chunkDescriptor(module.bindingType))
        val chunk = Chunk(range.first, range.last + 1, entries, reloads, spills, continuationIds)
        LowerContext(facts, slotKinds, locals, Emitter(node, locals.frameTypes), chunk)
            .lowerChunkBody()
        val allowance = if (continuationIds == null) PADDING_PER_SWITCH * node.instructions.count { it is TableSwitchInsnNode || it is LookupSwitchInsnNode } else 0
        val length = finish(node) + allowance
        val lowered = if (length <= limit) {
            LoweredChunk(node, entries, exits, recorded.dynamicExits, frameCost(locals), length)
        } else {
            null
        }
        return ChunkAttempt(lowered, length)
    }

    private val recordings = HashMap<IntRange, Recording>()

    private fun record(
        range: IntRange,
        entries: IntArray,
        slotKinds: SlotKinds,
        withWrites: Boolean,
        inlineMemory: InlineMemory?,
    ): Recording {
        val used = BooleanArray(frame.frameSlots)
        val recording = Locals(module.bindingType, CHUNK_ARGUMENTS, slotKinds, frame.frameSlots, recording = used, inlineMemory = inlineMemory)
        val node = methodNode("recording", "()V")
        val chunk = Chunk(range.first, range.last + 1, entries)
        LowerContext(facts, slotKinds, recording, Emitter(node, recording.frameTypes), chunk).lowerChunkBody()
        return Recording(
            used = used,
            exits = (chunk.exits.keys + chunk.dynamicExits).toIntArray(),
            dynamicExits = chunk.dynamicExits.toIntArray(),
            written = if (withWrites) writtenSlots(node, recording) else null,
        )
    }

    private fun writtenSlots(
        node: MethodNode,
        locals: Locals,
    ): BooleanArray {
        val written = BooleanArray(frame.frameSlots)
        node.instructions.forEach { instruction ->
            val local = when {
                instruction is VarInsnNode && instruction.opcode in Opcodes.ISTORE..Opcodes.ASTORE -> instruction.`var`
                instruction is IincInsnNode -> instruction.`var`
                else -> return@forEach
            }
            val slot = locals.slotOf(local)
            if (slot >= 0) written[slot] = true
        }
        return written
    }

    private fun methodNode(
        name: String,
        descriptor: String,
    ) =
        MethodNode(Opcodes.ASM9, Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, name, descriptor, null, null).apply { visitCode() }

    private fun finish(
        node: MethodNode,
    ): Int {
        val end = Label()
        node.visitLabel(end)
        node.visitMaxs(0, 0)
        node.visitEnd()
        return codeLength(node, end)
    }
}

private fun Label.resolvedOffset(): Int = ((info as? LabelNode)?.label ?: this).offset

private fun codeLength(
    node: MethodNode,
    end: Label,
): Int {
    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
    writer.visit(CLASS_FILE_VERSION, Opcodes.ACC_PUBLIC, "Probe", null, "java/lang/Object", null)
    node.accept(writer)
    return (end.info as LabelNode).label.offset
}

private fun frameCost(
    locals: Locals,
): Int = (locals.size * 8 + FRAME_OVERHEAD_BYTES + 31) / 32

private const val FRAME_OVERHEAD_BYTES = 256

private class Whole(
    val node: MethodNode,
    val locals: Locals,
    val context: LowerContext,
)

private class LoweredChunk(
    val node: MethodNode,
    val entries: IntArray,
    val exits: IntArray,
    val dynamicExits: IntArray,
    val frameCost: Int,
    val length: Int,
)

private class Recording(
    val used: BooleanArray,
    val exits: IntArray,
    val dynamicExits: IntArray,
    val written: BooleanArray?,
)

private class ChunkAttempt(
    val chunk: LoweredChunk?,
    val length: Int,
)

private const val CHUNK_INSTRUCTIONS = 2_000

private const val MAX_LIVENESS_WORDS = 16_000_000L

private const val CHUNK_OVERHEAD_BYTES = 512

private const val PADDING_PER_SWITCH = 3

private const val CHUNK_PLACEHOLDER = "chunk"
