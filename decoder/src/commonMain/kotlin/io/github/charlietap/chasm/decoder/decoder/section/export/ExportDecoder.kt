package io.github.charlietap.chasm.decoder.decoder.section.export

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.name.NameValueDecoder
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun ExportDecoder(
    context: DecoderContext,
): Result<Export, WasmDecodeError> =
    ExportDecoder(
        context = context,
        nameValueDecoder = ::NameValueDecoder,
        exportDescriptorDecoder = ::ExportDescriptorDecoder,
    )

internal inline fun ExportDecoder(
    context: DecoderContext,
    crossinline nameValueDecoder: Decoder<NameValue>,
    crossinline exportDescriptorDecoder: Decoder<Export.Descriptor>,
): Result<Export, WasmDecodeError> = binding {

    val name = nameValueDecoder(context).bind()
    val descriptor = exportDescriptorDecoder(context).bind()

    Export(name, descriptor)
}
