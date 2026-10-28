package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.config.StoreConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class CompilerBaseline(
    val schemaVersion: Int,
    val modules: List<CompilerBaselineModule>,
)

@Serializable
data class CompilerBaselineModule(
    val name: String,
    val functions: List<CompilerBaselineFunction>,
)

@Serializable
data class CompilerBaselineFunction(
    val index: Int,
    val name: String,
    val frameSlots: Int,
    val activationHeaderSlot: Int,
    val localInitialization: JsonElement,
    val unlinkedCheckpoints: List<Int>? = null,
    val exceptions: JsonObject? = null,
    val instructions: List<CompilerBaselineInstruction>,
)

@Serializable
data class CompilerBaselineInstruction(
    val offset: Int,
    val position: Int,
    val dispatcher: String,
    val variant: Int? = null,
    val fields: JsonObject? = null,
)

data class CompilerBaselineFixture(
    val name: String,
    val bytes: ByteArray,
)

data class CompilerBaselineStore(
    val suffix: String?,
    val config: StoreConfig,
) {
    companion object {
        val DEFAULT = CompilerBaselineStore(suffix = null, config = StoreConfig())

        val CHECKPOINTS = CompilerBaselineStore(suffix = null, config = StoreConfig(meterFuel = true, interruptible = true))

        val ALL = listOf(
            CompilerBaselineStore("default", StoreConfig()),
            CompilerBaselineStore("fuel", StoreConfig(meterFuel = true)),
            CompilerBaselineStore("interrupt", StoreConfig(interruptible = true)),
            CompilerBaselineStore("fuel_interrupt", StoreConfig(meterFuel = true, interruptible = true)),
        )
    }
}
