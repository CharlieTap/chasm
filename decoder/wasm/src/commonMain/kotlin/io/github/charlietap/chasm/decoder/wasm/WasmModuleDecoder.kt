package io.github.charlietap.chasm.decoder.wasm

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.decoder.ModuleDecoderError
import io.github.charlietap.chasm.decoder.SourceReader
import io.github.charlietap.chasm.decoder.wasm.builder.ModuleBuilder
import io.github.charlietap.chasm.decoder.wasm.decoder.factory.BinaryReaderFactory
import io.github.charlietap.chasm.decoder.wasm.decoder.magic.BinaryMagicNumberValidator
import io.github.charlietap.chasm.decoder.wasm.decoder.magic.MagicNumberValidator
import io.github.charlietap.chasm.decoder.wasm.decoder.section.BinarySectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.BinarySectionTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.SectionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.section.SectionTypeDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.version.BinaryVersionDecoder
import io.github.charlietap.chasm.decoder.wasm.decoder.version.VersionDecoder
import io.github.charlietap.chasm.decoder.wasm.ext.section
import io.github.charlietap.chasm.decoder.wasm.reader.SourceWasmBinaryReader
import io.github.charlietap.chasm.decoder.wasm.section.SectionSize

fun WasmModuleDecoder(
    source: SourceReader,
): Result<Module, ModuleDecoderError> =
    WasmModuleDecoder(
        source = source,
        readerFactory = ::SourceWasmBinaryReader,
        magicNumberValidator = ::BinaryMagicNumberValidator,
        versionDecoder = ::BinaryVersionDecoder,
        sectionTypeDecoder = ::BinarySectionTypeDecoder,
        sectionDecoder = BinarySectionDecoder(),
    )

internal fun WasmModuleDecoder(
    source: SourceReader,
    readerFactory: BinaryReaderFactory,
    magicNumberValidator: MagicNumberValidator,
    versionDecoder: VersionDecoder,
    sectionTypeDecoder: SectionTypeDecoder,
    sectionDecoder: SectionDecoder,
): Result<Module, ModuleDecoderError> = binding {

    val reader = readerFactory(source).apply {
        magicNumberValidator(this).bind()
    }

    val version = versionDecoder(reader).bind()
    val builder = ModuleBuilder(version)

    while (reader.exhausted().bind().not()) {

        val sectionType = sectionTypeDecoder(reader).bind()
        val sectionSize = SectionSize(reader.uint().bind())
        val section = sectionDecoder(reader, sectionType, sectionSize).bind()

        builder.section(section)
    }

    builder.build().bind()
}
