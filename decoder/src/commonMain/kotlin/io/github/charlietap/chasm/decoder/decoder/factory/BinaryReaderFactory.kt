package io.github.charlietap.chasm.decoder.decoder.factory

import io.github.charlietap.chasm.decoder.reader.WasmBinaryReader
import io.github.charlietap.chasm.stream.SourceReader

internal typealias BinaryReaderFactory = (SourceReader) -> WasmBinaryReader
