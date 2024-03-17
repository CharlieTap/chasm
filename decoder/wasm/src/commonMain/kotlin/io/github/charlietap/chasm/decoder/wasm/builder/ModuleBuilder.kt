package io.github.charlietap.chasm.decoder.wasm.builder

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.decoder.ModuleDecoderError
import io.github.charlietap.chasm.decoder.wasm.decoder.section.code.FunctionBody
import io.github.charlietap.chasm.decoder.wasm.decoder.section.function.FunctionHeader

internal class ModuleBuilder(private val version: Version) {
    private var types: MutableList<Type> = mutableListOf()
    private var functionHeaders: MutableList<FunctionHeader> = mutableListOf()
    private var functionBodies: MutableList<FunctionBody> = mutableListOf()
    private var tables: MutableList<Table> = mutableListOf()
    private var memories: MutableList<Memory> = mutableListOf()
    private var globals: MutableList<Global> = mutableListOf()
    private var elementSegments: MutableList<ElementSegment> = mutableListOf()
    private var dataSegments: MutableList<DataSegment> = mutableListOf()
    private var startFunction: StartFunction? = null
    private var exports: MutableList<Export> = mutableListOf()
    private var imports: MutableList<Import> = mutableListOf()
    private var customs: MutableList<Custom> = mutableListOf()

    fun types(types: List<Type>) = apply { this.types += types }

    fun imports(imports: List<Import>) = apply { this.imports += imports }

    fun exports(exports: List<Export>) = apply { this.exports += exports }

    fun functionHeaders(headers: List<FunctionHeader>) = apply { functionHeaders += headers }

    fun functionBodies(bodies: List<FunctionBody>) = apply { functionBodies += bodies }

    fun tables(tables: List<Table>) = apply { this.tables += tables }

    fun memories(memories: List<Memory>) = apply { this.memories += memories }

    fun globals(globals: List<Global>) = apply { this.globals += globals }

    fun elementSegments(elementSegments: List<ElementSegment>) = apply { this.elementSegments += elementSegments }

    fun dataSegments(dataSegments: List<DataSegment>) = apply { this.dataSegments += dataSegments }

    fun start(startFunction: StartFunction) = apply { this.startFunction = startFunction }

    fun custom(custom: Custom) = apply { customs.add(custom) }

    private fun assembleFunctions(): List<Function> = functionHeaders.mapIndexed { index, header ->
        val body = functionBodies[index]
        Function(
            header.idx,
            header.typeIndex,
            body.locals,
            body.body,
        )
    }

    fun build(): Result<Module, ModuleDecoderError> = binding {
        Module(
            version = version,
            types = types,
            functions = assembleFunctions(),
            tables = tables,
            memories = memories,
            globals = globals,
            elementSegments = elementSegments,
            dataSegments = dataSegments,
            startFunction = startFunction,
            exports = exports,
            imports = imports,
            customs = customs,
        )
    }
}
