package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiled.format.ArtifactTarget
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.format.UNSET_LONG
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.RuntimeConfig

@InternalChasmApi
sealed interface ModuleSetting {

    val name: String

    data class IntSetting(
        override val name: String,
        val value: Int,
    ) : ModuleSetting

    data class LongSetting(
        override val name: String,
        val value: Long,
    ) : ModuleSetting

    data class BooleanSetting(
        override val name: String,
        val value: Boolean,
    ) : ModuleSetting
}

@InternalChasmApi
class ModuleSettings(
    private val runtime: RuntimeConfig,
    private val target: ArtifactTarget,
    private val byteArrayBaseOffset: Long,
) {

    fun constants(): List<ModuleSetting> {

        val memory = runtime.limits.memory

        return listOf(
            ModuleSetting.IntSetting("runtimeAbi", COMPILED_RUNTIME_ABI),
            ModuleSetting.IntSetting("target", target.code),
            ModuleSetting.LongSetting("byteArrayBaseOffset", byteArrayBaseOffset),
            ModuleSetting.BooleanSetting("interrupts", runtime.interrupts),
            ModuleSetting.BooleanSetting("stacktraces", runtime.stacktraces),
            ModuleSetting.LongSetting("fuel", (runtime.limits.cpu as? CpuLimits.Metered)?.fuel?.units ?: UNSET_LONG),
            ModuleSetting.LongSetting("linearMemoryBytes", memory.linearMemory?.value ?: UNSET_LONG),
            ModuleSetting.LongSetting("gcHeapBytes", memory.gcHeap?.value ?: UNSET_LONG),
            ModuleSetting.LongSetting("tableBytes", memory.tables?.value ?: UNSET_LONG),
            ModuleSetting.LongSetting("stackBytes", memory.stack?.value ?: UNSET_LONG),
            ModuleSetting.BooleanSetting("trapOnGrowthFailure", memory.onGrowthFailure == GrowthFailureStrategy.Trap),
        )
    }
}
