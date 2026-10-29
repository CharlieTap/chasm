package io.github.charlietap.chasm.declarations.construction

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.declarations.ConstantExpression
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.declarations.DataSource
import io.github.charlietap.chasm.declarations.ElementSegmentDeclaration
import io.github.charlietap.chasm.declarations.GlobalDeclaration
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.ModuleNames
import io.github.charlietap.chasm.declarations.TableDeclaration
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.SharedStatus
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.VectorType
import io.github.charlietap.chasm.type.ext.functionType

class DeclarationsBuilder {

    private val stack = ArrayList<Any>()

    private val stringChunks = StringBuilder()
    private val stringLengths = IntColumn()
    private var strings: Array<String>? = null

    private val groups = ArrayList<RecursiveType>()
    private val definedTypes = ArrayList<DefinedType>()

    private val imports = ArrayList<Import>()
    private val functionTypeIndices = IntColumn()
    private val tables = ArrayList<TableDeclaration>()
    private val memories = ArrayList<MemoryType>()
    private val tags = ArrayList<TagType>()
    private val globals = ArrayList<GlobalDeclaration>()
    private val exportKinds = IntColumn()
    private val exportIndices = IntColumn()
    private val exportNames = IntColumn()
    private var startFunction: Int? = null
    private val elementSegments = ArrayList<ElementSegmentDeclaration>()
    private val dataSegments = ArrayList<DataSegmentDeclaration>()

    private var moduleName: String? = null
    private val functionNameIndices = IntColumn()
    private val functionNameStrings = IntColumn()
    private val exportedLocalNames = LinkedHashMap<Int, MutableList<String>>()
    private val trapNameIndices = IntColumn()
    private val trapNameStrings = IntColumn()
    private var built = false

    fun strings(
        chunk: String,
    ) {
        check(strings == null) { "strings already ended" }
        stringChunks.append(chunk)
    }

    fun stringLengths(
        part: String,
    ) {
        check(strings == null) { "strings already ended" }
        stringLengths.unpack(part)
    }

    fun endStrings() {
        check(strings == null) { "strings already ended" }
        var start = 0
        strings = Array(stringLengths.size) { index ->
            val end = start + stringLengths[index]
            stringChunks.substring(start, end).also { start = end }
        }
        check(start == stringChunks.length) { "string lengths cover $start of ${stringChunks.length} characters" }
        stringChunks.clear()
    }

    private fun string(
        index: Int,
    ): String = checkNotNull(strings) { "strings not ended" }[index]

    fun group(
        members: Int,
    ) {
        val group = RecursiveType(emptyList(), RecursiveType.State.CLOSED)
        groups += group
        repeat(members) { member ->
            definedTypes += DefinedType(group, member, definedTypes.size)
        }
    }

    fun groupBody(
        index: Int,
        members: Int,
    ) {
        val group = groups[index]
        check(group.subTypes.isEmpty()) { "group $index already has its body" }
        group.subTypes = popList(members)
    }

    fun abstractHeapType(
        tag: Int,
    ) {
        stack += HeapTypes.abstract(tag)
    }

    fun typeIndexHeapType(
        index: Int,
    ) {
        stack += ConcreteHeapType.TypeIndex(index)
    }

    fun recursiveHeapType(
        index: Int,
    ) {
        stack += ConcreteHeapType.RecursiveTypeIndex(index)
    }

    fun definedHeapType(
        index: Int,
    ) {
        stack += ConcreteHeapType.Defined(definedTypes[index])
    }

    fun referenceType(
        nullable: Boolean,
    ) {
        val heapType = pop<HeapType>()
        stack += if (nullable) ReferenceType.RefNull(heapType) else ReferenceType.Ref(heapType)
    }

    fun numberType(
        tag: Int,
    ) {
        stack += ValueType.Number(ValueTypes.number(tag))
    }

    fun vectorType() {
        stack += ValueType.Vector(VectorType.V128)
    }

    fun referenceValueType() {
        stack += ValueType.Reference(pop<ReferenceType>())
    }

    fun fieldType(
        storage: Int,
        mutable: Boolean,
    ) {
        val storageType = when (storage) {
            ValueTypes.STORAGE_VALUE -> StorageType.Value(pop<ValueType>())
            ValueTypes.STORAGE_I8 -> StorageType.Packed(PackedType.I8)
            ValueTypes.STORAGE_I16 -> StorageType.Packed(PackedType.I16)
            else -> error("unknown storage $storage")
        }
        stack += FieldType(storageType, if (mutable) Mutability.Var else Mutability.Const)
    }

    fun functionComposite(
        params: Int,
        results: Int,
    ) {
        stack += CompositeType.Function(functionType(params, results))
    }

    fun structComposite(
        fields: Int,
    ) {
        stack += CompositeType.Struct(StructType(popList(fields)))
    }

    fun arrayComposite() {
        stack += CompositeType.Array(ArrayType(pop()))
    }

    fun subType(
        final: Boolean,
        supertypes: Int,
    ) {
        val composite = pop<CompositeType>()
        val supers = popList<HeapType>(supertypes)
        stack += if (final) SubType.Final(supers, composite) else SubType.Open(supers, composite)
    }

    private fun functionType(
        params: Int,
        results: Int,
    ): FunctionType {
        val resultTypes = popList<ValueType>(results)
        val paramTypes = popList<ValueType>(params)
        return FunctionType(ResultType(paramTypes), ResultType(resultTypes))
    }

    fun limits(
        min: Long,
        hasMax: Boolean,
        max: Long,
    ) {
        stack += Limits(min.toULong(), if (hasMax) max.toULong() else null)
    }

    fun tableType(
        address64: Boolean,
    ) {
        val limits = pop<Limits>()
        val referenceType = pop<ReferenceType>()
        stack += TableType(addressType(address64), referenceType, limits)
    }

    fun memoryType(
        address64: Boolean,
        shared: Boolean,
    ) {
        stack += MemoryType(addressType(address64), pop(), if (shared) SharedStatus.Shared else SharedStatus.Unshared)
    }

    fun globalType(
        mutable: Boolean,
    ) {
        stack += GlobalType(pop(), if (mutable) Mutability.Var else Mutability.Const)
    }

    private fun tagType(
        typeIndex: Int,
    ): TagType = TagType(
        attribute = TagType.Attribute.Exception,
        typeIndex = typeIndex,
        functionType = checkNotNull(definedTypes[typeIndex].functionType()) { "type $typeIndex is not a function type" },
    )

    fun importFunction(
        module: Int,
        entity: Int,
        typeIndex: Int,
    ) {
        import(module, entity, Import.Descriptor.Function(Index.TypeIndex(typeIndex.toUInt())))
    }

    fun importTable(
        module: Int,
        entity: Int,
    ) {
        import(module, entity, Import.Descriptor.Table(pop()))
    }

    fun importMemory(
        module: Int,
        entity: Int,
    ) {
        import(module, entity, Import.Descriptor.Memory(pop()))
    }

    fun importGlobal(
        module: Int,
        entity: Int,
    ) {
        import(module, entity, Import.Descriptor.Global(pop()))
    }

    fun importTag(
        module: Int,
        entity: Int,
        typeIndex: Int,
    ) {
        import(module, entity, Import.Descriptor.Tag(tagType(typeIndex)))
    }

    private fun import(
        module: Int,
        entity: Int,
        descriptor: Import.Descriptor,
    ) {
        imports += Import(NameValue(string(module)), NameValue(string(entity)), descriptor)
    }

    fun functions(
        part: String,
    ) {
        functionTypeIndices.unpack(part)
    }

    fun table() {
        val initExpression = pop<ConstantExpression>()
        tables += TableDeclaration(pop(), initExpression)
    }

    fun memory() {
        memories += pop<MemoryType>()
    }

    fun tag(
        typeIndex: Int,
    ) {
        tags += tagType(typeIndex)
    }

    fun global() {
        val initExpression = pop<ConstantExpression>()
        globals += GlobalDeclaration(pop(), initExpression)
    }

    fun exportKinds(
        part: String,
    ) {
        exportKinds.unpack(part)
    }

    fun exportIndices(
        part: String,
    ) {
        exportIndices.unpack(part)
    }

    fun exportNames(
        part: String,
    ) {
        exportNames.unpack(part)
    }

    fun startFunction(
        index: Int,
    ) {
        startFunction = index
    }

    fun elementSegment(
        mode: Int,
        tableIndex: Int,
        count: Int,
    ) {
        val initExpressions = popList<ConstantExpression>(count)
        val segmentMode = when (mode) {
            ElementModes.PASSIVE -> ElementSegmentDeclaration.Mode.Passive
            ElementModes.DECLARATIVE -> ElementSegmentDeclaration.Mode.Declarative
            ElementModes.ACTIVE -> ElementSegmentDeclaration.Mode.Active(Index.TableIndex(tableIndex.toUInt()), pop())
            else -> error("unknown element mode $mode")
        }
        elementSegments += ElementSegmentDeclaration(pop(), initExpressions, segmentMode)
    }

    fun dataSegment(
        active: Boolean,
        memoryIndex: Int,
        offset: Long,
        size: Int,
    ) {
        val mode = if (active) {
            DataSegmentDeclaration.Mode.Active(Index.MemoryIndex(memoryIndex.toUInt()), pop())
        } else {
            DataSegmentDeclaration.Mode.Passive
        }
        dataSegments += DataSegmentDeclaration(DataPayload.External(offset, size), mode)
    }

    fun i32Const(
        value: Int,
    ) {
        stack += ConstantInstruction.I32Const(value)
    }

    fun i64Const(
        value: Long,
    ) {
        stack += ConstantInstruction.I64Const(value)
    }

    fun f32Const(
        bits: Int,
    ) {
        stack += ConstantInstruction.F32Const(bits)
    }

    fun f64Const(
        bits: Long,
    ) {
        stack += ConstantInstruction.F64Const(bits)
    }

    fun v128Const(
        low: Long,
        high: Long,
    ) {
        stack += ConstantInstruction.V128Const(low, high)
    }

    fun op(
        op: Int,
    ) {
        stack += ConstantOps.instruction(op)
    }

    fun globalGet(
        index: Int,
    ) {
        stack += ConstantInstruction.GlobalGet(Index.GlobalIndex(index.toUInt()))
    }

    fun refNull() {
        stack += ConstantInstruction.RefNull(pop())
    }

    fun refFunc(
        index: Int,
    ) {
        stack += ConstantInstruction.RefFunc(Index.FunctionIndex(index.toUInt()))
    }

    fun structNew(
        typeIndex: Int,
    ) {
        stack += ConstantInstruction.StructNew(Index.TypeIndex(typeIndex.toUInt()))
    }

    fun structNewDefault(
        typeIndex: Int,
    ) {
        stack += ConstantInstruction.StructNewDefault(Index.TypeIndex(typeIndex.toUInt()))
    }

    fun arrayNew(
        typeIndex: Int,
    ) {
        stack += ConstantInstruction.ArrayNew(Index.TypeIndex(typeIndex.toUInt()))
    }

    fun arrayNewDefault(
        typeIndex: Int,
    ) {
        stack += ConstantInstruction.ArrayNewDefault(Index.TypeIndex(typeIndex.toUInt()))
    }

    fun arrayNewFixed(
        typeIndex: Int,
        size: Int,
    ) {
        stack += ConstantInstruction.ArrayNewFixed(Index.TypeIndex(typeIndex.toUInt()), size.toUInt())
    }

    fun constantExpression(
        count: Int,
    ) {
        stack += ConstantExpression(popList(count))
    }

    fun refFuncConstantExpressions(
        part: String,
    ) {
        val indices = IntColumn().apply { unpack(part) }
        for (position in 0 until indices.size) {
            val instruction = ConstantInstruction.RefFunc(Index.FunctionIndex(indices[position].toUInt()))
            stack += ConstantExpression(listOf(instruction))
        }
    }

    fun moduleName(
        name: Int,
    ) {
        moduleName = string(name)
    }

    fun functionNameIndices(
        part: String,
    ) {
        functionNameIndices.unpack(part)
    }

    fun functionNames(
        part: String,
    ) {
        functionNameStrings.unpack(part)
    }

    fun trapNameIndices(
        part: String,
    ) {
        trapNameIndices.unpack(part)
    }

    fun trapNames(
        part: String,
    ) {
        trapNameStrings.unpack(part)
    }

    fun localNames(
        function: Int,
        part: String,
    ) {
        val names = IntColumn().apply { unpack(part) }
        val locals = exportedLocalNames.getOrPut(function) { ArrayList() }
        for (position in 0 until names.size) locals += string(names[position])
    }

    fun build(
        dataSource: DataSource?,
    ): ModuleDeclarations {
        check(!built) { "the declarations were already built" }
        built = true
        check(stack.isEmpty()) { "${stack.size} values left on the stack" }
        groups.forEachIndexed { index, group -> check(group.subTypes.isNotEmpty()) { "group $index has no body" } }
        val unnamed = moduleName == null && functionNameIndices.size == 0 && exportedLocalNames.isEmpty() && trapNameIndices.size == 0
        val names = if (unnamed) {
            ModuleNames.NONE
        } else {
            ModuleNames(
                moduleName = moduleName,
                functionNameIndices = functionNameIndices.toArray(),
                functionNames = Array(functionNameStrings.size) { position -> string(functionNameStrings[position]) },
                exportedLocalNames = exportedLocalNames,
                trapNameIndices = trapNameIndices.toArray(),
                trapNames = Array(trapNameStrings.size) { position -> string(trapNameStrings[position]) },
            )
        }
        return ModuleDeclarations(
            definedTypes = definedTypes,
            imports = imports,
            functionTypeIndices = functionTypeIndices.toArray(),
            tables = tables,
            memories = memories,
            tags = tags,
            globals = globals,
            exports = exports(),
            startFunction = startFunction?.let { index -> Index.FunctionIndex(index.toUInt()) },
            elementSegments = elementSegments,
            dataSegments = dataSegments,
            names = names,
            dataSource = dataSource,
        )
    }

    private fun exports(): List<Export> {
        check(exportKinds.size == exportNames.size && exportIndices.size == exportNames.size) {
            "export columns differ in length: ${exportNames.size} names, ${exportKinds.size} kinds, ${exportIndices.size} indices"
        }
        return List(exportNames.size) { position ->
            Export(
                name = NameValue(string(exportNames[position])),
                descriptor = ExportKinds.descriptor(exportKinds[position], exportIndices[position]),
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> pop(): T = stack.removeAt(stack.lastIndex) as T

    @Suppress("UNCHECKED_CAST")
    private fun <T> popList(
        count: Int,
    ): List<T> {
        check(count <= stack.size) { "$count values needed, ${stack.size} on the stack" }
        val top = stack.subList(stack.size - count, stack.size)
        val values = ArrayList<T>(top as List<T>)
        top.clear()
        return values
    }

    private fun addressType(
        address64: Boolean,
    ): AddressType = if (address64) AddressType.I64 else AddressType.I32
}
