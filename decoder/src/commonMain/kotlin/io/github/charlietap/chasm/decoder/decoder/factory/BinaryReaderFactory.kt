package io.github.charlietap.chasm.decoder.decoder.factory

import io.github.charlietap.chasm.decoder.reader.SourceReader
import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader

internal typealias BinaryReaderFactory = (SourceReader) -> WasmBinaryReader
