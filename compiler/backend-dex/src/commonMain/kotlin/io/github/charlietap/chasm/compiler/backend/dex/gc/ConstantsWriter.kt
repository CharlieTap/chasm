package io.github.charlietap.chasm.compiler.backend.dex.gc

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.backend.abi.CHECK_ARRAY_LENGTH
import io.github.charlietap.chasm.compiler.backend.abi.CODE_INSTALLATION
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.I31_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_GLOBAL
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_MANAGED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_REFERENCE_GLOBAL
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_REFERENCE_SEGMENT
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_REFERENCE_TABLE
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_RTT
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_GLOBAL
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_GLOBAL_VALUE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_SEGMENT
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_STORAGE_ELEMENTS
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TABLE
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ArrayElementKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ConstantExpressions
import io.github.charlietap.chasm.compiler.backend.analysis.gc.FieldStorage
import io.github.charlietap.chasm.compiler.backend.analysis.gc.Lane
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructStorage
import io.github.charlietap.chasm.compiler.backend.analysis.gc.arrayElementKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.subType
import io.github.charlietap.chasm.compiler.backend.dex.MetadataFiles
import io.github.charlietap.chasm.compiler.backend.dex.abi.BINDING_TYPE
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
import io.github.charlietap.chasm.compiler.backend.dex.abi.referenceType
import io.github.charlietap.chasm.compiler.backend.dex.lower.Unsupported
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.ArrayType
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.ClassAccess
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.CodeEmitter
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.DexConstructor
import io.github.charlietap.dexter.DexField
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.LiteralOp
import io.github.charlietap.dexter.MemberAccess
import io.github.charlietap.dexter.Reg
import io.github.charlietap.chasm.type.FieldType as WasmFieldType

internal class ConstantsWriter(
    private val module: Module,
    private val storagePlans: StoragePlans,
    private val expressions: ConstantExpressions,
) {
    private val importedGlobals = module.imports.mapNotNull { import -> (import.descriptor as? Import.Descriptor.Global)?.type }
    private val importedTables = module.imports.count { import -> import.descriptor is Import.Descriptor.Table }
    private val globalTypes = importedGlobals.map { type -> type.valueType } + module.globals.map { global -> global.type.valueType }

    fun write(
        files: MetadataFiles,
        owner: String,
    ) {
        files.write { file -> writeClass(file, owner) }
    }

    private fun writeClass(
        file: DexFile,
        owner: String,
    ) {

        val type = ClassType(owner)
        val builder = file.addClass(type, DexType.Object, access = ClassAccess.PackageFinal)
        val initializers = ArrayList<DexMethod>()

        expressions.globals.forEach { defined ->
            initializers += globalInitializer(builder, type, importedGlobals.size + defined, module.globals[defined].initExpression)
        }
        expressions.tables.forEach { defined ->
            initializers += tableInitializer(builder, type, importedTables + defined, module.tables[defined].initExpression)
        }
        expressions.elements.forEach { index ->
            initializers += elementInitializer(builder, type, index, module.elementSegments[index].initExpressions)
        }

        val initialize = DexMethod(type, INITIALIZE, DexType.Void, INSTALLATION, BINDING)
        builder.beginStaticMethod(initialize, registerCount = 2, access = MemberAccess.Package).apply {
            val installation = parameter(0)
            initializers.forEach { initializer -> invokeStatic(initializer, installation) }
            returnVoid()
            finish()
        }
    }

    private fun globalInitializer(
        builder: ClassBuilder,
        owner: ClassType,
        global: Int,
        expression: Expression,
    ): DexMethod {

        val method = DexMethod(owner, "global$global", DexType.Void, INSTALLATION)
        val code = builder.beginStaticMethod(method, registerCount = registersFor(expression) + 1, access = MemberAccess.Private)
        code.moveObject(INSTALLATION_REGISTER, code.parameter(0))
        val installation = INSTALLATION_REGISTER
        val value = Evaluator(code, installation).evaluate(expression)

        code.const(INDEX, global)
        code.invokeVirtual(INSTALLATION_REFERENCE_GLOBAL.method, installation, INDEX)
        code.moveResult(TARGET)
        code.checkCast(TARGET, REFERENCE_GLOBAL_TYPE)
        code.moveObject(VALUE, value)
        code.putField(VALUE, TARGET, GLOBAL_VALUE_FIELD)
        code.returnVoid()
        code.finish()

        return method
    }

    private fun tableInitializer(
        builder: ClassBuilder,
        owner: ClassType,
        table: Int,
        expression: Expression,
    ): DexMethod {

        val method = DexMethod(owner, "table$table", DexType.Void, INSTALLATION)
        val code = builder.beginStaticMethod(method, registerCount = registersFor(expression) + 1, access = MemberAccess.Private)
        code.moveObject(INSTALLATION_REGISTER, code.parameter(0))
        val installation = INSTALLATION_REGISTER
        val value = Evaluator(code, installation).evaluate(expression)

        code.const(INDEX, table)
        code.invokeVirtual(INSTALLATION_REFERENCE_TABLE.method, installation, INDEX)
        code.moveResult(TARGET)
        code.checkCast(TARGET, REFERENCE_TABLE_TYPE)
        code.getField(STORAGE, TARGET, TABLE_ELEMENTS_FIELD)
        code.moveObject(VALUE, value)
        code.invokeStatic(FILL_OBJECTS, STORAGE, VALUE)
        code.returnVoid()
        code.finish()

        return method
    }

    private fun elementInitializer(
        builder: ClassBuilder,
        owner: ClassType,
        element: Int,
        expressions: List<Expression>,
    ): DexMethod {

        val chunks = expressions.chunked(EXPRESSIONS_PER_METHOD)
        val chunkMethods = chunks.mapIndexed { chunk, members ->
            val method = DexMethod(owner, "element${element}_$chunk", DexType.Void, INSTALLATION, OBJECTS)
            val code = builder.beginStaticMethod(method, registerCount = members.maxOf(::registersFor) + 2, access = MemberAccess.Private)
            code.moveObject(INSTALLATION_REGISTER, code.parameter(0))
            code.moveObject(SEGMENT, code.parameter(1))
            val installation = INSTALLATION_REGISTER
            val storage = SEGMENT
            members.forEachIndexed { offset, expression ->
                val value = Evaluator(code, installation).evaluate(expression)
                code.const(SLOT, chunk * EXPRESSIONS_PER_METHOD + offset)
                code.arrayPut(ArrayKind.Object, value, storage, SLOT)
            }
            code.returnVoid()
            code.finish()
            method
        }

        val method = DexMethod(owner, "element$element", DexType.Void, INSTALLATION)
        val code = builder.beginStaticMethod(method, registerCount = BASE + 1, access = MemberAccess.Private)
        code.moveObject(INSTALLATION_REGISTER, code.parameter(0))
        val installation = INSTALLATION_REGISTER
        code.const(INDEX, expressions.size)
        code.newArray(STORAGE, INDEX, OBJECTS)
        chunkMethods.forEach { chunk -> code.invokeStatic(chunk, installation, STORAGE) }
        code.const(INDEX, element)
        code.invokeVirtual(INSTALLATION_REFERENCE_SEGMENT.method, installation, INDEX)
        code.moveResult(TARGET)
        code.checkCast(TARGET, REFERENCE_SEGMENT_TYPE)
        code.putField(STORAGE, TARGET, SEGMENT_ELEMENTS_FIELD)
        code.returnVoid()
        code.finish()

        return method
    }

    private fun registersFor(expression: Expression): Int {
        return BASE + 2 * (depthOf(expression) + 1)
    }

    private fun depthOf(expression: Expression): Int {

        var depth = 0
        var deepest = 0
        expression.instructions.forEach { instruction ->
            depth += effect(instruction)
            deepest = maxOf(deepest, depth)
        }

        return deepest
    }

    private fun effect(instruction: Instruction): Int {
        return when (instruction) {
            is NumericInstruction.I32Const, is NumericInstruction.I64Const, is NumericInstruction.F32Const, is NumericInstruction.F64Const -> 1
            is ReferenceInstruction.RefNull, is ReferenceInstruction.RefFunc, is VariableInstruction.GlobalGet -> 1
            is AggregateInstruction.StructNew -> 1 - storagePlans.struct(instruction.typeIndex.idx.toInt()).fields.size
            is AggregateInstruction.StructNewDefault -> 1
            is AggregateInstruction.ArrayNew -> -1
            is AggregateInstruction.ArrayNewFixed -> 1 - instruction.size.toInt()
            NumericInstruction.I32Add, NumericInstruction.I32Sub, NumericInstruction.I32Mul,
            NumericInstruction.I64Add, NumericInstruction.I64Sub, NumericInstruction.I64Mul,
            -> -1
            else -> 0
        }
    }

    private fun arrayElement(type: Int): ArrayElementKind {

        val composite = module.definedTypes[type].subType().compositeType as CompositeType.Array

        return arrayElementKind(composite.arrayType.fieldType)
    }

    private inner class Evaluator(
        private val code: CodeEmitter,
        private val installation: Reg,
    ) {
        private val kinds = ArrayList<Kind>()

        fun evaluate(expression: Expression): Reg {
            expression.instructions.forEach(::instruction)
            return slot(kinds.size - 1)
        }

        private fun instruction(instruction: Instruction) {
            when (instruction) {
                is NumericInstruction.I32Const -> push(Kind.NARROW) { target -> code.const(target, instruction.value) }
                is NumericInstruction.I64Const -> push(Kind.WIDE) { target -> code.constWide(target, instruction.value) }
                is NumericInstruction.F32Const -> push(Kind.NARROW) { target -> code.const(target, instruction.bits) }
                is NumericInstruction.F64Const -> push(Kind.WIDE) { target -> code.constWide(target, instruction.bits) }
                NumericInstruction.I32Add -> binary(BinaryOp.AddInt)
                NumericInstruction.I32Sub -> binary(BinaryOp.SubInt)
                NumericInstruction.I32Mul -> binary(BinaryOp.MulInt)
                NumericInstruction.I64Add -> binary(BinaryOp.AddLong)
                NumericInstruction.I64Sub -> binary(BinaryOp.SubLong)
                NumericInstruction.I64Mul -> binary(BinaryOp.MulLong)
                is ReferenceInstruction.RefNull -> push(Kind.OBJECT) { target -> code.const(target, 0) }
                is ReferenceInstruction.RefFunc -> push(Kind.OBJECT) { target ->
                    code.const(INDEX, instruction.funcIdx.idx.toInt())
                    code.invokeVirtual(INSTALLATION_MANAGED_FUNCTION.method, installation, INDEX)
                    code.moveResult(target)
                    code.checkCast(target, ClassType(MANAGED_FUNCTION))
                }
                is VariableInstruction.GlobalGet -> globalGet(instruction.globalIdx.idx.toInt())
                is AggregateInstruction.StructNew -> structNew(instruction.typeIndex.idx.toInt(), withFields = true)
                is AggregateInstruction.StructNewDefault -> structNew(instruction.typeIndex.idx.toInt(), withFields = false)
                is AggregateInstruction.ArrayNew -> arrayNew(instruction.typeIndex.idx.toInt())
                is AggregateInstruction.ArrayNewDefault -> arrayNewDefault(instruction.typeIndex.idx.toInt())
                is AggregateInstruction.ArrayNewFixed -> arrayNewFixed(instruction.typeIndex.idx.toInt(), instruction.size.toInt())
                AggregateInstruction.RefI31 -> refI31()
                AggregateInstruction.AnyConvertExtern, AggregateInstruction.ExternConvertAny -> Unit
                else -> throw Unsupported("$instruction is not a managed constant expression")
            }
        }

        private fun globalGet(global: Int) {

            val type = globalTypes[global]
            code.const(INDEX, global)

            if (type is ValueType.Reference) {
                code.invokeVirtual(INSTALLATION_REFERENCE_GLOBAL.method, installation, INDEX)
                code.moveResult(TARGET)
                code.checkCast(TARGET, REFERENCE_GLOBAL_TYPE)
                return push(Kind.OBJECT) { target -> code.getField(target, TARGET, GLOBAL_VALUE_FIELD) }
            }

            code.invokeVirtual(INSTALLATION_GLOBAL.method, installation, INDEX)
            code.moveResult(TARGET)

            val number = (type as? ValueType.Number)?.numberType ?: throw Unsupported("global $global of type $type in a constant expression")
            code.invokeVirtual(GLOBAL_VALUE, TARGET)
            when (number) {
                NumberType.I32 -> push(Kind.NARROW) { target ->
                    code.moveResult(RIGHT_WIDE)
                    code.convert(Conversion.LongToInt, target, RIGHT_WIDE)
                }
                NumberType.I64 -> push(Kind.WIDE) { target -> code.moveResult(target) }
                NumberType.F32 -> push(Kind.NARROW) { target ->
                    code.moveResult(RIGHT_WIDE)
                    code.convert(Conversion.LongToInt, target, RIGHT_WIDE)
                    code.invokeStatic(INT_BITS_TO_FLOAT.method, target)
                    code.moveResult(target)
                }
                NumberType.F64 -> push(Kind.WIDE) { target ->
                    code.moveResult(target)
                    code.invokeStatic(LONG_BITS_TO_DOUBLE.method, target)
                    code.moveResult(target)
                }
            }
        }

        private fun structNew(
            type: Int,
            withFields: Boolean,
        ) {

            val storage = storagePlans.struct(type)
            val first = kinds.size - if (withFields) storage.fields.size else 0
            loadRtt(STORAGE, type)
            code.allocateStruct(storage, STORAGE, STRUCT_REGISTERS)

            if (withFields) {
                storage.fields.forEachIndexed { index, field -> putField(storage, field, slot(first + index)) }
            }

            replace(first, TARGET)
        }

        private fun putField(
            storage: StructStorage,
            field: FieldStorage,
            value: Reg,
        ) {

            val lanes = field.lanes
            if (lanes.size == 2) {
                code.moveWide(WIDE, value)
                if (numberType(field.type) == NumberType.F64) {
                    code.invokeStatic(DOUBLE_TO_RAW_LONG_BITS.method, WIDE)
                    code.moveResult(WIDE)
                }
                code.convert(Conversion.LongToInt, NARROW, WIDE)
                putLane(NARROW, storage, lanes[0])
                code.const(RIGHT, HALF_BITS)
                code.binary(BinaryOp.UshrLong, WIDE, WIDE, RIGHT)
                code.convert(Conversion.LongToInt, NARROW, WIDE)
                putLane(NARROW, storage, lanes[1])
                return
            }

            val lane = lanes.single()
            if (lane.kind == LaneKind.REFERENCE) {
                code.moveObject(VALUE, value)
                putLane(VALUE, storage, lane)
                return
            }

            code.move(NARROW, value)
            if (numberType(field.type) == NumberType.F32) {
                code.invokeStatic(FLOAT_TO_RAW_INT_BITS.method, NARROW)
                code.moveResult(NARROW)
            }
            code.maskPacked(field.type, NARROW)
            putLane(NARROW, storage, lane)
        }

        private fun putLane(
            value: Reg,
            storage: StructStorage,
            lane: Lane,
        ) {
            code.putLane(value, ClassType(storagePlans.laneOwner(storage, lane)), lane, STRUCT_REGISTERS)
        }

        private fun numberType(type: WasmFieldType): NumberType? {
            return ((type.storageType as? StorageType.Value)?.type as? ValueType.Number)?.numberType
        }

        private fun loadRtt(
            register: Reg,
            type: Int,
        ) {
            code.const(INDEX, type)
            code.invokeVirtual(INSTALLATION_RTT.method, installation, INDEX)
            code.moveResult(register)
            code.checkCast(register, RTT)
        }

        private fun arrayNew(type: Int) {

            val element = arrayElement(type)
            val primitive = element != ArrayElementKind.REFERENCE
            val fill = MethodRef(
                ARRAYS,
                "fill",
                listOf(if (primitive) element.arrayDescriptor else "[$OBJECT_TYPE", if (primitive) element.descriptor else OBJECT_TYPE),
                "V",
            ).method

            code.move(INDEX, slot(kinds.size - 1))
            checkLength(type)
            code.newArray(STORAGE, INDEX, referenceType(element.arrayDescriptor) as ArrayType)
            code.invokeStatic(fill, STORAGE, storageValue(element.descriptor, slot(kinds.size - 2)))

            wrap(type, element, kinds.size - 2)
        }

        private fun arrayNewDefault(type: Int) {

            val element = arrayElement(type)
            code.move(INDEX, slot(kinds.size - 1))
            checkLength(type)
            code.newArray(STORAGE, INDEX, referenceType(element.arrayDescriptor) as ArrayType)

            wrap(type, element, kinds.size - 1)
        }

        private fun arrayNewFixed(
            type: Int,
            size: Int,
        ) {

            val element = arrayElement(type)
            val first = kinds.size - size
            code.const(INDEX, size)
            code.newArray(STORAGE, INDEX, referenceType(element.arrayDescriptor) as ArrayType)

            for (index in 0 until size) {
                val value = storageValue(element.descriptor, slot(first + index))
                code.const(INDEX, index)
                code.arrayPut(arrayKind(element.descriptor), value, STORAGE, INDEX)
            }

            wrap(type, element, first)
        }

        private fun checkLength(type: Int) {
            code.const(RIGHT, storagePlans.array(type).elementShift)
            code.invokeStatic(CHECK_ARRAY_LENGTH.method, INDEX, RIGHT)
            code.moveResult(INDEX)
        }

        private fun wrap(
            type: Int,
            element: ArrayElementKind,
            first: Int,
        ) {

            val owner = ClassType(storagePlans.array(type).className)
            loadRtt(VALUE, type)
            code.newInstance(TARGET, owner)
            code.invokeConstructor(DexConstructor(owner, RTT, referenceType(element.arrayDescriptor)), TARGET, VALUE, STORAGE)

            replace(first, TARGET)
        }

        private fun refI31() {

            code.move(NARROW, slot(kinds.size - 1))
            code.binary(LiteralOp.ShlInt, NARROW, NARROW, literal = 1)
            code.binary(LiteralOp.UshrInt, NARROW, NARROW, literal = 1)
            code.newInstance(TARGET, ClassType(I31_REFERENCE))
            code.invokeConstructor(DexConstructor(ClassType(I31_REFERENCE), DexType.Int), TARGET, NARROW)

            replace(kinds.size - 1, TARGET)
        }

        private fun storageValue(
            descriptor: String,
            value: Reg,
        ): Reg {
            return when (descriptor) {
                "J", "D" -> {
                    code.moveWide(WIDE, value)
                    WIDE
                }
                "I", "F" -> {
                    code.move(NARROW, value)
                    NARROW
                }
                "B" -> {
                    code.move(NARROW, value)
                    code.convert(Conversion.IntToByte, NARROW, NARROW)
                    NARROW
                }
                "C" -> {
                    code.move(NARROW, value)
                    code.convert(Conversion.IntToChar, NARROW, NARROW)
                    NARROW
                }
                else -> {
                    code.moveObject(VALUE, value)
                    VALUE
                }
            }
        }

        private fun binary(operation: BinaryOp) {

            val right = slot(kinds.size - 1)
            val left = slot(kinds.size - 2)
            if (kinds.last() == Kind.WIDE) {
                code.moveWide(WIDE, left)
                code.moveWide(RIGHT_WIDE, right)
                code.binary(operation, WIDE, WIDE, RIGHT_WIDE)
                code.moveWide(left, WIDE)
            } else {
                code.move(NARROW, left)
                code.move(RIGHT, right)
                code.binary(operation, NARROW, NARROW, RIGHT)
                code.move(left, NARROW)
            }

            kinds.removeAt(kinds.size - 1)
        }

        private inline fun push(
            kind: Kind,
            emission: (Reg) -> Unit,
        ) {

            val target = slot(kinds.size)
            when (kind) {
                Kind.NARROW -> {
                    emission(NARROW)
                    code.move(target, NARROW)
                }
                Kind.WIDE -> {
                    emission(WIDE)
                    code.moveWide(target, WIDE)
                }
                Kind.OBJECT -> {
                    emission(VALUE)
                    code.moveObject(target, VALUE)
                }
            }

            kinds += kind
        }

        private fun replace(
            first: Int,
            result: Reg,
        ) {

            while (kinds.size > first) kinds.removeAt(kinds.size - 1)
            code.moveObject(slot(first), result)
            kinds += Kind.OBJECT
        }

        private fun slot(depth: Int): Reg {
            return Reg(BASE + 2 * depth)
        }
    }

    private enum class Kind {
        NARROW,
        WIDE,
        OBJECT,
    }

    private fun arrayKind(element: String): ArrayKind {
        return when (element) {
            "I", "F" -> ArrayKind.Int
            "J", "D" -> ArrayKind.Wide
            "B" -> ArrayKind.Byte
            "C" -> ArrayKind.Char
            else -> ArrayKind.Object
        }
    }

    private companion object {
        const val ARRAYS = "Ljava/util/Arrays;"
        const val INITIALIZE = "initialize"
        val INSTALLATION = ClassType(CODE_INSTALLATION)
        val BINDING = referenceType(BINDING_TYPE)
        val RTT = ClassType(RTT_TYPE)
        val OBJECTS = DexType.Object.array()
        val REFERENCE_GLOBAL_TYPE = ClassType(REFERENCE_GLOBAL)
        val REFERENCE_TABLE_TYPE = ClassType(REFERENCE_TABLE)
        val REFERENCE_SEGMENT_TYPE = ClassType(REFERENCE_SEGMENT)
        val GLOBAL_VALUE_FIELD = DexField(REFERENCE_GLOBAL_TYPE, REFERENCE_GLOBAL_VALUE, DexType.Object)
        val TABLE_ELEMENTS_FIELD = DexField(REFERENCE_TABLE_TYPE, REFERENCE_STORAGE_ELEMENTS, DexType.Object.array())
        val SEGMENT_ELEMENTS_FIELD = DexField(REFERENCE_SEGMENT_TYPE, REFERENCE_STORAGE_ELEMENTS, DexType.Object.array())
        val GLOBAL_VALUE = MethodRef(GLOBAL_INSTANCE, "getValue", emptyList(), "J").method
        val FILL_OBJECTS = MethodRef(ARRAYS, "fill", listOf("[$OBJECT_TYPE", OBJECT_TYPE), "V").method

        val WIDE = Reg(0)
        val RIGHT_WIDE = Reg(2)
        val NARROW = Reg(4)
        val RIGHT = Reg(5)
        val INDEX = Reg(6)
        val TARGET = Reg(7)
        val VALUE = Reg(8)
        val STORAGE = Reg(9)
        val SLOT = Reg(10)
        val INSTALLATION_REGISTER = Reg(11)
        val SEGMENT = Reg(12)
        const val BASE = 13
        const val EXPRESSIONS_PER_METHOD = 256
        val STRUCT_REGISTERS = StructRegisters(struct = TARGET, array = STORAGE, index = INDEX)
    }
}
