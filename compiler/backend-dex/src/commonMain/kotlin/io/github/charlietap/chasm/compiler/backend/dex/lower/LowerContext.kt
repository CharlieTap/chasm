package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.InstructionIndex
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.References
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.innermostRegions
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowering
import io.github.charlietap.chasm.compiler.backend.dex.analysis.CachedConstant
import io.github.charlietap.chasm.compiler.backend.dex.analysis.FunctionAnalysis
import io.github.charlietap.chasm.type.FunctionType

internal class Unsupported(
    reason: String,
) : Exception(reason)

internal class LowerContext(
    val module: ModuleLowering,
    val function: CirFunction,
    val analysis: FunctionAnalysis,
    val registers: Registers,
    val slotKinds: SlotKinds,
    val emit: Emitter,
    val chunk: Chunk? = null,
) {
    val declarations: ModuleIndexSpaces get() = module.declarations
    val type: FunctionType get() = analysis.type
    val references: References get() = analysis.references
    val frameSlots: Int get() = analysis.frameSlots
    val parameterCount = type.params.types.size
    val resultCount = type.results.types.size

    val parameterDescriptors = type.params.types.map(::valueDescriptor)
    val parameterOffsets = IntArray(parameterCount).also { offsets ->
        var words = 0
        parameterDescriptors.forEachIndexed { index, descriptor ->
            offsets[index] = words
            words += descriptorWords(descriptor)
        }
    }

    val argumentArray = takesArgumentArray(type)

    val resultDescriptor = returnDescriptor(type)

    var position = SlotKinds.PROLOGUE

    private val innermostRegion: IntArray by lazy {
        innermostRegions(function.exceptions?.regions.orEmpty(), function.instructions.size)
    }

    fun regionAt(
        position: Int,
    ): Int = if (position < 0) -1 else innermostRegion[position]

    val cachedConstantIndex: Map<Resource, Int> = analysis.cachedConstants.withIndex()
        .mapNotNull { (index, constant) -> (constant as? CachedConstant.ObjectResource)?.let { cached -> cached.resource to index } }
        .toMap()

    val cachedValuesIndex: Int? = analysis.cachedConstants.indexOfFirst { constant -> constant is CachedConstant.Values }.takeIf { index -> index >= 0 }

    var cachesLoaded = false

    val sharedTraps = HashMap<MethodRef, String>()

    var publishesWrites = true

    var propagates = false

    val matchers = HashSet<Int>()

    val matcherOrder = ArrayList<Int>()

    val outOfLine = ArrayList<() -> Unit>()

    val bodyEnd: String get() = label(function.instructions.size)

    private val positionLabels = arrayOfNulls<String>(function.instructions.size + 1)

    fun label(
        position: Int,
    ): String {
        val chunk = chunk
        if (chunk != null && position !in chunk && !(position == chunk.end && position == function.instructions.size)) {
            chunk.exits += position
            return exitLabel(position)
        }
        return positionLabels[position] ?: "p$position".also { name -> positionLabels[position] = name }
    }

    fun line(
        position: Int,
    ): Int = position + 1 - (chunk?.start ?: 0)

    fun exitLabel(
        position: Int,
    ) = "x$position"

    fun label(
        position: InstructionIndex,
    ) = label(position.index)
}
