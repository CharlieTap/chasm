package io.github.charlietap.chasm.compiler.backend.jvm

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator

internal fun fixture(
    name: String,
): ByteArray =
    checkNotNull(JvmModule::class.java.classLoader.getResourceAsStream("fixtures/$name.wasm")) { "missing fixture $name" }
        .use { it.readBytes() }

internal fun decodeModule(
    bytes: ByteArray,
    config: ModuleConfig = ModuleConfig(),
): Module {
    val module = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException(it.toString()) }
    return WasmModuleValidator(config, module).getOrThrow { IllegalStateException(it.toString()) }
}

internal fun decodeNamedModule(
    bytes: ByteArray,
): Module =
    runCatching { decodeModule(bytes, ModuleConfig(decodeNameSection = true)) }.getOrElse { decodeModule(bytes) }

internal class Generated : JvmOutput {
    val classes = LinkedHashMap<String, ByteArray>()

    override fun classFile(
        internalName: String,
        bytes: ByteArray,
    ) {
        check(classes.put(internalName, bytes) == null) { "class $internalName generated twice" }
    }
}
