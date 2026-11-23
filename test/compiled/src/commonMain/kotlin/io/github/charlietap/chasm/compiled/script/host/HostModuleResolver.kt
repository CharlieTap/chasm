package io.github.charlietap.chasm.compiled.script.host

import io.github.charlietap.chasm.compiled.Compiler
import io.github.charlietap.chasm.compiled.script.ScriptModules
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.expect

typealias HostModuleResolver = (RuntimeConfig, Compiler) -> Module

fun HostModuleResolver(
    runtimeConfig: RuntimeConfig,
    compiler: Compiler,
): Module {
    val bytes = checkNotNull(ScriptModules::class.java.getResourceAsStream(FILE_HOST_MODULE)) { "missing $FILE_HOST_MODULE" }.use { it.readBytes() }
    return compiler(bytes, ModuleConfig(), runtimeConfig).expect("Failed to compile script host module")
}

private const val FILE_HOST_MODULE = "/script/spectest-host.wasm"
