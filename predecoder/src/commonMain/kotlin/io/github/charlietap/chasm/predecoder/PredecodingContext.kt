package io.github.charlietap.chasm.predecoder

import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.type.matching.TypeMatcherContext

data class InstructionCacheKey(
    val instruction: Instruction,
    val functionParamCount: Int,
    val functionResultCount: Int,
)

data class PredecodingContext(
    val instance: ModuleInstance,
    val store: Store,
    val instructionCache: HashMap<InstructionCacheKey, DispatchableInstruction>,
    val types: MutableList<DefinedType>,
    val functionParamCount: Int = 0,
    val functionResultCount: Int = 0,
) : TypeMatcherContext {
    override val lookup: DefinedTypeLookup = { index ->
        types.getOrNull(index)
    }

    private val interfaceSlots: Int
        get() = maxOf(functionParamCount, functionResultCount)

    fun localSlot(localIndex: Int): Int = if (localIndex < functionParamCount) {
        localIndex
    } else {
        interfaceSlots + (localIndex - functionParamCount)
    }

    fun instructionCacheKey(instruction: Instruction): InstructionCacheKey = InstructionCacheKey(
        instruction = instruction,
        functionParamCount = functionParamCount,
        functionResultCount = functionResultCount,
    )
}
