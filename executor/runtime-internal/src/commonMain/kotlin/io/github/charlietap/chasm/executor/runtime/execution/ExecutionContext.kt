package io.github.charlietap.chasm.executor.runtime.execution

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.matching.TypeMatcherContext
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor

data class ExecutionContext(
    val stack: Stack,
    val store: Store,
    val instance: ModuleInstance,
) : TypeMatcherContext {

    override fun lookup(): (Index.TypeIndex) -> DefinedType? = { index ->
        instance.types.getOrNull(index.idx.toInt())
    }

    override fun substitution(): (ConcreteHeapType) -> ConcreteHeapType {
        val types = instance.types
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
