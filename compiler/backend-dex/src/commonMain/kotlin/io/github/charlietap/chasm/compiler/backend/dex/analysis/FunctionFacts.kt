package io.github.charlietap.chasm.compiler.backend.dex.analysis

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.InstructionTable
import io.github.charlietap.chasm.compiler.backend.analysis.References
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeReferences
import io.github.charlietap.chasm.compiler.backend.analysis.frameFacts
import io.github.charlietap.chasm.compiler.backend.analysis.indirectCallType
import io.github.charlietap.chasm.compiler.backend.analysis.instructionTable
import io.github.charlietap.chasm.compiler.backend.analysis.isLeaf
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowering
import io.github.charlietap.chasm.compiler.backend.dex.abi.FunctionSignature
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

internal data class FunctionFacts(
    val table: InstructionTable,
    val type: FunctionType,
    val frameSlots: Int,
    val localTypes: Array<ValueType?>,
    val firstTemporary: Int,
    val initialValues: LongArray,
    val memoryMode: MemoryMode,
    val typedSignatures: Array<String?>,
    val callsDirectly: Boolean,
    val window: CallWindow,
    val references: References,
    val signature: FunctionSignature,
    val isLeaf: Boolean,
)

internal fun functionFacts(
    module: ModuleLowering,
    function: CirFunction,
): FunctionFacts {

    val declarations = module.declarations
    val frame = frameFacts(function, declarations)
    val type = frame.type
    val instructions = function.instructions
    val table = instructionTable(function, declarations)
    val inlineMemory = module.inlineMemory
    val memoryMode = when {
        inlineMemory == null || !table.accesses.usesMemory(0, instructions.size, inlineMemory.index) -> MemoryMode.Helper
        module.options.byteArrayBaseOffset != null -> MemoryMode.Unsafe
        else -> MemoryMode.ByteArray
    }

    val typedSignatures = Array(instructions.size) { position ->
        instructions[position].indirectCallType(declarations)?.let(module::signatureClass)
    }
    val callsDirectly = typedSignatures.any { signature -> signature != null } || instructions.any { instruction ->
        when (instruction) {
            is ControlInstruction.Call -> !declarations.isImported(instruction.function)
            else -> false
        }
    }

    return FunctionFacts(
        table = table,
        type = type,
        frameSlots = frame.frameSlots,
        localTypes = frame.localTypes,
        firstTemporary = frame.firstTemporary,
        initialValues = frame.initialValues,
        memoryMode = memoryMode,
        typedSignatures = typedSignatures,
        callsDirectly = callsDirectly,
        window = analyzeCallWindow(module, function, typedSignatures),
        references = analyzeReferences(table, function, type, frame.frameSlots, module.analysis),
        signature = FunctionSignature(type),
        isLeaf = function.isLeaf(),
    )
}
