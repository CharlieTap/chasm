package io.github.charlietap.chasm.decoder.context

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader

internal open class CodeBodyDecoderContext(
    val config: ModuleConfig,
    final override var reader: WasmBinaryReader,
    var requiresDataCount: Boolean = false,
    final override var index: Int = 0,
) : ReaderContext,
    VectorContext
