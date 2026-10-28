package io.github.charlietap.chasm.tools.compilerbaseline

import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import kotlinx.serialization.json.JsonObject

class CirBaselineWriter(
    private val moduleDecoder: CompilerModuleDecoder,
) {
    private val renderer = CirRenderer()

    fun write(
        fixture: CompilerBaselineFixture,
        output: Appendable,
    ) {
        val module = moduleDecoder.decode(ModuleConfig(decodeNameSection = true), fixture.bytes).unwrap()
        val cir = renderer.module(ModuleCompiler(module).unwrap())
        output.append(cirBaselineJson.encodeToString(JsonObject.serializer(), cir)).append('\n')
    }
}
