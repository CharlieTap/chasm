package io.github.charlietap.chasm.script.decoder

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.component
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.validate

internal suspend fun BinaryValidator(
    bytes: ByteArray,
    moduleConfig: ModuleConfig,
): ChasmResult<Any, ModuleError> {
    return when (WasmLayerDecoder(bytes)) {
        WasmLayer.Core -> module(bytes, moduleConfig)
        WasmLayer.Component -> when (val decoded = component(bytes, ComponentConfig(moduleConfig))) {
            is ChasmResult.Success -> validate(decoded.result)
            is ChasmResult.Error -> decoded
        }
        null -> ChasmResult.Error(ModuleError.Malformed("invalid or unsupported WebAssembly binary layer"))
    }
}
