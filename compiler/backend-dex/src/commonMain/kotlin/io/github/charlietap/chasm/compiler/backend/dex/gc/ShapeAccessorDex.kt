package io.github.charlietap.chasm.compiler.backend.dex.gc

import io.github.charlietap.chasm.compiler.backend.abi.ACCESSOR_CLASS_SUFFIX
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_ACCESSOR
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_ACCESSOR_NEW
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_ACCESSOR_NEW_STORAGE
import io.github.charlietap.chasm.compiler.backend.abi.CREATE_FIELD_INDEX_ERROR
import io.github.charlietap.chasm.compiler.backend.abi.CREATE_NUMBER_FIELD_ERROR
import io.github.charlietap.chasm.compiler.backend.abi.CREATE_REFERENCE_FIELD_ERROR
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_STRUCT
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_ACCESSOR
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_ACCESSOR_NEW
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_ACCESSOR_READ_BITS
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_ACCESSOR_READ_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_ACCESSOR_WRITE_BITS
import io.github.charlietap.chasm.compiler.backend.abi.STRUCT_ACCESSOR_WRITE_REFERENCE
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ShapeField
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
import io.github.charlietap.chasm.compiler.backend.dex.abi.referenceType
import io.github.charlietap.chasm.compiler.backend.dex.abi.valueType
import io.github.charlietap.dexter.ArrayType
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
import io.github.charlietap.dexter.Dexter
import io.github.charlietap.dexter.Label
import io.github.charlietap.dexter.LiteralOp
import io.github.charlietap.dexter.Reg

class ShapeAccessorDex {

    fun struct(
        shapeDescriptor: String,
        fields: List<ShapeField>,
    ): ByteArray {

        val dexter = Dexter()
        val file = dexter.beginFile()
        val type = ClassType(shapeDescriptor)
        val dexFields = fields.map { field -> DexField(ClassType(field.owner), field.name, valueType(field.descriptor)) }
        val accessorType = accessorType(type)
        val accessor = accessorClass(file, accessorType, STRUCT_ACCESSOR)

        writeReadBits(accessor, accessorType, type, fields, dexFields)
        writeReadReference(accessor, accessorType, type, fields, dexFields)
        writeWriteBits(accessor, accessorType, type, fields, dexFields)
        writeWriteReference(accessor, accessorType, type, fields, dexFields)
        writeNewStruct(accessor, accessorType, type)

        return file.finish()
    }

    fun array(
        shapeDescriptor: String,
        dataDescriptor: String,
    ): ByteArray {

        val dexter = Dexter()
        val file = dexter.beginFile()
        val type = ClassType(shapeDescriptor)
        val dataType = referenceType(dataDescriptor) as ArrayType
        val accessorType = accessorType(type)
        val accessor = accessorClass(file, accessorType, ARRAY_ACCESSOR)

        writeNewStorage(accessor, accessorType, dataType)
        writeNewArray(accessor, accessorType, type, dataType)

        return file.finish()
    }

    private fun accessorType(shapeType: ClassType): ClassType {
        return ClassType(shapeType.descriptor.removeSuffix(";") + ACCESSOR_CLASS_SUFFIX + ";")
    }

    private fun accessorClass(
        file: DexFile,
        type: ClassType,
        accessorInterface: String,
    ): ClassBuilder {

        val builder = file.addClass(type, DexType.Object, ClassType(accessorInterface), access = ClassAccess.PublicFinal)

        val code = builder.beginConstructor(DexConstructor(type), registerCount = 1)
        code.invokeConstructor(DexConstructor(DexType.Object), code.self)
        code.returnVoid()
        code.finish()

        return builder
    }

    private fun writeReadBits(
        builder: ClassBuilder,
        accessorType: ClassType,
        type: ClassType,
        fields: List<ShapeField>,
        dexFields: List<DexField>,
    ) {

        val code = builder.beginInstanceMethod(DexMethod(accessorType, STRUCT_ACCESSOR_READ_BITS, DexType.Long, DexType.Object, DexType.Int), registerCount = 5)
        val struct = code.parameter(0)
        val index = code.parameter(1)
        code.checkCast(struct, type)
        val cases = switchOnFieldIndex(code, index, dexFields.size)

        fields.forEachIndexed { position, field ->

            val dexField = dexFields[position]
            code.bind(cases[position])

            if (holdsReference(field)) {
                throwFieldError(code, CREATE_REFERENCE_FIELD_ERROR, index)
            } else {
                when (field.descriptor) {
                    "J" -> {
                        code.getField(WIDE_RESULT, struct, dexField)
                    }
                    "D" -> {
                        code.getField(WIDE_RESULT, struct, dexField)
                        code.invokeStatic(DOUBLE_TO_RAW_LONG_BITS.method, WIDE_RESULT)
                        code.moveResult(WIDE_RESULT)
                    }
                    "F" -> {
                        code.getField(RESULT, struct, dexField)
                        code.invokeStatic(FLOAT_TO_RAW_INT_BITS.method, RESULT)
                        code.moveResult(RESULT)
                        code.convert(Conversion.IntToLong, WIDE_RESULT, RESULT)
                    }
                    "B" -> {
                        code.getField(RESULT, struct, dexField)
                        code.binary(LiteralOp.AndInt, RESULT, RESULT, BYTE_MASK)
                        code.convert(Conversion.IntToLong, WIDE_RESULT, RESULT)
                    }
                    else -> {
                        code.getField(RESULT, struct, dexField)
                        code.convert(Conversion.IntToLong, WIDE_RESULT, RESULT)
                    }
                }
                code.returnValue(WIDE_RESULT)
            }
        }
        code.finish()
    }

    private fun writeReadReference(
        builder: ClassBuilder,
        accessorType: ClassType,
        type: ClassType,
        fields: List<ShapeField>,
        dexFields: List<DexField>,
    ) {

        val code = builder.beginInstanceMethod(DexMethod(accessorType, STRUCT_ACCESSOR_READ_REFERENCE, DexType.Object, DexType.Object, DexType.Int), registerCount = 4)
        val struct = code.parameter(0)
        val index = code.parameter(1)
        code.checkCast(struct, type)
        val cases = switchOnFieldIndex(code, index, dexFields.size)

        fields.forEachIndexed { position, field ->

            code.bind(cases[position])

            if (holdsReference(field)) {
                code.getField(RESULT, struct, dexFields[position])
                code.returnValue(RESULT)
            } else {
                throwFieldError(code, CREATE_NUMBER_FIELD_ERROR, index)
            }
        }
        code.finish()
    }

    private fun writeWriteBits(
        builder: ClassBuilder,
        accessorType: ClassType,
        type: ClassType,
        fields: List<ShapeField>,
        dexFields: List<DexField>,
    ) {

        val code = builder.beginInstanceMethod(DexMethod(accessorType, STRUCT_ACCESSOR_WRITE_BITS, DexType.Void, DexType.Object, DexType.Int, DexType.Long), registerCount = 7)
        val struct = code.parameter(0)
        val index = code.parameter(1)
        val value = code.parameter(2)
        code.checkCast(struct, type)
        val cases = switchOnFieldIndex(code, index, dexFields.size)

        fields.forEachIndexed { position, field ->

            val dexField = dexFields[position]
            code.bind(cases[position])

            if (holdsReference(field)) {
                throwFieldError(code, CREATE_REFERENCE_FIELD_ERROR, index)
            } else {
                when (field.descriptor) {
                    "J" -> {
                        code.putField(value, struct, dexField)
                    }
                    "D" -> {
                        code.invokeStatic(LONG_BITS_TO_DOUBLE.method, value)
                        code.moveResult(WIDE_RESULT)
                        code.putField(WIDE_RESULT, struct, dexField)
                    }
                    "F" -> {
                        code.convert(Conversion.LongToInt, RESULT, value)
                        code.invokeStatic(INT_BITS_TO_FLOAT.method, RESULT)
                        code.moveResult(RESULT)
                        code.putField(RESULT, struct, dexField)
                    }
                    else -> {
                        code.convert(Conversion.LongToInt, RESULT, value)
                        code.putField(RESULT, struct, dexField)
                    }
                }
                code.returnVoid()
            }
        }
        code.finish()
    }

    private fun writeWriteReference(
        builder: ClassBuilder,
        accessorType: ClassType,
        type: ClassType,
        fields: List<ShapeField>,
        dexFields: List<DexField>,
    ) {

        val code = builder.beginInstanceMethod(DexMethod(accessorType, STRUCT_ACCESSOR_WRITE_REFERENCE, DexType.Void, DexType.Object, DexType.Int, DexType.Object), registerCount = 5)
        val struct = code.parameter(0)
        val index = code.parameter(1)
        val value = code.parameter(2)
        code.checkCast(struct, type)
        val cases = switchOnFieldIndex(code, index, dexFields.size)

        fields.forEachIndexed { position, field ->

            code.bind(cases[position])

            if (holdsReference(field)) {
                code.moveObject(RESULT, value)
                code.checkCast(RESULT, referenceType(field.descriptor))
                code.putField(RESULT, struct, dexFields[position])
                code.returnVoid()
            } else {
                throwFieldError(code, CREATE_NUMBER_FIELD_ERROR, index)
            }
        }
        code.finish()
    }

    private fun writeNewStruct(
        builder: ClassBuilder,
        accessorType: ClassType,
        type: ClassType,
    ) {

        val code = builder.beginInstanceMethod(DexMethod(accessorType, STRUCT_ACCESSOR_NEW, ClassType(MANAGED_STRUCT)), registerCount = 2)

        code.newInstance(RESULT, type)
        code.invokeConstructor(DexConstructor(type), RESULT)
        code.returnValue(RESULT)
        code.finish()
    }

    private fun writeNewStorage(
        builder: ClassBuilder,
        accessorType: ClassType,
        dataType: ArrayType,
    ) {

        val code = builder.beginInstanceMethod(DexMethod(accessorType, ARRAY_ACCESSOR_NEW_STORAGE, DexType.Object, DexType.Int), registerCount = 3)

        code.newArray(RESULT, code.parameter(0), dataType)
        code.returnValue(RESULT)
        code.finish()
    }

    private fun writeNewArray(
        builder: ClassBuilder,
        accessorType: ClassType,
        type: ClassType,
        dataType: ArrayType,
    ) {

        val code = builder.beginInstanceMethod(DexMethod(accessorType, ARRAY_ACCESSOR_NEW, ClassType(MANAGED_ARRAY), DexType.Object), registerCount = 3)
        val storageArgument = code.parameter(0)

        code.checkCast(storageArgument, dataType)
        code.newInstance(RESULT, type)
        code.invokeConstructor(DexConstructor(type, dataType), RESULT, storageArgument)
        code.returnValue(RESULT)
        code.finish()
    }

    private fun switchOnFieldIndex(
        code: CodeEmitter,
        index: Reg,
        count: Int,
    ): List<Label> {

        val cases = List(count) { code.label() }
        if (count > 0) {
            val table = code.labelTable(0, count, cases[0])
            cases.forEachIndexed { position, label -> table[position] = label }
            code.packedSwitch(index, table)
        }
        throwFieldError(code, CREATE_FIELD_INDEX_ERROR, index)

        return cases
    }

    private fun throwFieldError(
        code: CodeEmitter,
        createError: MethodRef,
        index: Reg,
    ) {
        code.invokeStatic(createError.method, index)
        code.moveResult(RESULT)
        code.throwValue(RESULT)
    }

    private fun holdsReference(field: ShapeField): Boolean {
        return field.descriptor.startsWith("L") || field.descriptor.startsWith("[")
    }

    private companion object {
        val RESULT = Reg(0)
        val WIDE_RESULT = Reg(0)
        const val BYTE_MASK = 0xFF
    }
}
