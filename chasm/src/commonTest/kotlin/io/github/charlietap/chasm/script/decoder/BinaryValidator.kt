package io.github.charlietap.chasm.script.decoder

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.component
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.embedding.validate

internal fun BinaryValidator(
    bytes: ByteArray,
    moduleConfig: ModuleConfig,
): ChasmResult<Any, ModuleError> {
    return when (WasmLayerDecoder(bytes)) {
        WasmLayer.Core -> module(bytes, moduleConfig).fold(
            onSuccess = { module -> validate(module) },
            onError = { error -> ChasmResult.Error(error) },
        )
        WasmLayer.Component -> component(bytes, ComponentConfig(moduleConfig)).fold(
            onSuccess = { component -> validate(component) },
            onError = { error -> ChasmResult.Error(error) },
        )
        null -> ChasmResult.Error(ModuleError.Malformed("invalid or unsupported WebAssembly binary layer"))
    }
}
