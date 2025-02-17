package io.github.charlietap.chasm.executor.runtime.execution

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.stack.ControlStack
import io.github.charlietap.chasm.executor.runtime.stack.ValueStack
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.SubType
import io.github.charlietap.chasm.type.ir.factory.DefinedTypeUnrollerFactory
import io.github.charlietap.chasm.type.ir.matching.DefinedTypeReverseLookup
import io.github.charlietap.chasm.type.ir.matching.TypeMatcherContext
import io.github.charlietap.chasm.type.ir.rolling.DefinedTypeUnroller
import io.github.charlietap.chasm.type.ir.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.type.ir.rolling.substitution.TypeIndexToDefinedTypeSubstitutor

data class ExecutionContext(
    val cstack: ControlStack,
    val vstack: ValueStack,
    val store: Store,
    val instance: ModuleInstance,
    val config: RuntimeConfig,
    val unrollCache: HashMap<DefinedType, SubType> = hashMapOf(),
) : TypeMatcherContext {

    override val lookup: (Index.TypeIndex) -> DefinedType? = { index ->
        instance.types.getOrNull(index.idx)
    }

    override val reverseLookup: DefinedTypeReverseLookup
        get() = TODO("Not yet implemented")

    override val substitutor: ConcreteHeapTypeSubstitutor = TypeIndexToDefinedTypeSubstitutor(instance.types)
    override val unroller: DefinedTypeUnroller = DefinedTypeUnrollerFactory(unrollCache)
}
