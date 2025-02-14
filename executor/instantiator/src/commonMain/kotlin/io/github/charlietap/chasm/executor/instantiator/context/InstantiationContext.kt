package io.github.charlietap.chasm.executor.instantiator.context

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.instruction.LoadOp
import io.github.charlietap.chasm.executor.runtime.instruction.StoreOp
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.type.factory.DefinedTypeUnrollerFactory
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.type.matching.DefinedTypeReverseLookup
import io.github.charlietap.chasm.type.matching.TypeMatcherContext
import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeIndexToDefinedTypeSubstitutor

data class InstantiationContext(
    val config: RuntimeConfig,
    val store: Store,
    val module: Module,
    var instance: ModuleInstance? = null,
    val instructionCache: HashMap<Instruction, DispatchableInstruction> = hashMapOf(),
    val types: MutableList<DefinedType> = mutableListOf(),
    val unrollCache: HashMap<DefinedType, SubType> = hashMapOf(),
    val loadCache: HashMap<FusedOperand, LoadOp> = hashMapOf(),
    val storeCache: HashMap<FusedDestination, StoreOp> = hashMapOf(),
) : TypeMatcherContext {

    override val lookup: DefinedTypeLookup = { index ->
        types.getOrNull(index.idx.toInt())
    }

    override val reverseLookup: DefinedTypeReverseLookup = { type ->
        val index = types.indexOfFirst { definedType -> type == definedType }
        Index.TypeIndex(index.toUInt())
    }

    override val substitutor: ConcreteHeapTypeSubstitutor = TypeIndexToDefinedTypeSubstitutor(types)
    override val unroller: DefinedTypeUnroller = DefinedTypeUnrollerFactory(unrollCache)
}
