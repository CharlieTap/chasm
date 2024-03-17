package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.decoder.ByteArraySourceReader
import io.github.charlietap.chasm.decoder.ModuleDecoder
import io.github.charlietap.chasm.decoder.SourceReader
import io.github.charlietap.chasm.decoder.wasm.WasmModuleDecoder
import io.github.charlietap.chasm.error.ChasmError.DecodeError

fun module(sourceReader: SourceReader): ChasmResult<Module, DecodeError> {
    return module(sourceReader, ::WasmModuleDecoder)
}

fun module(bytes: ByteArray): ChasmResult<Module, DecodeError> {
    return module(ByteArraySourceReader(bytes), ::WasmModuleDecoder)
}

internal fun module(
    reader: SourceReader,
    decoder: ModuleDecoder,
): ChasmResult<Module, DecodeError> {
    return decoder(reader)
        .mapError(::DecodeError)
        .fold(::Success, ::Error)
}
