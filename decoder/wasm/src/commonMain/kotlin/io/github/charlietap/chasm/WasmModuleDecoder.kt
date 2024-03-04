package io.github.charlietap.chasm

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.builder.ModuleBuilder
import io.github.charlietap.chasm.decoder.factory.BinaryReaderFactory
import io.github.charlietap.chasm.decoder.magic.BinaryMagicNumberValidator
import io.github.charlietap.chasm.decoder.magic.MagicNumberValidator
import io.github.charlietap.chasm.decoder.section.BinarySectionDecoder
import io.github.charlietap.chasm.decoder.section.BinarySectionTypeDecoder
import io.github.charlietap.chasm.decoder.section.SectionDecoder
import io.github.charlietap.chasm.decoder.section.SectionTypeDecoder
import io.github.charlietap.chasm.decoder.version.BinaryVersionDecoder
import io.github.charlietap.chasm.decoder.version.VersionDecoder
import io.github.charlietap.chasm.ext.section
import io.github.charlietap.chasm.reader.SourceWasmBinaryReader
import io.github.charlietap.chasm.section.SectionSize

class WasmModuleDecoder(
    private val readerFactory: BinaryReaderFactory = ::SourceWasmBinaryReader,
    private val magicNumberValidator: MagicNumberValidator = ::BinaryMagicNumberValidator,
    private val versionDecoder: VersionDecoder = ::BinaryVersionDecoder,
    private val sectionTypeDecoder: SectionTypeDecoder = ::BinarySectionTypeDecoder,
    private val sectionDecoder: SectionDecoder = BinarySectionDecoder(),
) : ModuleDecoder {

    override fun invoke(source: SourceReader): Result<Module, ModuleDecoderError> = binding {

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
}
