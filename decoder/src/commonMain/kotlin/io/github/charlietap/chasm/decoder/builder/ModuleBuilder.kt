package io.github.charlietap.chasm.decoder.builder

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.section.code.FunctionBody
import io.github.charlietap.chasm.decoder.decoder.section.function.FunctionHeader
import io.github.charlietap.chasm.decoder.error.ModuleDecodeError
import io.github.charlietap.chasm.decoder.error.ModuleDecoderError
import io.github.charlietap.chasm.decoder.error.SectionDecodeError
import io.github.charlietap.chasm.type.DefinedType

internal class ModuleBuilder(private val version: Version) {
    private var types: MutableList<Type> = []
    private var definedTypes: MutableList<DefinedType> = []
    private var functionHeaders: MutableList<FunctionHeader> = []
    private var functionBodies: MutableList<FunctionBody> = []
    private var tables: MutableList<Table> = []
    private var memories: MutableList<Memory> = []
    private var tags: MutableList<Tag> = []
    private var globals: MutableList<Global> = []
    private var elementSegments: MutableList<ElementSegment> = []
    private var dataSegments: MutableList<DataSegment> = []
    private var startFunctions: MutableList<StartFunction> = []
    private var exports: MutableList<Export> = []
    private var imports: MutableList<Import> = []
    private var customs: MutableList<Custom> = []
    private var dataCount: UInt? = null

    fun types(
        types: List<Type>,
        definedTypes: List<DefinedType>,
    ) = apply {
        this.types += types
        this.definedTypes += definedTypes
    }

    fun imports(imports: List<Import>) = apply { this.imports += imports }

    fun exports(exports: List<Export>) = apply { this.exports += exports }

    fun functionHeaders(headers: List<FunctionHeader>) = apply { functionHeaders += headers }

    fun functionBodies(bodies: List<FunctionBody>) = apply { functionBodies += bodies }

    fun tables(tables: List<Table>) = apply { this.tables += tables }

    fun memories(memories: List<Memory>) = apply { this.memories += memories }

    fun tags(tags: List<Tag>) = apply { this.tags += tags }

    fun globals(globals: List<Global>) = apply { this.globals += globals }

    fun elementSegments(elementSegments: List<ElementSegment>) = apply { this.elementSegments += elementSegments }

    fun dataSegments(dataSegments: List<DataSegment>) = apply { this.dataSegments += dataSegments }

    fun start(startFunction: StartFunction) = apply { this.startFunctions += startFunction }

    fun custom(custom: Custom) = apply { customs.add(custom) }

    fun dataCount(count: UInt) = apply { dataCount = count }

    fun build(context: DecoderContext): Result<Module, ModuleDecoderError> = binding {

        if (functionHeaders.size != functionBodies.size) {
            Err(ModuleDecodeError.ModuleMalformed).bind<Unit>()
        }

        if (startFunctions.size > 1) {
            Err(SectionDecodeError.MultipleStartFunctions).bind<Unit>()
        }

        if (context.requiresDataCount && dataCount == null) {
            Err(SectionDecodeError.DataCountRequired).bind<Unit>()
        }

        if (dataCount != null && dataCount != dataSegments.size.toUInt()) {
            Err(SectionDecodeError.DataCountMismatch).bind<Unit>()
        }

        val functionImports = imports.count { it.descriptor is Import.Descriptor.Function }

        val functions = functionHeaders.mapIndexed { index, header ->
            val body = functionBodies[index]
            Function(
                Index.FunctionIndex(functionImports.toUInt() + header.idx.idx),
                header.typeIndex,
                body.locals,
                body.body,
            )
        }

        Module(
            version = version,
            types = types,
            definedTypes = definedTypes,
            functions = functions,
            tables = tables,
            memories = memories,
            tags = tags,
            globals = globals,
            elementSegments = elementSegments,
            dataSegments = dataSegments,
            startFunction = startFunctions.firstOrNull(),
            exports = exports,
            imports = imports,
            customs = customs,
        )
    }
}
