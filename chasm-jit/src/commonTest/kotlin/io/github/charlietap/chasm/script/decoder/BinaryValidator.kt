package io.github.charlietap.chasm.script.decoder

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.jit.module
import io.github.charlietap.chasm.jit.validate

internal fun BinaryValidator(
    bytes: ByteArray,
    moduleConfig: ModuleConfig,
): ChasmResult<Any, ChasmError> {
    return when (WasmLayerDecoder(bytes)) {
        WasmLayer.Core -> module(bytes, moduleConfig).fold(
            onSuccess = { module -> validate(module) },
            onError = { error -> ChasmResult.Error(error) },
        )
        WasmLayer.Component -> ChasmResult.Error(ChasmError.DecodeError("components are not supported by the JIT"))
        null -> ChasmResult.Error(ChasmError.DecodeError("invalid or unsupported WebAssembly binary layer"))
    }
}
