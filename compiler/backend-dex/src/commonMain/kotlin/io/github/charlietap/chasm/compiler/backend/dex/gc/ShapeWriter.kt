package io.github.charlietap.chasm.compiler.backend.dex.gc

import io.github.charlietap.chasm.compiler.backend.abi.CREATE_OWNER_COLLECTED_ERROR
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY_LENGTH
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY_STORAGE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_STRUCT
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_OWNER
import io.github.charlietap.chasm.compiler.backend.abi.SHAPE_OWNER_CLASS
import io.github.charlietap.chasm.compiler.backend.abi.SHAPE_OWNER_ENGINE
import io.github.charlietap.chasm.compiler.backend.abi.WEAK_REFERENCE_GET
import io.github.charlietap.chasm.compiler.backend.abi.WEAK_REFERENCE_TYPE
import io.github.charlietap.chasm.compiler.backend.dex.abi.method
import io.github.charlietap.chasm.compiler.backend.dex.abi.referenceType
import io.github.charlietap.chasm.compiler.backend.dex.abi.valueType
import io.github.charlietap.dexter.ArrayType
import io.github.charlietap.dexter.ClassAccess
import io.github.charlietap.dexter.ClassBuilder
import io.github.charlietap.dexter.ClassType
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.DexConstructor
import io.github.charlietap.dexter.DexField
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.DexMethod
import io.github.charlietap.dexter.DexType
import io.github.charlietap.dexter.Reg

internal class ShapeWriter {

    fun write(
        file: DexFile,
        layouts: GcLayouts,
    ) {

        if (layouts.structs.isEmpty() && layouts.arrays.isEmpty()) {
            return
        }

        file.addClass(ClassType(SHAPE_OWNER_CLASS), DexType.Object, access = ClassAccess.PublicFinal).addStaticField(OWNER_ENGINE)
        layouts.structs.forEach { shape -> writeStruct(file, shape) }
        layouts.arrays.forEach { shape -> writeArray(file, shape) }
    }

    private fun writeStruct(
        file: DexFile,
        shape: StructShape,
    ) {

        val type = ClassType(shape.className)
        val superclass = ClassType(shape.parent?.className ?: MANAGED_STRUCT)
        val builder = file.addClass(type, superclass, access = access(shape.final))
        val dexFields = shape.fields.map { gcField -> DexField(ClassType(gcField.owner), gcField.name, valueType(gcField.descriptor)) }

        shape.fields.zip(dexFields)
            .filter { (gcField, _) -> gcField.owner == shape.className }
            .forEach { (_, dexField) -> builder.addInstanceField(dexField) }

        val code = builder.beginConstructor(DexConstructor(type), registerCount = 1)
        code.invokeConstructor(DexConstructor(superclass), code.self)
        code.returnVoid()
        code.finish()

        if (shape.parent == null) {
            writeOwner(builder, type)
        }
    }

    private fun writeArray(
        file: DexFile,
        shape: ArrayShape,
    ) {

        val type = ClassType(shape.className)
        val superclass = ClassType(shape.parent?.className ?: MANAGED_ARRAY)
        val dataType = referenceType(shape.data.descriptor) as ArrayType
        val builder = file.addClass(type, superclass, access = access(shape.final))
        val data = DexField(ClassType(shape.data.owner), shape.data.name, dataType)

        if (shape.parent == null) {
            builder.addInstanceField(data)
        }

        val code = builder.beginConstructor(DexConstructor(type, dataType), registerCount = 3)
        val elements = code.parameter(0)
        if (shape.parent == null) {
            code.invokeConstructor(DexConstructor(superclass), code.self)
            code.putField(elements, code.self, data)
        } else {
            code.invokeConstructor(DexConstructor(superclass, dataType), code.self, elements)
        }
        code.returnVoid()
        code.finish()

        if (shape.parent == null) {
            writeOwner(builder, type)
            writeStorage(builder, type, data)
            writeLength(builder, type, data)
        }
    }

    private fun writeStorage(
        builder: ClassBuilder,
        type: ClassType,
        data: DexField,
    ) {

        val code = builder.beginInstanceMethod(DexMethod(type, MANAGED_ARRAY_STORAGE, DexType.Object), registerCount = 2)

        code.getField(RESULT, code.self, data)
        code.returnValue(RESULT)
        code.finish()
    }

    private fun writeLength(
        builder: ClassBuilder,
        type: ClassType,
        data: DexField,
    ) {

        val code = builder.beginInstanceMethod(DexMethod(type, MANAGED_ARRAY_LENGTH, DexType.Int), registerCount = 2)

        code.getField(RESULT, code.self, data)
        code.arrayLength(RESULT, RESULT)
        code.returnValue(RESULT)
        code.finish()
    }

    private fun writeOwner(
        builder: ClassBuilder,
        type: ClassType,
    ) {

        val code = builder.beginInstanceMethod(DexMethod(type, REFERENCE_OWNER, DexType.Object), registerCount = 2)
        val collected = code.label()

        code.getStatic(RESULT, OWNER_ENGINE)
        code.checkCast(RESULT, WEAK_REFERENCE)
        code.invokeVirtual(WEAK_REFERENCE_GET.method, RESULT)
        code.moveResult(RESULT)
        code.branchZero(Cond.Eq, RESULT, collected)
        code.returnValue(RESULT)
        code.bind(collected)
        code.invokeStatic(CREATE_OWNER_COLLECTED_ERROR.method)
        code.moveResult(RESULT)
        code.throwValue(RESULT)
        code.finish()
    }

    private fun access(final: Boolean): ClassAccess {
        return if (final) ClassAccess.PublicFinal else ClassAccess.Public
    }

    private companion object {
        val RESULT = Reg(0)
        val WIDE_RESULT = Reg(0)
        val OWNER_ENGINE = DexField(ClassType(SHAPE_OWNER_CLASS), SHAPE_OWNER_ENGINE, DexType.Object)
        val WEAK_REFERENCE = ClassType(WEAK_REFERENCE_TYPE)
        const val BYTE_MASK = 0xFF
    }
}
