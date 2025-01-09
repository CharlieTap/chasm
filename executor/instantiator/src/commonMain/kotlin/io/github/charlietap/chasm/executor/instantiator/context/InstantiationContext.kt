package io.github.charlietap.chasm.executor.instantiator.context

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.type.matching.TypeMatcherContext
import io.github.charlietap.chasm.type.rolling.DefinedTypeRoller
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.DefinedTypeSubstitutor

data class InstantiationContext(
    val config: RuntimeConfig,
    val store: Store,
    val module: Module,
    var instance: ModuleInstance? = null,
) : TypeMatcherContext {

    val types by lazy {
        try {
            module.types.map(Type::recursiveType).fold(mutableListOf<DefinedType>()) { acc, recursiveType ->
                val size = acc.size

                val substitutor: ConcreteHeapTypeSubstitutor = { heapType ->
                    when (heapType) {
                        is ConcreteHeapType.TypeIndex -> {
                            ConcreteHeapType.Defined(acc[heapType.index.idx.toInt()])
                        }
                        else -> heapType
                    }
                }

                acc.apply {
                    addAll(
                        DefinedTypeRoller(size, recursiveType).map { definedType ->
                            DefinedTypeSubstitutor(definedType, substitutor)
                        },
                    )
                }
            }
        } catch (_: IndexOutOfBoundsException) {
            emptyList()
        }
    }

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
