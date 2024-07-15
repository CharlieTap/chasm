package io.github.charlietap.chasm.decoder.wasm.decoder.section.export

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.wasm.Decoder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.decoder.value.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.wasm.error.WasmDecodeError

internal fun ExportDecoder(
    context: DecoderContext,
): Result<Export, WasmDecodeError> =
    ExportDecoder(
        context = context,
        nameValueDecoder = ::NameValueDecoder,
        exportDescriptorDecoder = ::ExportDescriptorDecoder,
    )

internal fun ExportDecoder(
    context: DecoderContext,
    nameValueDecoder: Decoder<NameValue>,
    exportDescriptorDecoder: Decoder<Export.Descriptor>,
): Result<Export, WasmDecodeError> = binding {

    val name = nameValueDecoder(context).bind()
    val descriptor = exportDescriptorDecoder(context).bind()

    Export(name, descriptor)
}
