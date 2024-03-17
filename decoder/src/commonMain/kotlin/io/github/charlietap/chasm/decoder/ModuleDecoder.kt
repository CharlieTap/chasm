package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module

fun interface ModuleDecoder : (SourceReader) -> Result<Module, ModuleDecoderError>
