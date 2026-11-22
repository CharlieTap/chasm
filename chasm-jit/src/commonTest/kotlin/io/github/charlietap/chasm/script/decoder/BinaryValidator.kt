package io.github.charlietap.chasm.script.decoder

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import kotlinx.coroutines.runBlocking

internal fun BinaryValidator(
    bytes: ByteArray,
    moduleConfig: ModuleConfig,
): ChasmResult<Any, ModuleError> {
    return when (WasmLayerDecoder(bytes)) {
        WasmLayer.Core -> runBlocking { module(bytes, moduleConfig, validate = true) }
        WasmLayer.Component -> ChasmResult.Error(ModuleError.Malformed("components are not supported by the JIT"))
        null -> ChasmResult.Error(ModuleError.Malformed("invalid or unsupported WebAssembly binary layer"))
    }
}
