package io.github.charlietap.chasm.decoder.factory

import io.github.charlietap.chasm.SourceReader
import io.github.charlietap.chasm.reader.WasmBinaryReader

typealias BinaryReaderFactory = (SourceReader) -> WasmBinaryReader
