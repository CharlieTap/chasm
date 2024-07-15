package io.github.charlietap.chasm.decoder.wasm

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.ModuleDecoderError
import io.github.charlietap.chasm.decoder.SourceReader
import io.github.charlietap.chasm.decoder.wasm.builder.ModuleBuilder
import io.github.charlietap.chasm.decoder.wasm.context.DecoderContext
import io.github.charlietap.chasm.decoder.wasm.context.scope.Scope
import io.github.charlietap.chasm.decoder.wasm.context.scope.SectionScope
import io.github.charlietap.chasm.decoder.wasm.decoder.factory.BinaryReaderFactory
import io.github.charlietap.chasm.decoder.wasm.decoder.magic.BinaryMagicNumberValidator
import io.github.charlietap.chasm.decoder.wasm.decoder.magic.MagicNumberValidator
import io.github.charlietap.chasm.decoder.wasm.decoder.section.SectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.SectionTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.version.VersionDecoder
import io.github.charlietap.chasm.decoder.wasm.ext.section
import io.github.charlietap.chasm.decoder.wasm.reader.SourceWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.Section
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize
import io.github.charlietap.chasm.decoder.wasm.section.SectionType

fun WasmModuleDecoder(
    source: SourceReader,
): Result<Module, ModuleDecoderError> =
    WasmModuleDecoder(
        source = source,
        readerFactory = ::SourceWasmBinaryReader,
        magicNumberValidator = ::BinaryMagicNumberValidator,
        versionDecoder = ::VersionDecoder,
        sectionTypeDecoder = ::SectionTypeDecoder,
        sectionDecoder = ::SectionDecoder,
        scope = ::SectionScope,
    )

internal fun WasmModuleDecoder(
    source: SourceReader,
    readerFactory: BinaryReaderFactory,
    magicNumberValidator: MagicNumberValidator,
    versionDecoder: Decoder<Version>,
    sectionTypeDecoder: Decoder<SectionType>,
    sectionDecoder: Decoder<Section>,
    scope: Scope<Pair<SectionSize, SectionType>>,
): Result<Module, ModuleDecoderError> = binding {

    val reader = readerFactory(source).apply {
        magicNumberValidator(this).bind()
    }
    val context = DecoderContext(reader)

    val version = versionDecoder(context).bind()
    val builder = ModuleBuilder(version)

    while (reader.exhausted().bind().not()) {

        val sectionType = sectionTypeDecoder(context).bind()
        val sectionSize = SectionSize(reader.uint().bind())

        val scopedContext = scope(context, sectionSize to sectionType).bind()
        val section = sectionDecoder(scopedContext).bind()

        builder.section(section)
    }

    builder.build().bind()
}
