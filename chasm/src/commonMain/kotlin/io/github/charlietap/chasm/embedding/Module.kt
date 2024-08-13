package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.decoder.ModuleDecoder
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.decoder.reader.ByteArraySourceReader
import io.github.charlietap.chasm.decoder.reader.SourceReader
import io.github.charlietap.chasm.embedding.error.ChasmError.DecodeError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Module

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
        .map(::Module)
        .fold(::Success, ::Error)
}
