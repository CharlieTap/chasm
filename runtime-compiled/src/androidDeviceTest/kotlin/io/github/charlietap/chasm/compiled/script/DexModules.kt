package io.github.charlietap.chasm.compiled.script

import io.github.charlietap.chasm.compiled.DexInput
import io.github.charlietap.chasm.compiled.ScriptFailure
import io.github.charlietap.chasm.compiled.dexClassName
import io.github.charlietap.chasm.compiled.module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.dex.CompileConfig
import io.github.charlietap.chasm.dex.DexOutput
import io.github.charlietap.chasm.dex.compile
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import kotlinx.coroutines.runBlocking

internal fun compileDexModule(
    bytes: ByteArray,
    config: ModuleConfig,
    runtime: RuntimeConfig,
): ChasmResult<Module, ScriptFailure> {

    val validated = ScriptModules.validate(bytes, config)
    if (validated is ChasmResult.Error) {
        return ChasmResult.Error(ScriptFailure.Module(validated.error))
    }

    val output = DexOutput.memory()
    val className = dexClassName()
    val compiled = runBlocking { compile(bytes, output, CompileConfig(className, runtime)) }
    if (compiled is ChasmResult.Error) {
        return ChasmResult.Error(ScriptFailure.Execution(ExecutionError.Unsupported(compiled.error.message)))
    }

    return when (val loaded = module(DexInput.memory(output.dexFiles, output.resources), className)) {
        is ChasmResult.Success -> loaded
        is ChasmResult.Error -> ChasmResult.Error(ScriptFailure.Module(loaded.error))
    }
}
