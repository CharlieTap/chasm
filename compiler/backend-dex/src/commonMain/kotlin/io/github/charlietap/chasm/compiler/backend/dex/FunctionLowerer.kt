package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.dex.abi.ART_MAX_COMPILED_REGISTERS
import io.github.charlietap.chasm.compiler.backend.dex.abi.BINDING_TYPE
import io.github.charlietap.chasm.compiler.backend.dex.abi.CHUNK_CODE_UNITS
import io.github.charlietap.chasm.compiler.backend.dex.abi.CHUNK_INSTRUCTIONS
import io.github.charlietap.chasm.compiler.backend.dex.abi.HUGE_METHOD_CODE_UNITS
import io.github.charlietap.chasm.compiler.backend.dex.abi.MAX_REGISTERS
import io.github.charlietap.chasm.compiler.backend.dex.abi.chunkName
import io.github.charlietap.chasm.compiler.backend.dex.analysis.DataflowSlotKindAnalyzer
import io.github.charlietap.chasm.compiler.backend.dex.analysis.FunctionAnalysis
import io.github.charlietap.chasm.compiler.backend.dex.analysis.MemoryMode
import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindAnalyzer
import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindFlow
import io.github.charlietap.chasm.compiler.backend.dex.analysis.analyzeFunction
import io.github.charlietap.chasm.compiler.backend.dex.analysis.estimateDexSize
import io.github.charlietap.chasm.compiler.backend.dex.analysis.functionFacts
import io.github.charlietap.chasm.compiler.backend.dex.lower.CHUNK_WINDOW_WORDS
import io.github.charlietap.chasm.compiler.backend.dex.lower.ENTRY_BITS
import io.github.charlietap.chasm.compiler.backend.dex.lower.Emitter
import io.github.charlietap.chasm.compiler.backend.dex.lower.HomeMismatch
import io.github.charlietap.chasm.compiler.backend.dex.lower.LowerContext
import io.github.charlietap.chasm.compiler.backend.dex.lower.RegisterHomes
import io.github.charlietap.chasm.compiler.backend.dex.lower.Registers
import io.github.charlietap.chasm.compiler.backend.dex.lower.SlotKindConflict
import io.github.charlietap.chasm.compiler.backend.dex.lower.Unsupported
import io.github.charlietap.chasm.compiler.backend.dex.lower.frameCost
import io.github.charlietap.chasm.compiler.backend.dex.lower.lowerDispatcher
import io.github.charlietap.chasm.compiler.backend.dex.lower.lowerFunctionBody
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexterFileFullException
import io.github.charlietap.dexter.DexterMethodTooLargeException
import io.github.charlietap.dexter.MemberAccess

internal typealias FunctionLowerer = (ModuleLowering, CirFunction, ClassBuilder, DexMethod) -> String?

internal fun FunctionLowerer(
    module: ModuleLowering,
    function: CirFunction,
    code: ClassBuilder,
    method: DexMethod,
): String? {
    return FunctionLowering(
        module = module,
        function = function,
        code = code,
        method = method,
        router = EstimateRouter(),
        slotKindAnalyzer = DataflowSlotKindAnalyzer(),
        chunkPlanner = ChunkPlanner(),
        chunkEmitter = ProbingChunkEmitter(module, function, code),
    ).lower()
}

internal class FunctionLowering(
    private val module: ModuleLowering,
    private val function: CirFunction,
    private val code: ClassBuilder,
    private val method: DexMethod,
    private val router: FunctionRouter,
    private val slotKindAnalyzer: SlotKindAnalyzer,
    private val chunkPlanner: ChunkPlanner,
    private val chunkEmitter: ChunkEmitter,
) {
    private val facts = functionFacts(module, function)

    fun lower(): String? {

        val whole = wholeFunctionAnalysis(module, function, facts)
        var reusable: RegisterHomes? = null
        var retained: Attempt.Retained? = null

        for (strategy in router.route(whole).strategies) {

            val attempt = when (strategy) {
                LoweringStrategy.WholeInOrder -> wholeInOrder(checkNotNull(whole).analysis)
                LoweringStrategy.Helper -> attempt(helperAnalysis(), null)
                LoweringStrategy.Inline -> attempt(checkNotNull(whole).analysis, null)
                LoweringStrategy.Split -> try {
                    split(whole, reusable)
                } catch (exception: DexterFileFullException) {
                    Attempt.FileFull(exception)
                }
            }

            when (attempt) {
                is Attempt.Lowered -> return null
                is Attempt.Rejected -> return attempt.reason
                is Attempt.TooLarge -> reusable = attempt.homes
                is Attempt.Retained -> {
                    if (retained == null) {
                        retained = attempt
                    }
                }
            }
        }

        return when (val failure = checkNotNull(retained) { "f${function.index.index}: no split attempt" }) {
            is Attempt.Refused -> failure.reason
            is Attempt.FileFull -> throw failure.exception
        }
    }

    private fun helperAnalysis(): FunctionAnalysis {
        return analyzeFunction(module, function, facts, MemoryMode.Helper)
    }

    private fun wholeInOrder(inline: FunctionAnalysis): Attempt {

        val first = attempt(inline, null)
        if (first !is Attempt.TooLarge || !first.inlinedAccesses) {
            return first
        }

        return attempt(helperAnalysis(), first.homes)
    }

    private fun attempt(
        analysis: FunctionAnalysis,
        reusable: RegisterHomes?,
    ): Attempt {

        val wide = functionRegisters(function, analysis, null)
        if (wide.count > MAX_REGISTERS) {
            return Attempt.TooLarge(inlinedAccesses = false, homes = reusable)
        }

        val firstTemporary = if (module.analysis.usesExceptions) facts.frameSlots else facts.firstTemporary
        val initialised = BooleanArray(facts.frameSlots) { slot -> slot < firstTemporary || facts.references.isReference[slot] }
        val pristine = reusable?.takeIf { homes -> homes.reusableFor(initialised) }
            ?: RegisterHomes(slotKindAnalyzer.analyze(function, facts, initialised), initialised)

        var current: RegisterHomes? = pristine
        while (true) {

            val homes = current
            val registers = if (homes != null) functionRegisters(function, analysis, homes) else wide
            if (registers.count >= ART_MAX_COMPILED_REGISTERS) {
                return Attempt.TooLarge(inlinedAccesses = false, homes = pristine)
            }

            val body = code.beginStaticMethod(method, registers.count, MemberAccess.Public)
            val context = LowerContext(module, function, analysis, registers, homes, Emitter(body, module.references))
            try {
                context.lowerFunctionBody()
                body.finish(HUGE_METHOD_CODE_UNITS)
                return Attempt.Lowered()
            } catch (conflict: SlotKindConflict) {
                body.abort()
                pin(checkNotNull(homes), conflict)
            } catch (mismatch: HomeMismatch) {
                body.abort()
                if (homes == null) {
                    throw mismatch
                }
                current = null
            } catch (unsupported: Unsupported) {
                body.abort()
                return Attempt.Rejected(unsupported.message ?: "unsupported")
            } catch (_: DexterMethodTooLargeException) {
                return Attempt.TooLarge(inlinedAccesses = analysis.usesInlineMemory, homes = pristine)
            }
        }
    }

    private fun split(
        whole: WholeFunctionAnalysis?,
        reusable: RegisterHomes?,
    ): Attempt {

        val analysis = whole?.analysis ?: analyzeFunction(module, function, facts, facts.memoryMode)
        if (chunkRegisters(function, analysis, null, null).count > MAX_REGISTERS) {
            return Attempt.Refused(FRAME_TOO_LARGE)
        }

        val starts = ArrayList<Int>()
        val orphans = ArrayList<Int>()
        val initialised = BooleanArray(facts.frameSlots) { true }
        val estimate = if (module.options.splitInstructions != null) null else whole?.estimate ?: estimateDexSize(module, function, analysis)
        val splitFacts = SplitFacts(function, facts, estimate)
        var current: RegisterHomes? = reusable?.takeIf { homes -> homes.reusableFor(initialised) }
            ?: RegisterHomes(slotKindAnalyzer.analyze(function, facts, initialised), initialised)

        while (true) {

            val homes = current
            val chunks = try {
                splitChunks(analysis, splitFacts, homes, starts, orphans.size)
            } catch (conflict: SlotKindConflict) {
                pin(checkNotNull(homes), conflict)
                orphans += starts
                starts.clear()
                continue
            } catch (mismatch: HomeMismatch) {
                if (homes == null) {
                    throw mismatch
                }
                current = null
                orphans += starts
                starts.clear()
                continue
            } catch (unsupported: Unsupported) {
                return Attempt.Refused(unsupported.message ?: "unsupported")
            }

            val table = dispatchTable(chunks, splitFacts)
            val registers = dispatcherRegisters(analysis)
            val slotFrameCost = (facts.frameSlots * 8 + 31) / 32
            val cost = frameCost(registers) + chunks.maxOf { chunk -> chunk.cost } + slotFrameCost
            val body = code.beginStaticMethod(method, registers.count, MemberAccess.Public)
            try {
                LowerContext(module, function, analysis, registers, null, Emitter(body, module.references))
                    .lowerDispatcher(chunks.map { chunk -> chunk.method }, table, cost)
            } catch (failure: Throwable) {
                body.abort()
                throw failure
            }

            try {
                body.finish(HUGE_METHOD_CODE_UNITS)
            } catch (_: DexterMethodTooLargeException) {
                return Attempt.Refused("function needs ${chunks.size} chunks, more than one dispatcher holds")
            }

            module.chunkStarts[function.index.index] = (orphans + starts).toIntArray()

            return Attempt.Lowered()
        }
    }

    private fun splitChunks(
        analysis: FunctionAnalysis,
        splitFacts: SplitFacts,
        homes: RegisterHomes?,
        starts: MutableList<Int>,
        orphans: Int,
    ): List<SplitChunk> {

        val size = function.instructions.size
        val owner = module.codeClass(function.index)
        val chunks = ArrayList<SplitChunk>()
        var window = module.options.splitInstructions
        var start = 0

        while (start < size) {

            val forced = window
            var limit = if (forced != null) {
                minOf(size, start + forced)
            } else {
                minOf(checkNotNull(splitFacts.estimate).reach(start, CHUNK_CODE_UNITS), start + CHUNK_INSTRUCTIONS)
            }

            while (true) {

                val planned = chunkPlanner.plan(function, splitFacts, start, limit)
                val chunkMethod = MethodRef(owner, chunkName(method.name, orphans + starts.size), CHUNK_PARAMETERS, "I")
                val chunk = chunkEmitter.emit(analysis, splitFacts, homes, planned, chunkMethod, splitFacts.ranked + starts.size + 1)
                if (chunk != null) {
                    starts += start
                    chunks += chunk
                    start = planned.end
                    break
                }

                if (planned.end - start == 1) {
                    throw Unsupported("instruction $start too large for a method")
                }

                limit = start + maxOf(1, (planned.end - start) / 2)
                if (window != null) {
                    window = limit - start
                }
            }
        }

        return chunks
    }

    private fun pin(
        homes: RegisterHomes,
        conflict: SlotKindConflict,
    ) {

        check(conflict.kind != SlotKindFlow.CONFLICT) {
            "f${function.index.index}: slot ${conflict.slot} is read at ${conflict.position} holding either a narrow or a wide value"
        }
        check(homes.pin(conflict.slot) == SlotKindFlow.UNDEFINED) { "f${function.index.index}: slot ${conflict.slot} conflicts after pinning" }

        homes.pinConflict(conflict.slot)
    }

    private sealed interface Attempt {

        class Lowered : Attempt

        data class TooLarge(
            val inlinedAccesses: Boolean,
            val homes: RegisterHomes?,
        ) : Attempt

        data class Rejected(
            val reason: String,
        ) : Attempt

        sealed interface Retained : Attempt

        data class Refused(
            val reason: String,
        ) : Retained

        data class FileFull(
            val exception: DexterFileFullException,
        ) : Retained
    }
}

private fun dispatchTable(
    chunks: List<SplitChunk>,
    splitFacts: SplitFacts,
): LongArray {

    val continuations = splitFacts.continuations
    val ranked = splitFacts.ranked
    val table = LongArray(ranked + chunks.size) { -1L }

    chunks.forEachIndexed { index, chunk ->

        chunk.entries.forEachIndexed { local, entry ->
            val id = continuations[entry]
            if (id >= 0) {
                table[id] = (index.toLong() shl ENTRY_BITS) or local.toLong()
            }
        }

        if (index > 0) {
            table[ranked + index] = index.toLong() shl ENTRY_BITS
        }
    }

    return table
}

private fun functionRegisters(
    function: CirFunction,
    analysis: FunctionAnalysis,
    homes: RegisterHomes?,
): Registers {

    val facts = analysis.facts
    val window = facts.window

    return Registers(
        frameSlots = facts.frameSlots,
        temporaries = window.temporaries,
        windowWords = window.windowWords,
        parameterWords = facts.signature.argumentWords,
        cachedConstants = analysis.cachedConstants.size,
        unsafeMemory = analysis.unsafeMemory,
        inlineMemory = analysis.usesInlineMemory,
        activation = facts.references.writesThrough,
        savesStack = function.exceptions != null,
        callBuffer = window.callBufferWords > 0,
        homes = homes,
    )
}

private const val FRAME_TOO_LARGE = "frame too large"

private fun dispatcherRegisters(analysis: FunctionAnalysis): Registers {
    return Registers(
        frameSlots = 0,
        temporaries = 0,
        windowWords = CHUNK_WINDOW_WORDS,
        parameterWords = analysis.facts.signature.argumentWords,
        cachedConstants = 0,
        unsafeMemory = false,
        inlineMemory = false,
        activation = false,
        savesStack = true,
        callBuffer = false,
        chunk = true,
    )
}

private val CHUNK_PARAMETERS = listOf(BINDING_TYPE, CONTEXT_TYPE, "I", "I", "I")
