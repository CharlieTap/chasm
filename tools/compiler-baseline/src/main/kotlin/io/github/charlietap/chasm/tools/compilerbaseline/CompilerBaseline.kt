package io.github.charlietap.chasm.tools.compilerbaseline

import kotlinx.serialization.Serializable

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
    val instructions: List<String>,
)

data class CompilerBaselineFixture(
    val name: String,
    val bytes: ByteArray,
)
