package io.github.charlietap.chasm.predecoder

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.instruction.LoadOp
import io.github.charlietap.chasm.executor.runtime.instruction.StoreOp
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.SubType
import io.github.charlietap.chasm.type.ir.factory.DefinedTypeUnrollerFactory
import io.github.charlietap.chasm.type.ir.matching.DefinedTypeLookup
import io.github.charlietap.chasm.type.ir.matching.DefinedTypeReverseLookup
import io.github.charlietap.chasm.type.ir.matching.TypeMatcherContext
import io.github.charlietap.chasm.type.ir.rolling.DefinedTypeUnroller
import io.github.charlietap.chasm.type.ir.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.type.ir.rolling.substitution.TypeIndexToDefinedTypeSubstitutor

data class PredecodingContext(
    val instance: ModuleInstance,
    val store: Store,
    val instructionCache: HashMap<Instruction, DispatchableInstruction>,
    val types: MutableList<DefinedType>,
    val unrollCache: HashMap<DefinedType, SubType>,
    val loadCache: HashMap<FusedOperand, LoadOp>,
    val storeCache: HashMap<FusedDestination, StoreOp>,
) : TypeMatcherContext {
    override val lookup: DefinedTypeLookup = { index ->
        types.getOrNull(index.idx)
    }

    override val reverseLookup: DefinedTypeReverseLookup = { type ->
        val index = types.indexOfFirst { definedType -> type == definedType }
        Index.TypeIndex(index)
    }

    override val substitutor: ConcreteHeapTypeSubstitutor = TypeIndexToDefinedTypeSubstitutor(types)
    override val unroller: DefinedTypeUnroller = DefinedTypeUnrollerFactory(unrollCache)
}
