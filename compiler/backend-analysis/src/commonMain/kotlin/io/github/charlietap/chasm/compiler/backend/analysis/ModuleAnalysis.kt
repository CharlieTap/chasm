package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.MAX_PARAMETERS
import io.github.charlietap.chasm.compiler.backend.abi.signatureKey
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

class ModuleAnalysis(
    val signatureTypes: Map<String, FunctionType>,
    val hasTailCalls: Boolean,
    val usesExceptions: Boolean,
    val mayGrowMemory: BooleanArray,
    val mayUnwind: BooleanArray,
    val requestsTailCall: BooleanArray,
    val bridgedFunctions: FunctionCensus,
    val referencedFunctions: FunctionCensus,
)

class FunctionCensus(
    included: BooleanArray,
) {

    private val ordinals = IntArray(included.size) { NOT_INCLUDED }

    val count: Int

    init {
        var next = 0
        for (function in included.indices) {
            if (included[function]) ordinals[function] = next++
        }
        count = next
    }

    fun ordinal(function: Int): Int {
        return ordinals[function]
    }

    companion object {
        const val NOT_INCLUDED = -1
    }
}

fun analyzeModule(
    code: CirModule,
    declarations: ModuleIndexSpaces,
    referenceDescriptor: String = "J",
): ModuleAnalysis {
    return analyzeModule(code, declarations, code.functions.map { function -> functionSummary(function, declarations) }, referenceDescriptor)
}

fun analyzeModule(
    code: CirModule,
    declarations: ModuleIndexSpaces,
    summaries: List<FunctionSummary>,
    referenceDescriptor: String = "J",
): ModuleAnalysis {

    val signatureTypes = LinkedHashMap<String, FunctionType>()
    val seenTypes = BooleanArray(declarations.typeCount)
    val bridged = BooleanArray(declarations.functionCount)
    val referenced = BooleanArray(declarations.functionCount)
    for (summary in summaries) {
        for (function in summary.bridgedFunctions) bridged[function] = true
        for (function in summary.referencedFunctions) referenced[function] = true
        for (typeIndex in summary.indirectCallTypes) {
            if (seenTypes[typeIndex]) {
                continue
            }
            seenTypes[typeIndex] = true
            val type = declarations.functionType(TypeIndex(typeIndex)).takeIf { it.params.types.size <= MAX_PARAMETERS } ?: continue
            signatureTypes.putIfAbsent(signatureKey(type, referenceDescriptor), type)
        }
    }

    val effects = CallEffects(code, declarations, summaries)
    return ModuleAnalysis(
        signatureTypes = signatureTypes,
        hasTailCalls = effects.requestsTailCall.any { requests -> requests },
        usesExceptions = summaries.any(FunctionSummary::usesExceptions),
        mayGrowMemory = effects.mayGrowMemory,
        mayUnwind = effects.mayUnwind,
        requestsTailCall = effects.requestsTailCall,
        bridgedFunctions = FunctionCensus(bridged),
        referencedFunctions = FunctionCensus(referenced),
    )
}

fun Instruction.indirectCallTypeIndex(): TypeIndex? {
    return when (this) {
        is ControlInstruction.CallIndirectI -> type
        is ControlInstruction.CallIndirectS -> type
        is ControlInstruction.CallRefS -> type
        else -> null
    }
}

fun Instruction.isTailCall(): Boolean = this is ControlInstruction.ReturnCall ||
    this is ControlInstruction.ReturnCallIndirectI || this is ControlInstruction.ReturnCallIndirectS ||
    this is ControlInstruction.ReturnCallRefS

/** Whether [this] is a tail call by [function] to itself outside any try region, [region], which lowers to a jump. */
fun Instruction.isSelfTailCall(
    function: CirFunction,
    region: Int,
): Boolean = this is ControlInstruction.ReturnCall && this.function == function.index && region < 0

internal fun Instruction.isCall(): Boolean {
    return isTailCall() || this is ControlInstruction.Call ||
        this is ControlInstruction.CallIndirectI || this is ControlInstruction.CallIndirectS ||
        this is ControlInstruction.CallRefS
}

fun CirFunction.isLeaf(): Boolean {
    return instructions.none(Instruction::isCall)
}

fun Instruction.raisesException(): Boolean = this is ControlInstruction.Throw || this is ControlInstruction.ThrowRefS

fun collectable(
    type: ValueType,
): Boolean = type is ValueType.Reference && collectable(type.referenceType)

private fun collectable(
    type: ReferenceType,
): Boolean =
    type.heapType != AbstractHeapType.Func && type.heapType != AbstractHeapType.NoFunc
