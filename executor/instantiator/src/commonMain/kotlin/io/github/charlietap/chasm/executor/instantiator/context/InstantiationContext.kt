package io.github.charlietap.chasm.executor.instantiator.context

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.predecoder.InstructionCacheKey
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.RTT

data class InstantiationContext(
    val config: RuntimeConfig,
    val store: Store,
    val module: Module,
    val runtimeTypes: List<RTT>,
    var instance: ModuleInstance? = null,
    val instructionCache: HashMap<InstructionCacheKey, DispatchableInstruction> = hashMapOf(),
)
