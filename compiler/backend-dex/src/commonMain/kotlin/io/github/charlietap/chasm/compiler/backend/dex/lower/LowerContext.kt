package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.InstructionIndex
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.References
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowering
import io.github.charlietap.chasm.compiler.backend.dex.analysis.FunctionAnalysis
import io.github.charlietap.chasm.compiler.backend.dex.analysis.FunctionFacts
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.dexter.Label
import io.github.charlietap.dexter.Labels

internal class Unsupported(
    reason: String,
) : Exception(reason)

internal class LowerContext(
    val module: ModuleLowering,
    val function: CirFunction,
    val analysis: FunctionAnalysis,
    val registers: Registers,
    homes: RegisterHomes?,
    val emit: Emitter,
    val chunk: Chunk? = null,
) {
    val declarations: ModuleIndexSpaces get() = module.declarations
    val facts: FunctionFacts get() = analysis.facts
    val type: FunctionType get() = facts.type
    val references: References get() = facts.references
    val frameSlots: Int get() = facts.frameSlots
    val parameterCount = type.params.types.size
    val resultCount = type.results.types.size

    val parameterDescriptors: List<String> get() = facts.signature.parameterDescriptors
    val parameterOffsets: IntArray get() = facts.signature.parameterOffsets

    val argumentArray: Boolean get() = facts.signature.argumentArray

    val resultDescriptor: String get() = facts.signature.resultDescriptor

    val kinds = SlotKindCursor(homes, facts.frameSlots)

    val roots = RootFrame(facts.references, registers, emit, chunk != null)

    val position: Int get() = kinds.position

    fun enterPrologue() {
        kinds.moveTo(SlotKindCursor.PROLOGUE)
        roots.enterPrologue()
    }

    fun enterInstruction(position: Int): Boolean {
        val reached = kinds.moveTo(position)
        roots.enterInstruction(position)
        return reached
    }

    fun enterColdCode() {
        roots.enterColdCode()
    }

    inline fun at(
        origin: Int,
        block: () -> Unit,
    ) {
        kinds.moveTo(origin)
        block()
    }

    fun regionAt(position: Int): Int {
        return if (position < 0) -1 else facts.table.innermostRegion(position)
    }

    val cachedConstantIndex: Map<Resource, Int> get() = analysis.cachedConstantIndex

    val cachedValuesIndex: Int? get() = analysis.cachedValuesIndex

    var cachesLoaded = false

    val sharedTrapLabels = HashMap<MethodRef, Label>()

    var propagates = false

    val matchers = HashSet<Int>()

    val matcherOrder = ArrayList<Int>()

    val outOfLine = ArrayList<() -> Unit>()

    val spillLabel = emit.freshLabel()

    val chunkEndLabel = emit.freshLabel()

    val propagationLabel = emit.freshLabel()

    val matcherLabels = emit.labels(function.exceptions?.regions?.size ?: 0)

    private val firstPosition = chunk?.start ?: 0

    private var positionLabels: Labels? = null

    private val exitLabels = HashMap<Int, Label>()

    val bodyEnd: Label get() = label(function.instructions.size)

    fun label(position: Int): Label {

        val chunk = chunk
        if (chunk != null && position !in chunk && !(position == chunk.end && position == function.instructions.size)) {
            chunk.exits += position
            return exitLabel(position)
        }

        var labels = positionLabels
        if (labels == null) {
            labels = emit.labels((chunk?.end ?: function.instructions.size) - firstPosition + 1)
            positionLabels = labels
        }

        return labels[position - firstPosition]
    }

    fun line(
        position: Int,
    ): Int = position + 1 - (chunk?.start ?: 0)

    fun exitLabel(position: Int): Label {
        return exitLabels.getOrPut(position) { emit.freshLabel() }
    }

    fun label(
        position: InstructionIndex,
    ) = label(position.index)
}
