package io.github.charlietap.chasm.compiler.backend.dex.gc

import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY_LENGTH
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_ARRAY_STORAGE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_STRUCT
import io.github.charlietap.chasm.compiler.backend.dex.abi.referenceType
import io.github.charlietap.chasm.compiler.backend.dex.abi.valueType
import io.github.charlietap.dexter.ClassAccess
import io.github.charlietap.dexter.ClassType
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

        shape.fields
            .filter { field -> field.owner == shape.className }
            .forEach { field -> builder.addInstanceField(DexField(type, field.name, valueType(field.descriptor))) }

        val code = builder.beginConstructor(DexConstructor(type), registerCount = 1)
        code.invokeConstructor(DexConstructor(superclass), code.self)
        code.returnVoid()
        code.finish()
    }

    private fun writeArray(
        file: DexFile,
        shape: ArrayShape,
    ) {

        val type = ClassType(shape.className)
        val superclass = ClassType(shape.parent?.className ?: MANAGED_ARRAY)
        val dataType = referenceType(shape.data.descriptor)
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
            val storage = builder.beginInstanceMethod(DexMethod(type, MANAGED_ARRAY_STORAGE, DexType.Object), registerCount = 2)
            storage.getField(STORAGE, storage.self, data)
            storage.returnValue(STORAGE)
            storage.finish()

            val length = builder.beginInstanceMethod(DexMethod(type, MANAGED_ARRAY_LENGTH, DexType.Int), registerCount = 2)
            length.getField(STORAGE, length.self, data)
            length.arrayLength(LENGTH, STORAGE)
            length.returnValue(LENGTH)
            length.finish()
        }
    }

    private fun access(final: Boolean): ClassAccess {
        return if (final) ClassAccess.PublicFinal else ClassAccess.Public
    }

    private companion object {
        val LENGTH = Reg(0)
        val STORAGE = Reg(0)
    }
}
