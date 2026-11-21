package io.github.charlietap.chasm.script.decoder

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.jit.module
import kotlinx.coroutines.runBlocking

internal typealias BinaryDecoder = (ByteArray, ModuleConfig) -> ChasmResult<Any, ModuleError>
internal typealias CoreBinaryDecoder = (ByteArray, ModuleConfig) -> ChasmResult<Any, ModuleError>
internal typealias ComponentBinaryDecoder = (ByteArray, ComponentConfig) -> ChasmResult<Any, ModuleError>

internal fun BinaryDecoder(
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

internal fun BinaryDecoder(
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

private fun CoreBinaryDecoder(
    bytes: ByteArray,
    config: ModuleConfig,
): ChasmResult<Any, ModuleError> = runBlocking { module(bytes, config, validate = false) }

private fun ComponentBinaryDecoder(
    bytes: ByteArray,
    config: ComponentConfig,
): ChasmResult<Any, ModuleError> = ChasmResult.Error(ModuleError.Malformed("components are not supported by the JIT"))
