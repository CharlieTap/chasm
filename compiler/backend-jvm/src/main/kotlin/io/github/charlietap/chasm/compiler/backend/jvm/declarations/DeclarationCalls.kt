package io.github.charlietap.chasm.compiler.backend.jvm.declarations

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.backend.abi.packLongs
import io.github.charlietap.chasm.declarations.ConstantExpression
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.declarations.ElementSegmentDeclaration
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.construction.ConstantOps
import io.github.charlietap.chasm.declarations.construction.ElementModes
import io.github.charlietap.chasm.declarations.construction.ExportKinds
import io.github.charlietap.chasm.declarations.construction.HeapTypes
import io.github.charlietap.chasm.declarations.construction.ValueTypes
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.SharedStatus
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.ValueType
import java.io.OutputStream
import java.util.IdentityHashMap

internal class BuilderCall(
    val methodName: String,
    val arguments: List<Any>,
)

internal typealias DeclarationCallGenerator = (ModuleDeclarations, OutputStream) -> List<BuilderCall>

internal fun DeclarationCallGenerator(
    declarations: ModuleDeclarations,
    payloads: OutputStream,
): List<BuilderCall> = DeclarationCallWriter(declarations, payloads).write()

private class DeclarationCallWriter(
    private val declarations: ModuleDeclarations,
    private val payloads: OutputStream,
) {
    private val calls = ArrayList<BuilderCall>()

    private val strings = LinkedHashMap<String, Int>()

    private val typeIndicesByIdentity = IdentityHashMap<DefinedType, Int>()

    fun write(): List<BuilderCall> {
        collectStrings()
        writeStrings()
        writeTypes()
        writeImports()
        writeDefinitions()
        writeExports()
        writeSegments()
        writeNames()
        return calls
    }

    private fun call(
        methodName: String,
        vararg arguments: Any,
    ) {
        calls += BuilderCall(methodName, arguments.toList())
    }

    private fun packed(
        name: String,
        values: IntArray,
        vararg leading: Any,
    ) {
        packLongs(LongArray(values.size) { values[it].toLong() }).forEach { part -> call(name, *leading, part) }
    }

    private fun collectStrings() {
        declarations.imports.forEach { import ->
            internString(import.moduleName.name)
            internString(import.entityName.name)
        }
        declarations.exports.forEach { export -> internString(export.name.name) }
        val names = declarations.names
        names.moduleName?.let(::internString)
        names.functionNames.forEach(::internString)
        names.trapNames.forEach(::internString)
        names.exportedLocalNames.values.forEach { locals -> locals.forEach(::internString) }
    }

    private fun internString(
        value: String,
    ) {
        strings.getOrPut(value) { strings.size }
    }

    private fun stringIndex(
        value: String,
    ): Int = strings.getValue(value)

    private fun writeStrings() {
        if (strings.isEmpty()) {
            call("endStrings")
            return
        }
        val chunk = StringBuilder()
        var chunkBytes = 0
        strings.keys.forEach { value ->
            value.forEach { character ->
                val bytes = modifiedUtf8Bytes(character)
                if (chunkBytes + bytes > MAX_STRING_CONSTANT_UTF8_BYTES) {
                    call("strings", chunk.toString())
                    chunk.clear()
                    chunkBytes = 0
                }
                chunk.append(character)
                chunkBytes += bytes
            }
        }
        if (chunk.isNotEmpty()) call("strings", chunk.toString())
        packed("stringLengths", strings.keys.map { it.length }.toIntArray())
        call("endStrings")
    }

    private fun writeTypes() {
        val groups = ArrayList<RecursiveType>()
        var groupStart = 0
        declarations.definedTypes.forEachIndexed { index, type ->
            typeIndicesByIdentity[type] = index
            if (groups.lastOrNull() !== type.recursiveType) {
                groups += type.recursiveType
                groupStart = index
            }
            require(type.recursiveTypeIndex == index - groupStart && type.recursiveTypeIndex < type.recursiveType.subTypes.size) {
                "type $index is out of its group's order"
            }
        }
        groups.forEach { group -> call("group", group.subTypes.size) }
        groups.forEachIndexed { index, group ->
            group.subTypes.forEach(::subType)
            call("groupBody", index, group.subTypes.size)
        }
    }

    private fun subType(
        subType: SubType,
    ) {
        subType.superTypes.forEach(::heapType)
        when (val composite = subType.compositeType) {
            is CompositeType.Function -> {
                composite.functionType.params.types.forEach(::valueType)
                composite.functionType.results.types.forEach(::valueType)
                call("functionComposite", composite.functionType.params.types.size, composite.functionType.results.types.size)
            }
            is CompositeType.Struct -> {
                composite.structType.fields.forEach(::fieldType)
                call("structComposite", composite.structType.fields.size)
            }
            is CompositeType.Array -> {
                fieldType(composite.arrayType.fieldType)
                call("arrayComposite")
            }
        }
        call("subType", subType is SubType.Final, subType.superTypes.size)
    }

    private fun fieldType(
        field: FieldType,
    ) {
        val mutable = field.mutability == Mutability.Var
        when (val storage = field.storageType) {
            is StorageType.Value -> {
                valueType(storage.type)
                call("fieldType", ValueTypes.STORAGE_VALUE, mutable)
            }
            is StorageType.Packed -> call(
                "fieldType",
                if (storage.type == PackedType.I8) ValueTypes.STORAGE_I8 else ValueTypes.STORAGE_I16,
                mutable,
            )
        }
    }

    private fun valueType(
        type: ValueType,
    ) {
        when (type) {
            is ValueType.Number -> call("numberType", ValueTypes.tag(type.numberType))
            is ValueType.Vector -> call("vectorType")
            is ValueType.Reference -> {
                referenceType(type.referenceType)
                call("referenceValueType")
            }
            is ValueType.Bottom -> throw IllegalArgumentException("a module cannot declare the bottom value type")
        }
    }

    private fun referenceType(
        type: ReferenceType,
    ) {
        heapType(type.heapType)
        call("referenceType", type is ReferenceType.RefNull)
    }

    private fun heapType(
        type: HeapType,
    ) {
        when (type) {
            is AbstractHeapType -> call("abstractHeapType", HeapTypes.tag(type))
            is ConcreteHeapType.TypeIndex -> call("typeIndexHeapType", type.index)
            is ConcreteHeapType.RecursiveTypeIndex -> call("recursiveHeapType", type.index)
            is ConcreteHeapType.Defined -> call("definedHeapType", definedIndex(type.definedType))
        }
    }

    private fun definedIndex(
        type: DefinedType,
    ): Int {
        typeIndicesByIdentity[type]?.let { return it }
        val index = type.typeIndex
        require(index in declarations.definedTypes.indices && declarations.definedTypes[index] == type) {
            "a heap type names a type the module does not define"
        }
        return index
    }

    private fun limits(
        limits: Limits,
    ) {
        call("limits", limits.min.toLong(), limits.max != null, (limits.max ?: 0uL).toLong())
    }

    private fun tableType(
        type: TableType,
    ) {
        referenceType(type.referenceType)
        limits(type.limits)
        call("tableType", type.addressType == AddressType.I64)
    }

    private fun memoryType(
        type: MemoryType,
    ) {
        limits(type.limits)
        call("memoryType", type.addressType == AddressType.I64, type.shared == SharedStatus.Shared)
    }

    private fun globalType(
        type: GlobalType,
    ) {
        valueType(type.valueType)
        call("globalType", type.mutability == Mutability.Var)
    }

    private fun writeImports() {
        declarations.imports.forEach { import ->
            val module = stringIndex(import.moduleName.name)
            val entity = stringIndex(import.entityName.name)
            when (val descriptor = import.descriptor) {
                is Import.Descriptor.Function -> call("importFunction", module, entity, descriptor.typeIndex.toInt())
                is Import.Descriptor.Table -> {
                    tableType(descriptor.type)
                    call("importTable", module, entity)
                }
                is Import.Descriptor.Memory -> {
                    memoryType(descriptor.type)
                    call("importMemory", module, entity)
                }
                is Import.Descriptor.Global -> {
                    globalType(descriptor.type)
                    call("importGlobal", module, entity)
                }
                is Import.Descriptor.Tag -> call("importTag", module, entity, descriptor.type.typeIndex)
            }
        }
    }

    private fun writeDefinitions() {
        if (declarations.functionTypeIndices.isNotEmpty()) packed("functions", declarations.functionTypeIndices)
        declarations.tables.forEach { table ->
            tableType(table.type)
            constantExpression(table.initExpression)
            call("table")
        }
        declarations.memories.forEach { memory ->
            memoryType(memory)
            call("memory")
        }
        declarations.tags.forEach { tag -> call("tag", tag.typeIndex) }
        declarations.globals.forEach { global ->
            globalType(global.type)
            constantExpression(global.initExpression)
            call("global")
        }
    }

    private fun writeExports() {
        val exports = declarations.exports
        if (exports.isEmpty()) return
        packed("exportKinds", IntArray(exports.size) { ExportKinds.kind(exports[it].descriptor) })
        packed("exportIndices", IntArray(exports.size) { ExportKinds.index(exports[it].descriptor) })
        packed("exportNames", IntArray(exports.size) { stringIndex(exports[it].name.name) })
    }

    private fun writeSegments() {
        declarations.startFunction?.let { call("startFunction", it.toInt()) }
        declarations.elementSegments.forEach(::elementSegment)
        var offset = 0L
        declarations.dataSegments.forEach { segment ->
            val payload = segment.payload
            require(payload is DataPayload.Inline) { "a data segment's bytes must be at hand to compile it" }
            payloads.write(payload.bytes)
            val mode = segment.mode
            if (mode is DataSegmentDeclaration.Mode.Active) constantExpression(mode.offset)
            call(
                "dataSegment",
                mode is DataSegmentDeclaration.Mode.Active,
                (mode as? DataSegmentDeclaration.Mode.Active)?.memoryIndex?.toInt() ?: 0,
                offset,
                payload.size,
            )
            offset += payload.size
        }
    }

    private fun elementSegment(
        segment: ElementSegmentDeclaration,
    ) {
        referenceType(segment.type)
        val mode = segment.mode
        if (mode is ElementSegmentDeclaration.Mode.Active) constantExpression(mode.offsetExpr)
        var position = 0
        val initExpressions = segment.initExpressions
        while (position < initExpressions.size) {
            val run = ArrayList<Int>()
            while (position < initExpressions.size) {
                val function = singleRefFunc(initExpressions[position]) ?: break
                run += function
                position++
            }
            if (run.isNotEmpty()) {
                packed("refFuncConstantExpressions", run.toIntArray())
            } else {
                constantExpression(initExpressions[position])
                position++
            }
        }
        val modeTag = when (mode) {
            ElementSegmentDeclaration.Mode.Passive -> ElementModes.PASSIVE
            ElementSegmentDeclaration.Mode.Declarative -> ElementModes.DECLARATIVE
            is ElementSegmentDeclaration.Mode.Active -> ElementModes.ACTIVE
        }
        val tableIndex = (mode as? ElementSegmentDeclaration.Mode.Active)?.tableIndex?.toInt() ?: 0
        call("elementSegment", modeTag, tableIndex, initExpressions.size)
    }

    private fun singleRefFunc(
        expression: ConstantExpression,
    ): Int? = (expression.instructions.singleOrNull() as? ConstantInstruction.RefFunc)?.funcIdx?.toInt()

    private fun constantExpression(
        expression: ConstantExpression,
    ) {
        expression.instructions.forEach(::instruction)
        call("constantExpression", expression.instructions.size)
    }

    private fun instruction(
        instruction: ConstantInstruction,
    ) {
        when (instruction) {
            is ConstantInstruction.I32Const -> call("i32Const", instruction.value)
            is ConstantInstruction.I64Const -> call("i64Const", instruction.value)
            is ConstantInstruction.F32Const -> call("f32Const", instruction.bits)
            is ConstantInstruction.F64Const -> call("f64Const", instruction.bits)
            is ConstantInstruction.V128Const -> call("v128Const", instruction.low, instruction.high)
            is ConstantInstruction.GlobalGet -> call("globalGet", instruction.globalIdx.toInt())
            is ConstantInstruction.RefNull -> {
                heapType(instruction.type)
                call("refNull")
            }
            is ConstantInstruction.RefFunc -> call("refFunc", instruction.funcIdx.toInt())
            is ConstantInstruction.StructNew -> call("structNew", instruction.typeIndex.toInt())
            is ConstantInstruction.StructNewDefault -> call("structNewDefault", instruction.typeIndex.toInt())
            is ConstantInstruction.ArrayNew -> call("arrayNew", instruction.typeIndex.toInt())
            is ConstantInstruction.ArrayNewDefault -> call("arrayNewDefault", instruction.typeIndex.toInt())
            is ConstantInstruction.ArrayNewFixed -> call("arrayNewFixed", instruction.typeIndex.toInt(), instruction.size.toInt())
            else -> call("op", ConstantOps.op(instruction).also { require(it >= 0) { "unknown instruction $instruction" } })
        }
    }

    private fun writeNames() {
        val names = declarations.names
        names.moduleName?.let { call("moduleName", stringIndex(it)) }
        if (names.functionNameIndices.isNotEmpty()) {
            packed("functionNameIndices", names.functionNameIndices)
            packed("functionNames", IntArray(names.functionNames.size) { stringIndex(names.functionNames[it]) })
        }
        if (names.trapNameIndices.isNotEmpty()) {
            packed("trapNameIndices", names.trapNameIndices)
            packed("trapNames", IntArray(names.trapNames.size) { stringIndex(names.trapNames[it]) })
        }
        names.exportedLocalNames.forEach { (function, locals) ->
            packed("localNames", IntArray(locals.size) { stringIndex(locals[it]) }, function)
        }
    }

    private companion object {
        const val MAX_STRING_CONSTANT_UTF8_BYTES = 60_000

        fun modifiedUtf8Bytes(
            character: Char,
        ): Int = when (character.code) {
            in 1..0x7F -> 1
            in 0x80..0x7FF, 0 -> 2
            else -> 3
        }
    }
}
