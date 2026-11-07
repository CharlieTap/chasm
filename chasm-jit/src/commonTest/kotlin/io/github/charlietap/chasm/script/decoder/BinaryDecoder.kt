package io.github.charlietap.chasm.script.decoder

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.error.ChasmError.DecodeError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.jit.module

internal typealias BinaryDecoder = (ByteArray, ModuleConfig) -> ChasmResult<Any, DecodeError>
internal typealias CoreBinaryDecoder = (ByteArray, ModuleConfig) -> ChasmResult<Any, DecodeError>
internal typealias ComponentBinaryDecoder = (ByteArray, ComponentConfig) -> ChasmResult<Any, DecodeError>

internal fun BinaryDecoder(
    bytes: ByteArray,
    moduleConfig: ModuleConfig,
): ChasmResult<Any, DecodeError> {
    return BinaryDecoder(
        bytes,
        moduleConfig,
        ::WasmLayerDecoder,
        ::CoreBinaryDecoder,
        ::ComponentBinaryDecoder,
    )
}

internal fun BinaryDecoder(
    bytes: ByteArray,
    moduleConfig: ModuleConfig,
    layerDecoder: WasmLayerDecoder,
    coreBinaryDecoder: CoreBinaryDecoder,
    componentBinaryDecoder: ComponentBinaryDecoder,
): ChasmResult<Any, DecodeError> {
    return when (layerDecoder(bytes)) {
        WasmLayer.Core -> coreBinaryDecoder(bytes, moduleConfig)
        WasmLayer.Component -> componentBinaryDecoder(bytes, ComponentConfig(moduleConfig))
        null -> ChasmResult.Error(DecodeError("invalid or unsupported WebAssembly binary layer"))
    }
}

private fun CoreBinaryDecoder(
    bytes: ByteArray,
    config: ModuleConfig,
): ChasmResult<Any, DecodeError> = module(bytes, config)

private fun ComponentBinaryDecoder(
    bytes: ByteArray,
    config: ComponentConfig,
): ChasmResult<Any, DecodeError> = ChasmResult.Error(DecodeError("components are not supported by the JIT"))
