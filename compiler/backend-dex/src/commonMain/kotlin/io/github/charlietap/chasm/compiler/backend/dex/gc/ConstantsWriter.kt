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
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.I31_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_GLOBAL
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_MANAGED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_REFERENCE_GLOBAL
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_REFERENCE_SEGMENT
import io.github.charlietap.chasm.compiler.backend.abi.INSTALLATION_REFERENCE_TABLE
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
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
import io.github.charlietap.chasm.compiler.backend.dex.abi.referenceType
import io.github.charlietap.chasm.compiler.backend.dex.abi.valueType
import io.github.charlietap.chasm.compiler.backend.dex.lower.Unsupported
import io.github.charlietap.chasm.type.NumberType
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
import io.github.charlietap.dexter.Reg

internal class ConstantsWriter(
    private val module: Module,
    private val layouts: GcLayouts,
) {
    private val importedGlobals = module.imports.mapNotNull { import -> (import.descriptor as? Import.Descriptor.Global)?.type }
    private val importedTables = module.imports.count { import -> import.descriptor is Import.Descriptor.Table }
    private val globalTypes = importedGlobals.map { type -> type.valueType } + module.globals.map { global -> global.type.valueType }

    val needed: Boolean = module.globals.any { global -> global.type.valueType is ValueType.Reference && needsCode(global.initExpression) } ||
        module.tables.any { table -> needsCode(table.initExpression) } ||
        module.elementSegments.any { segment -> segment.initExpressions.any(::needsCode) }

    fun write(
        file: DexFile,
        owner: String,
    ) {

        val type = ClassType(owner)
        val builder = file.addClass(type, DexType.Object, CONSUMER, access = ClassAccess.PublicFinal)
        val initializers = ArrayList<DexMethod>()

        module.globals.forEachIndexed { defined, global ->
            if (global.type.valueType is ValueType.Reference && needsCode(global.initExpression)) {
                initializers += globalInitializer(builder, type, importedGlobals.size + defined, global.initExpression)
            }
        }
        module.tables.forEachIndexed { defined, table ->
            if (needsCode(table.initExpression)) {
                initializers += tableInitializer(builder, type, importedTables + defined, table.initExpression)
            }
        }
        module.elementSegments.forEachIndexed { index, segment ->
            if (segment.initExpressions.any(::needsCode)) {
                initializers += elementInitializer(builder, type, index, segment.initExpressions)
            }
        }

        builder.beginConstructor(DexConstructor(type), registerCount = 1).apply {
            invokeConstructor(DexConstructor(DexType.Object), self)
            returnVoid()
            finish()
        }

        builder.beginInstanceMethod(DexMethod(type, "accept", DexType.Void, DexType.Object), registerCount = 2).apply {
            val installation = parameter(0)
            checkCast(installation, ClassType(CODE_INSTALLATION))
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
        val code = builder.beginStaticMethod(method, registerCount = registersFor(expression) + 1)
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
        val code = builder.beginStaticMethod(method, registerCount = registersFor(expression) + 1)
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
            val code = builder.beginStaticMethod(method, registerCount = members.maxOf(::registersFor) + 2)
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
        val code = builder.beginStaticMethod(method, registerCount = BASE + 1)
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

    private fun needsCode(expression: Expression): Boolean {
        return expression.instructions.any { instruction ->
            when (instruction) {
                is AggregateInstruction.StructNew,
                is AggregateInstruction.StructNewDefault,
                is AggregateInstruction.ArrayNew,
                is AggregateInstruction.ArrayNewDefault,
                is AggregateInstruction.ArrayNewFixed,
                AggregateInstruction.RefI31,
                -> true
                is VariableInstruction.GlobalGet -> globalTypes[instruction.globalIdx.idx.toInt()] is ValueType.Reference
                else -> false
            }
        }
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
            is AggregateInstruction.StructNew -> 1 - layouts.struct(instruction.typeIndex.idx.toInt()).fields.size
            is AggregateInstruction.StructNewDefault -> 1
            is AggregateInstruction.ArrayNew -> -1
            is AggregateInstruction.ArrayNewFixed -> 1 - instruction.size.toInt()
            NumericInstruction.I32Add, NumericInstruction.I32Sub, NumericInstruction.I32Mul,
            NumericInstruction.I64Add, NumericInstruction.I64Sub, NumericInstruction.I64Mul,
            -> -1
            else -> 0
        }
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

            val shape = layouts.struct(type)
            val first = kinds.size - if (withFields) shape.fields.size else 0
            val owner = ClassType(shape.className)
            code.newInstance(TARGET, owner)
            code.invokeConstructor(DexConstructor(owner), TARGET)

            if (withFields) {
                shape.fields.forEachIndexed { index, field ->
                    val value = storageValue(field.descriptor, slot(first + index))
                    code.putField(value, TARGET, DexField(ClassType(field.owner), field.name, valueType(field.descriptor)))
                }
            }

            replace(first, TARGET)
        }

        private fun arrayNew(type: Int) {

            val shape = layouts.array(type)
            val element = shape.data.descriptor.substring(1)
            val primitive = element.length == 1
            val fill = MethodRef(
                ARRAYS,
                "fill",
                listOf(if (primitive) shape.data.descriptor else "[$OBJECT_TYPE", if (primitive) element else OBJECT_TYPE),
                "V",
            ).method

            code.move(INDEX, slot(kinds.size - 1))
            checkLength(element)
            code.newArray(STORAGE, INDEX, referenceType(shape.data.descriptor) as ArrayType)
            code.invokeStatic(fill, STORAGE, storageValue(element, slot(kinds.size - 2)))

            wrap(shape, kinds.size - 2)
        }

        private fun arrayNewDefault(type: Int) {

            val shape = layouts.array(type)
            code.move(INDEX, slot(kinds.size - 1))
            checkLength(shape.data.descriptor.substring(1))
            code.newArray(STORAGE, INDEX, referenceType(shape.data.descriptor) as ArrayType)

            wrap(shape, kinds.size - 1)
        }

        private fun arrayNewFixed(
            type: Int,
            size: Int,
        ) {

            val shape = layouts.array(type)
            val element = shape.data.descriptor.substring(1)
            val first = kinds.size - size
            code.const(INDEX, size)
            code.newArray(STORAGE, INDEX, referenceType(shape.data.descriptor) as ArrayType)

            for (index in 0 until size) {
                val value = storageValue(element, slot(first + index))
                code.const(INDEX, index)
                code.arrayPut(arrayKind(element), value, STORAGE, INDEX)
            }

            wrap(shape, first)
        }

        private fun checkLength(
            element: String,
        ) {
            val shift = when (element) {
                "B" -> 0
                "C" -> 1
                "J", "D" -> 3
                else -> 2
            }
            code.const(RIGHT, shift)
            code.invokeStatic(CHECK_ARRAY_LENGTH.method, INDEX, RIGHT)
            code.moveResult(INDEX)
        }

        private fun wrap(
            shape: ArrayShape,
            first: Int,
        ) {

            val owner = ClassType(shape.className)
            code.newInstance(TARGET, owner)
            code.invokeConstructor(DexConstructor(owner, referenceType(shape.data.descriptor)), TARGET, STORAGE)

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
                    if (descriptor != OBJECT_TYPE) code.checkCast(VALUE, referenceType(descriptor))
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
        val INSTALLATION = ClassType(CODE_INSTALLATION)
        val CONSUMER = ClassType("Ljava/util/function/Consumer;")
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
    }
}
