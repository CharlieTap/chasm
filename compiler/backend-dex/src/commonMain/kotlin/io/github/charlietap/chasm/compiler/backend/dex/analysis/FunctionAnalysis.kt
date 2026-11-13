package io.github.charlietap.chasm.compiler.backend.dex.analysis

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TYPE_TEST
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.analysis.References
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeReferences
import io.github.charlietap.chasm.compiler.backend.analysis.form.HelperArgument
import io.github.charlietap.chasm.compiler.backend.analysis.form.access
import io.github.charlietap.chasm.compiler.backend.analysis.form.arrayCopy
import io.github.charlietap.chasm.compiler.backend.analysis.form.helperCall
import io.github.charlietap.chasm.compiler.backend.analysis.frameFacts
import io.github.charlietap.chasm.compiler.backend.analysis.indirectCallType
import io.github.charlietap.chasm.compiler.backend.analysis.instructionTable
import io.github.charlietap.chasm.compiler.backend.analysis.isLeaf
import io.github.charlietap.chasm.compiler.backend.analysis.redundantBoundsChecks
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowering
import io.github.charlietap.chasm.compiler.backend.dex.abi.INLINE_MEMORY_INSTRUCTIONS
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

internal sealed interface CachedConstant {

    class ObjectResource(
        val resource: Resource,
        val type: String,
    ) : CachedConstant

    class Values : CachedConstant
}

internal class FunctionAnalysis(
    val type: FunctionType,
    val frameSlots: Int,
    val localTypes: Array<ValueType?>,
    val firstTemporary: Int,
    val initialValues: LongArray,
    val usesInlineMemory: Boolean,
    val unsafeMemory: Boolean,
    val typedSignatures: Array<String?>,
    val callsDirectly: Boolean,
    val isLeaf: Boolean,
    val cachedConstants: List<CachedConstant>,
    val window: CallWindow,
    val references: References,
    val checkedAccesses: BooleanArray,
)

internal fun analyzeFunction(
    module: ModuleLowering,
    function: CirFunction,
    inlineAccesses: Boolean,
    split: Boolean = false,
): FunctionAnalysis {
    val declarations = module.declarations
    val frame = frameFacts(function, declarations)
    val type = frame.type
    val frameSlots = frame.frameSlots
    val instructions = function.instructions

    val inlineMemory = module.inlineMemory.takeIf { inlineAccesses && (split || instructions.size <= INLINE_MEMORY_INSTRUCTIONS) }
    val usesInlineMemory = inlineMemory != null &&
        instructions.any { instruction -> (instruction as? MemoryInstruction)?.access()?.memory == inlineMemory }
    val unsafeMemory = usesInlineMemory && module.options.byteArrayBaseOffset != null
    val typedSignatures = Array(instructions.size) { position ->
        instructions[position].indirectCallType(declarations)?.let(module::signatureClass)
    }
    val callsDirectly = typedSignatures.any { it != null } || instructions.any { instruction ->
        when (instruction) {
            is ControlInstruction.Call -> !declarations.isImported(instruction.function)
            else -> false
        }
    }
    return FunctionAnalysis(
        type = type,
        frameSlots = frameSlots,
        localTypes = frame.localTypes,
        firstTemporary = frame.firstTemporary,
        initialValues = frame.initialValues,
        usesInlineMemory = usesInlineMemory,
        unsafeMemory = unsafeMemory,
        typedSignatures = typedSignatures,
        callsDirectly = callsDirectly,
        isLeaf = function.isLeaf(),
        cachedConstants = cachedConstants(function, if (usesInlineMemory) inlineMemory else null),
        window = analyzeCallWindow(module, function, typedSignatures),
        references = analyzeReferences(function, type, frameSlots, declarations, module.analysis),
        checkedAccesses = if (usesInlineMemory) {
            function.redundantBoundsChecks(instructionTable(function, declarations))
        } else {
            BooleanArray(instructions.size)
        },
    )
}

private fun cachedConstants(
    function: CirFunction,
    inlineMemory: MemoryIndex?,
): List<CachedConstant> {
    val uses = LinkedHashMap<Resource, ResourceUse>()
    var valueLoads = 0

    fun use(
        resource: Resource,
        type: String,
        times: Int = 1,
    ) {
        uses.getOrPut(resource) { ResourceUse(type) }.count += times
    }
    for (instruction in function.instructions) {
        when (instruction) {
            is VariableInstruction.GlobalGetS -> use(Resource.Global(instruction.global.index), GLOBAL_INSTANCE)
            is VariableInstruction.GlobalSetI -> use(Resource.Global(instruction.global.index), GLOBAL_INSTANCE)
            is VariableInstruction.GlobalSetS -> use(Resource.Global(instruction.global.index), GLOBAL_INSTANCE)
            is MemoryInstruction -> instruction.access()?.memory?.takeIf { it != inlineMemory }?.let { use(Resource.Memory(it.index), MEMORY_INSTANCE) }
            is ControlInstruction.CallIndirectI -> {
                use(Resource.Table(instruction.table.index), TABLE_INSTANCE)
                valueLoads++
            }
            is ControlInstruction.CallIndirectS -> {
                use(Resource.Table(instruction.table.index), TABLE_INSTANCE)
                valueLoads++
            }
            is ControlInstruction.ReturnCallIndirectI -> {
                use(Resource.Table(instruction.table.index), TABLE_INSTANCE)
                valueLoads++
            }
            is ControlInstruction.ReturnCallIndirectS -> {
                use(Resource.Table(instruction.table.index), TABLE_INSTANCE)
                valueLoads++
            }
            is ControlInstruction.Throw -> valueLoads++
            is ReferenceInstruction.RefFuncS -> valueLoads++
            is ReferenceInstruction.RefCastS -> use(Resource.TypeTest(instruction.typeTest), REFERENCE_TYPE_TEST)
            is ReferenceInstruction.RefTestS -> use(Resource.TypeTest(instruction.typeTest), REFERENCE_TYPE_TEST)
            is AggregateInstruction.RefCastStructGetS -> use(Resource.TypeTest(instruction.typeTest), REFERENCE_TYPE_TEST)
            is AggregateInstruction.StructNewS,
            is AggregateInstruction.StructNewDefaultS,
            is AggregateInstruction.ArrayNewDefaultI,
            is AggregateInstruction.ArrayNewDefaultS,
            is AggregateInstruction.ArrayNewFixedS,
            -> valueLoads++
            is AggregateInstruction -> valueLoads += instruction.helperCall()?.arguments?.count { it is HelperArgument.RuntimeType } ?: 0
            else -> Unit
        }
    }
    if (inlineMemory != null && function.instructions.any { it is ControlInstruction }) {
        use(Resource.Memory(inlineMemory.index), MEMORY_INSTANCE, times = CACHE_THRESHOLD)
    }
    val constants = uses.filterValues { use -> use.count >= CACHE_THRESHOLD }
        .map { (resource, use) -> CachedConstant.ObjectResource(resource, use.type) }
    return if (valueLoads >= CACHE_THRESHOLD) constants + CachedConstant.Values() else constants
}

private class ResourceUse(
    val type: String,
) {
    var count = 0
}

private const val CACHE_THRESHOLD = 2

internal fun Instruction.isArrayCopy() = (this as? AggregateInstruction)?.arrayCopy() != null
