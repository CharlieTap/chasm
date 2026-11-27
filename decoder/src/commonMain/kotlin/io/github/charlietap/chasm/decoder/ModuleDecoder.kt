package io.github.charlietap.chasm.decoder

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError

internal fun interface ModuleDecoder<Source> : (ModuleConfig, Source) -> Result<Module, ModuleDecoderError>
