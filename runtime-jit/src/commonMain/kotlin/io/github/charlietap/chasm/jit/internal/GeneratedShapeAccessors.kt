package io.github.charlietap.chasm.jit.internal

import dalvik.system.InMemoryDexClassLoader
import io.github.charlietap.chasm.compiler.backend.abi.ACCESSOR_CLASS_SUFFIX
import io.github.charlietap.chasm.compiler.backend.dex.gc.ShapeAccessorDex
import io.github.charlietap.chasm.compiler.backend.dex.gc.ShapeField
import io.github.charlietap.chasm.runtime.compiled.gc.ArrayAccessor
import io.github.charlietap.chasm.runtime.compiled.gc.ShapeAccessors
import io.github.charlietap.chasm.runtime.compiled.gc.StructAccessor
import java.lang.reflect.Field
import java.nio.ByteBuffer

internal class GeneratedShapeAccessors(
    private val writer: ShapeAccessorDex = ShapeAccessorDex(),
) : ShapeAccessors {

    override fun struct(
        shape: Class<*>,
        fields: List<Field>,
    ): StructAccessor {

        val shapeFields = fields.map { field -> ShapeField(descriptor(field.declaringClass), field.name, descriptor(field.type)) }

        return load(shape, writer.struct(descriptor(shape), shapeFields)) as StructAccessor
    }

    override fun array(
        shape: Class<*>,
        data: Field,
    ): ArrayAccessor {
        return load(shape, writer.array(descriptor(shape), descriptor(data.type))) as ArrayAccessor
    }

    private fun load(
        shape: Class<*>,
        dex: ByteArray,
    ): Any {

        val loader = InMemoryDexClassLoader(ByteBuffer.wrap(dex), shape.classLoader)

        return loader.loadClass(shape.name + ACCESSOR_CLASS_SUFFIX).getDeclaredConstructor().newInstance()
    }

    private fun descriptor(type: Class<*>): String {
        return when {
            type.isArray -> type.name.replace('.', '/')
            type.isPrimitive -> primitiveDescriptor(type)
            else -> "L" + type.name.replace('.', '/') + ";"
        }
    }

    private fun primitiveDescriptor(type: Class<*>): String {
        return when (type) {
            Int::class.javaPrimitiveType -> "I"
            Long::class.javaPrimitiveType -> "J"
            Float::class.javaPrimitiveType -> "F"
            Double::class.javaPrimitiveType -> "D"
            Byte::class.javaPrimitiveType -> "B"
            Char::class.javaPrimitiveType -> "C"
            Short::class.javaPrimitiveType -> "S"
            else -> "Z"
        }
    }
}
