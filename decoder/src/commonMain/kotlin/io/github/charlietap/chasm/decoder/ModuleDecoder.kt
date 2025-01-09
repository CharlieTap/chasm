package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.stream.SourceReader

fun interface ModuleDecoder : (ModuleConfig, SourceReader) -> Result<Module, ModuleDecoderError>
