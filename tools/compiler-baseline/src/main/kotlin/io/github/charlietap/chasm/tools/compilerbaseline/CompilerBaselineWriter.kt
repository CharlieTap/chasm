package io.github.charlietap.chasm.tools.compilerbaseline

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CompilerBaselineWriter(
    private val generator: CompilerBaselineGenerator,
    private val json: Json,
) {
    fun write(
        fixture: CompilerBaselineFixture,
        output: Appendable,
    ) {
        val baseline = generator.generate(fixture)
        output.append(json.encodeToString(baseline)).append('\n')
    }
}
