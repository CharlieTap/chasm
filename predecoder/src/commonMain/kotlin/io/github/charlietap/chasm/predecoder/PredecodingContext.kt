package io.github.charlietap.chasm.predecoder

import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.LoadOp
import io.github.charlietap.chasm.runtime.instruction.StoreOp
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.type.matching.TypeMatcherContext

data class PredecodingContext(
    val instance: ModuleInstance,
    val store: Store,
    val instructionCache: HashMap<Instruction, DispatchableInstruction>,
    val types: MutableList<DefinedType>,
    val loadCache: HashMap<FusedOperand, LoadOp>,
    val storeCache: HashMap<FusedDestination, StoreOp>,
) : TypeMatcherContext {
    override val lookup: DefinedTypeLookup = { index ->
        types.getOrNull(index)
    }
}
