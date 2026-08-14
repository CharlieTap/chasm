package io.github.charlietap.chasm.decoder.decoder

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.context.CodeBodyDecoderContext
import io.github.charlietap.chasm.decoder.context.ComponentDecoderContext
import io.github.charlietap.chasm.decoder.context.ModuleDecoderContext
import io.github.charlietap.chasm.decoder.context.ReaderContext
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal typealias ContextDecoder<C, T> = (C) -> Result<T, WasmDecodeError>

internal typealias ReaderDecoder<T> = ContextDecoder<ReaderContext, T>

internal typealias Decoder<T> = ContextDecoder<ModuleDecoderContext, T>

internal typealias CodeBodyDecoder<T> = ContextDecoder<CodeBodyDecoderContext, T>

internal typealias ComponentDecoder<T> = ContextDecoder<ComponentDecoderContext, T>
