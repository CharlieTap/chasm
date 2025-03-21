package io.github.charlietap.chasm.runtime.execution

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.factory.DefinedTypeUnrollerFactory
import io.github.charlietap.chasm.type.matching.DefinedTypeReverseLookup
import io.github.charlietap.chasm.type.matching.TypeMatcherContext
import io.github.charlietap.chasm.type.rolling.DefinedTypeUnroller
import io.github.charlietap.chasm.type.rolling.substitution.Substitution

data class ExecutionContext(
    val cstack: ControlStack,
    val vstack: ValueStack,
    val store: Store,
    val instance: ModuleInstance,
    val config: RuntimeConfig,
    val unrollCache: HashMap<DefinedType, SubType> = hashMapOf(),
) : TypeMatcherContext {

    override val lookup: (Int) -> DefinedType? = { index ->
        instance.types.getOrNull(index)
    }

    override val reverseLookup: DefinedTypeReverseLookup
        get() = TODO("Not yet implemented")

    override val substitution: Substitution.TypeIndexToDefinedType = Substitution.TypeIndexToDefinedType(instance.types)
    override val unroller: DefinedTypeUnroller = DefinedTypeUnrollerFactory(unrollCache)
}
