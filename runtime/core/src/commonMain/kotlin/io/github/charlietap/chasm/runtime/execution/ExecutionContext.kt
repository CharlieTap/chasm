package io.github.charlietap.chasm.runtime.execution

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.matching.TypeMatcherContext

data class ExecutionContext(
    val cstack: ControlStack,
    val vstack: ValueStack,
    val store: Store,
    val instance: ModuleInstance,
    val config: RuntimeConfig,
    val getInstructions: () -> Array<DispatchableInstruction>,
    val setInstructions: (Array<DispatchableInstruction>) -> Unit,
) : TypeMatcherContext {
    override val lookup: (Int) -> DefinedType? = { index ->
        instance.types.getOrNull(index)
    }
}
