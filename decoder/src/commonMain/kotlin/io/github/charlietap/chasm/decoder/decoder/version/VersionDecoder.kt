package io.github.charlietap.chasm.decoder.decoder.version

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.ext.version

internal fun VersionDecoder(
    context: DecoderContext,
): Result<Version, WasmDecodeError> = context.reader.ubytes(4u).flatMap(UByteArray::version)
