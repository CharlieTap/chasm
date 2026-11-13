package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
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
)

fun analyzeModule(
    module: Module,
    code: CirModule,
    declarations: ModuleIndexSpaces,
): ModuleAnalysis {
    val signatureTypes = LinkedHashMap<String, FunctionType>()
    for (function in code.functions) {
        for (instruction in function.instructions) {
            val type = instruction.indirectCallType(declarations)?.takeIf { it.params.types.size <= MAX_PARAMETERS } ?: continue
            signatureTypes.putIfAbsent(signatureKey(type), type)
        }
    }
    val effects = CallEffects(code, declarations)
    return ModuleAnalysis(
        signatureTypes = signatureTypes,
        hasTailCalls = effects.requestsTailCall.any { requests -> requests },
        usesExceptions = code.functions.any { function ->
            function.exceptions != null || function.instructions.any(Instruction::raisesException)
        },
        mayGrowMemory = effects.mayGrowMemory,
        mayCollect = effects.mayCollect,
        mayUnwind = effects.mayUnwind,
        requestsTailCall = effects.requestsTailCall,
        publishesRegisters = module.mayHoldCollectableReferences() || code.functions.any(CirFunction::catchesExceptionReference),
    )
}

private fun CirFunction.catchesExceptionReference(): Boolean =
    exceptions?.regions.orEmpty().any { region -> region.catches.any { handler -> handler.exceptionReference } }

fun Instruction.indirectCallType(
    declarations: ModuleIndexSpaces,
): FunctionType? = when (this) {
    is ControlInstruction.CallIndirectI -> declarations.functionType(type)
    is ControlInstruction.CallIndirectS -> declarations.functionType(type)
    is ControlInstruction.CallRefS -> declarations.functionType(type)
    else -> null
}

fun Instruction.isTailCall(): Boolean = this is ControlInstruction.ReturnCall ||
    this is ControlInstruction.ReturnCallIndirectI || this is ControlInstruction.ReturnCallIndirectS ||
    this is ControlInstruction.ReturnCallRefS

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
