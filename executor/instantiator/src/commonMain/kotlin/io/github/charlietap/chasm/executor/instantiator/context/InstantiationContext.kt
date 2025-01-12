package io.github.charlietap.chasm.executor.instantiator.context

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.type.matching.TypeMatcherContext
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor

data class InstantiationContext(
    val config: RuntimeConfig,
    val store: Store,
    val module: Module,
    var instance: ModuleInstance? = null,
    val instructionCache: HashMap<Instruction, DispatchableInstruction> = hashMapOf(),
    val types: MutableList<DefinedType> = mutableListOf(),
) : TypeMatcherContext {

    override fun lookup(): DefinedTypeLookup {
        return { index ->
            types.getOrNull(index.idx.toInt())
        }
    }

    override fun substitution(): ConcreteHeapTypeSubstitutor {
        val substitution: ConcreteHeapTypeSubstitutor = { concreteHeapType ->
            when (concreteHeapType) {
                is ConcreteHeapType.TypeIndex -> if (concreteHeapType.index.idx.toInt() < types.size) {
                    ConcreteHeapType.Defined(types[concreteHeapType.index.idx.toInt()])
                } else {
                    concreteHeapType
                }
                else -> concreteHeapType
            }
        }
        return substitution
    }
}
