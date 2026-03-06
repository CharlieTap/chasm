package io.github.charlietap.chasm.executor.instantiator.context

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.predecoder.InstructionCacheKey
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.type.matching.TypeMatcherContext

data class InstantiationContext(
    val config: RuntimeConfig,
    val store: Store,
    val module: Module,
    var instance: ModuleInstance? = null,
    val instructionCache: HashMap<InstructionCacheKey, DispatchableInstruction> = hashMapOf(),
    val types: MutableList<DefinedType> = mutableListOf(),
) : TypeMatcherContext {

    override val lookup: DefinedTypeLookup = { index ->
        types.getOrNull(index)
    }
}
