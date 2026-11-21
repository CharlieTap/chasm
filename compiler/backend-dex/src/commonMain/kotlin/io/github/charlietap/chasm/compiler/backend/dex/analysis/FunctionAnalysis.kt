package io.github.charlietap.chasm.compiler.backend.dex.analysis

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.GlobalIndex
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_GLOBAL
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TABLE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TYPE_TEST
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.InstructionTable
import io.github.charlietap.chasm.compiler.backend.analysis.form.HelperArgument
import io.github.charlietap.chasm.compiler.backend.analysis.form.arrayCopy
import io.github.charlietap.chasm.compiler.backend.analysis.redundantBoundsChecks
import io.github.charlietap.chasm.compiler.backend.dex.ModuleLowering
import io.github.charlietap.chasm.type.ValueType

internal sealed interface CachedConstant {

    class ObjectResource(
        val resource: Resource,
        val type: String,
    ) : CachedConstant

    class Values : CachedConstant
}

internal enum class MemoryMode {
    Helper,
    ByteArray,
    Unsafe,
}

internal class FunctionAnalysis(
    val facts: FunctionFacts,
    val memoryMode: MemoryMode,
    val cachedConstants: List<CachedConstant>,
    val checkedAccesses: BooleanArray,
) {
    val usesInlineMemory = memoryMode != MemoryMode.Helper

    val unsafeMemory = memoryMode == MemoryMode.Unsafe

    val cachedConstantIndex: Map<Resource, Int> = cachedConstants.withIndex()
        .mapNotNull { (index, constant) -> (constant as? CachedConstant.ObjectResource)?.let { cached -> cached.resource to index } }
        .toMap()

    val cachedValuesIndex: Int? = cachedConstants.indexOfFirst { constant -> constant is CachedConstant.Values }.takeIf { index -> index >= 0 }
}

internal fun analyzeFunction(
    module: ModuleLowering,
    function: CirFunction,
    facts: FunctionFacts,
    memoryMode: MemoryMode,
): FunctionAnalysis {

    val usesInlineMemory = memoryMode != MemoryMode.Helper

    return FunctionAnalysis(
        facts = facts,
        memoryMode = memoryMode,
        cachedConstants = cachedConstants(module, function, facts.table, module.inlineMemory.takeIf { usesInlineMemory }),
        checkedAccesses = if (usesInlineMemory) {
            function.redundantBoundsChecks(facts.table, module.workspace.boundsChecks)
        } else {
            module.workspace.noRedundantChecks(function.instructions.size)
        },
    )
}

private fun cachedConstants(
    module: ModuleLowering,
    function: CirFunction,
    table: InstructionTable,
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

    fun useGlobal(
        global: Int,
    ) {
        if (module.declarations.globalType(GlobalIndex(global)) is ValueType.Reference) {
            use(Resource.ReferenceGlobal(global), REFERENCE_GLOBAL)
        } else {
            use(Resource.Global(global), GLOBAL_INSTANCE)
        }
    }

    fun useTable(
        table: Int,
    ) {
        use(Resource.ReferenceTable(table), REFERENCE_TABLE)
    }

    for (position in function.instructions.indices) {
        when (val instruction = function.instructions[position]) {
            is VariableInstruction.GlobalGetS -> useGlobal(instruction.global.index)
            is VariableInstruction.GlobalSetI -> useGlobal(instruction.global.index)
            is VariableInstruction.GlobalSetS -> useGlobal(instruction.global.index)
            is MemoryInstruction -> {
                val access = table.accesses.accessIndexAt(position)
                val memory = if (access >= 0) table.accesses.memories[access] else -1
                if (memory >= 0 && (inlineMemory == null || memory != inlineMemory.index)) {
                    use(Resource.Memory(memory), MEMORY_INSTANCE)
                }
            }
            is ControlInstruction.CallIndirectI -> {
                useTable(instruction.table.index)
                valueLoads++
            }
            is ControlInstruction.CallIndirectS -> {
                useTable(instruction.table.index)
                valueLoads++
            }
            is ControlInstruction.ReturnCallIndirectI -> {
                useTable(instruction.table.index)
                valueLoads++
            }
            is ControlInstruction.ReturnCallIndirectS -> {
                useTable(instruction.table.index)
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
            is AggregateInstruction -> valueLoads += table.forms.helperCall(position)?.arguments?.count { it is HelperArgument.RuntimeType } ?: 0
            else -> Unit
        }
    }

    if (inlineMemory != null && function.reloadsMemory(module, table)) {
        use(Resource.Memory(inlineMemory.index), MEMORY_INSTANCE, times = CACHE_THRESHOLD)
    }

    val constants = uses.filterValues { use -> use.count >= CACHE_THRESHOLD }
        .map { (resource, use) -> CachedConstant.ObjectResource(resource, use.type) }

    return if (valueLoads >= CACHE_THRESHOLD) constants + CachedConstant.Values() else constants
}

private fun CirFunction.reloadsMemory(
    module: ModuleLowering,
    table: InstructionTable,
): Boolean {
    return exceptions != null || instructions.indices.any { position ->
        when (val instruction = instructions[position]) {
            is ControlInstruction.Call ->
                module.declarations.isImported(instruction.function) || module.analysis.mayGrowMemory[instruction.function.index]
            is ControlInstruction.CallIndirectI, is ControlInstruction.CallIndirectS, is ControlInstruction.CallRefS -> true
            is MemoryInstruction -> table.accesses.accessIndexAt(position) < 0
            else -> false
        }
    }
}

private class ResourceUse(
    val type: String,
) {
    var count = 0
}

private const val CACHE_THRESHOLD = 2

internal fun Instruction.isArrayCopy() = (this as? AggregateInstruction)?.arrayCopy() != null
