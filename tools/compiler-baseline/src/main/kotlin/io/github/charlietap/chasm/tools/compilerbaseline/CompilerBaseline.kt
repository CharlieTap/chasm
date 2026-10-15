package io.github.charlietap.chasm.tools.compilerbaseline

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
    val exceptions: JsonObject? = null,
    val instructions: List<CompilerBaselineInstruction>,
)

@Serializable
data class CompilerBaselineInstruction(
    val offset: Int,
    val tag: String,
    val immediates: JsonObject? = null,
)

data class CompilerBaselineFixture(
    val name: String,
    val bytes: ByteArray,
)
