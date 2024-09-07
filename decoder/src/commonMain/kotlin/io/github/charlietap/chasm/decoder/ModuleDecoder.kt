package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.stream.SourceReader

fun interface ModuleDecoder : (SourceReader) -> Result<Module, ModuleDecoderError>
