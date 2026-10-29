package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.InstructionIndex
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.compiler.backend.analysis.FrameFacts
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.References
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.innermostRegions
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowering
import io.github.charlietap.chasm.type.FunctionType
import org.objectweb.asm.Label

internal class Unsupported(
    reason: String,
) : Exception(reason)

internal class FunctionFacts(
    val module: ModuleLowering,
    val function: CirFunction,
    val frame: FrameFacts,
    val references: References,
    val checkedAccesses: BooleanArray,
) {
    val innermostRegion = innermostRegions(function.exceptions?.regions.orEmpty(), function.instructions.size)

    val resources = ArrayList<Resource>()
    private val resourceIds = HashMap<Resource, Int>()

    fun resourceId(
        resource: Resource,
    ): Int = resourceIds.getOrPut(resource) { resources.size.also { resources += resource } }
}

internal class LowerContext(
    val facts: FunctionFacts,
    val slotKinds: SlotKinds,
    val locals: Locals,
    val emit: Emitter,
    val chunk: Chunk? = null,
) {
    val module: ModuleLowering get() = facts.module
    val function: CirFunction get() = facts.function
    val frame: FrameFacts get() = facts.frame
    val references: References get() = facts.references
    val checkedAccesses: BooleanArray get() = facts.checkedAccesses
    val declarations: ModuleIndexSpaces get() = module.declarations
    val type: FunctionType get() = frame.type
    val parameterCount = type.params.types.size
    val resultCount = type.results.types.size
    val parameterDescriptors = type.params.types.map(::valueDescriptor)

    val argumentArray = takesArgumentArray(type)

    val resultDescriptor = returnDescriptor(type)

    var position = SlotKinds.PROLOGUE

    private val firstPosition = chunk?.start ?: 0

    private val labels = arrayOfNulls<Label>((chunk?.end ?: function.instructions.size) - firstPosition + 1)

    val outOfLine = ArrayList<() -> Unit>()

    var outOfBoundsLabel: Label? = null

    val matchers = HashMap<Int, Label>()
    val matcherOrder = ArrayList<Int>()
    var propagationLabel: Label? = null

    fun regionAt(
        position: Int,
    ): Int = if (position < 0) -1 else facts.innermostRegion[position]

    fun label(
        position: Int,
    ): Label {
        val chunk = chunk ?: return placement(position)
        return if (position in chunk) placement(position) else chunk.exit(position)
    }

    fun label(
        position: InstructionIndex,
    ): Label = label(position.index)

    fun placement(
        position: Int,
    ): Label {
        val index = position - firstPosition
        return labels[index] ?: Label().also { label -> labels[index] = label }
    }

    val bodyEnd: Label get() = placement(function.instructions.size)
}
