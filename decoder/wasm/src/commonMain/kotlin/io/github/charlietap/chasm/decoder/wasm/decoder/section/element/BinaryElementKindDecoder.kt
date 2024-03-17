package io.github.charlietap.chasm.decoder.wasm.decoder.section.element

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.decoder.wasm.error.SectionDecodeError
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.wasm.reader.WasmBinaryReader

internal fun BinaryElementKindDecoder(
    reader: WasmBinaryReader,
): Result<ElementKind, WasmDecodeError> = binding {
    when (val byte = reader.byte().bind()) {
        ElementKind.FuncRef.byte -> ElementKind.FuncRef
        else -> Err(SectionDecodeError.UnknownElementKind(byte)).bind<ElementKind>()
    }
}
