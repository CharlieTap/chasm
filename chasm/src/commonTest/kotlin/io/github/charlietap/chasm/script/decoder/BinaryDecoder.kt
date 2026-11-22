package io.github.charlietap.chasm.script.decoder

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.component
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult

internal typealias BinaryDecoder = suspend (ByteArray, ModuleConfig) -> ChasmResult<Any, ModuleError>
internal typealias CoreBinaryDecoder = suspend (ByteArray, ModuleConfig) -> ChasmResult<Any, ModuleError>
internal typealias ComponentBinaryDecoder = suspend (ByteArray, ComponentConfig) -> ChasmResult<Any, ModuleError>

internal suspend fun BinaryDecoder(
    bytes: ByteArray,
    moduleConfig: ModuleConfig,
): ChasmResult<Any, ModuleError> {
    return BinaryDecoder(
        bytes,
        moduleConfig,
        ::WasmLayerDecoder,
        ::CoreBinaryDecoder,
        ::ComponentBinaryDecoder,
    )
}

internal suspend fun BinaryDecoder(
    bytes: ByteArray,
    moduleConfig: ModuleConfig,
    layerDecoder: WasmLayerDecoder,
    coreBinaryDecoder: CoreBinaryDecoder,
    componentBinaryDecoder: ComponentBinaryDecoder,
): ChasmResult<Any, ModuleError> {
    return when (layerDecoder(bytes)) {
        WasmLayer.Core -> coreBinaryDecoder(bytes, moduleConfig)
        WasmLayer.Component -> componentBinaryDecoder(bytes, ComponentConfig(moduleConfig))
        null -> ChasmResult.Error(ModuleError.Malformed("invalid or unsupported WebAssembly binary layer"))
    }
}

private suspend fun CoreBinaryDecoder(
    bytes: ByteArray,
    config: ModuleConfig,
): ChasmResult<Any, ModuleError> {
    return module(bytes, config, validate = false)
}

private suspend fun ComponentBinaryDecoder(
    bytes: ByteArray,
    config: ComponentConfig,
): ChasmResult<Any, ModuleError> {
    return component(bytes, config)
}
