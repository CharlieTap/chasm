package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.dex.CompileConfig
import io.github.charlietap.chasm.dex.DexOutput
import io.github.charlietap.chasm.dex.MemoryDexOutput
import io.github.charlietap.chasm.dex.compile
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger

private const val ENTRY_PACKAGE = "chasm.compiled.device"

private val EMPTY_MODULE = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)

private val modules = AtomicInteger()

fun dexClassName(): String {
    return "$ENTRY_PACKAGE.Module${modules.incrementAndGet()}"
}

fun dexOutput(
    bytes: ByteArray,
    className: String,
    config: RuntimeConfig = RuntimeConfig(),
): MemoryDexOutput {

    val output = DexOutput.memory()
    runBlocking { compile(bytes, output, CompileConfig(className, config)) }.expect("the module compiles to DEX")

    return output
}

fun dexModule(
    bytes: ByteArray,
    config: RuntimeConfig = RuntimeConfig(),
): Module {

    val className = dexClassName()
    val output = dexOutput(bytes, className, config)

    return module(DexInput.memory(output.dexFiles, output.resources), className).expect("the DEX module loads")
}

fun dexModule(
    name: String,
    config: RuntimeConfig = RuntimeConfig(),
): Module {
    return dexModule(resourceBytes("modules/$name.wasm"), config)
}

fun dexStore(config: RuntimeConfig = RuntimeConfig()): Store {
    return store(dexModule(EMPTY_MODULE, config))
}
