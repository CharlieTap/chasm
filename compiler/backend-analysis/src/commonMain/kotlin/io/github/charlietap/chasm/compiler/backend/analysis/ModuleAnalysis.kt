package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.MAX_PARAMETERS
import io.github.charlietap.chasm.compiler.backend.abi.signatureKey
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ValueType

class ModuleAnalysis(
    val signatureTypes: Map<String, FunctionType>,
    val hasTailCalls: Boolean,
    val usesExceptions: Boolean,
    val mayGrowMemory: BooleanArray,
    val mayCollect: BooleanArray,
    val mayUnwind: BooleanArray,
    val requestsTailCall: BooleanArray,
    val publishesRegisters: Boolean,
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
    module: Module,
    code: CirModule,
    declarations: ModuleIndexSpaces,
): ModuleAnalysis {
    return analyzeModule(module, code, declarations, code.functions.map { function -> functionSummary(function, declarations) })
}

fun analyzeModule(
    module: Module,
    code: CirModule,
    declarations: ModuleIndexSpaces,
    summaries: List<FunctionSummary>,
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
            signatureTypes.putIfAbsent(signatureKey(type), type)
        }
    }

    val effects = CallEffects(code, declarations, summaries)
    return ModuleAnalysis(
        signatureTypes = signatureTypes,
        hasTailCalls = effects.requestsTailCall.any { requests -> requests },
        usesExceptions = summaries.any(FunctionSummary::usesExceptions),
        mayGrowMemory = effects.mayGrowMemory,
        mayCollect = effects.mayCollect,
        mayUnwind = effects.mayUnwind,
        requestsTailCall = effects.requestsTailCall,
        publishesRegisters = module.mayHoldCollectableReferences() || summaries.any(FunctionSummary::catchesExceptionReference),
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

internal fun Instruction.isCall(): Boolean {
    return isTailCall() || this is ControlInstruction.Call ||
        this is ControlInstruction.CallIndirectI || this is ControlInstruction.CallIndirectS ||
        this is ControlInstruction.CallRefS
}

fun CirFunction.isLeaf(): Boolean {
    return instructions.none(Instruction::isCall)
}

fun Instruction.raisesException(): Boolean = this is ControlInstruction.Throw || this is ControlInstruction.ThrowRefS

fun Module.mayHoldCollectableReferences(): Boolean {
    val compositeTypes = definedTypes.map { type -> type.asSubType.compositeType }
    if (compositeTypes.any { it !is CompositeType.Function }) return true
    val signatures = compositeTypes.filterIsInstance<CompositeType.Function>().flatMap { function ->
        function.functionType.params.types + function.functionType.results.types
    }
    val locals = functions.flatMap { function -> function.locals.map { it.type } }
    val globals = globals.map { it.type.valueType } + imports.mapNotNull { (it.descriptor as? Import.Descriptor.Global)?.type?.valueType }
    val tables = tables.map { it.type.referenceType } + imports.mapNotNull { (it.descriptor as? Import.Descriptor.Table)?.type?.referenceType }
    return (signatures + locals + globals).any(::collectable) || tables.any(::collectable) || tags.isNotEmpty()
}

fun collectable(
    type: ValueType,
): Boolean = type is ValueType.Reference && collectable(type.referenceType)

private fun collectable(
    type: ReferenceType,
): Boolean =
    type.heapType != AbstractHeapType.Func && type.heapType != AbstractHeapType.NoFunc
