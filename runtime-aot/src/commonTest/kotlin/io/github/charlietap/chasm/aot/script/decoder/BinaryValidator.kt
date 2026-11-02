package io.github.charlietap.chasm.aot.script.decoder

import io.github.charlietap.chasm.aot.script.ScriptModules
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult

internal fun BinaryValidator(
    bytes: ByteArray,
    moduleConfig: ModuleConfig,
): ChasmResult<Any, ChasmError> = when (WasmLayerDecoder(bytes)) {
    WasmLayer.Core -> ScriptModules.validate(bytes, moduleConfig)
    WasmLayer.Component -> ChasmResult.Error(ChasmError.DecodeError("components are not compiled ahead of time"))
    null -> ChasmResult.Error(ChasmError.DecodeError("invalid or unsupported WebAssembly binary layer"))
}
