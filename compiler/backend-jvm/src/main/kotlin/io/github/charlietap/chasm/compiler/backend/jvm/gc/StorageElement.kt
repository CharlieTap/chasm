package io.github.charlietap.chasm.compiler.backend.jvm.gc

import io.github.charlietap.chasm.compiler.backend.abi.FILL_BYTES
import io.github.charlietap.chasm.compiler.backend.abi.FILL_CHARS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_DOUBLES
import io.github.charlietap.chasm.compiler.backend.abi.FILL_FLOATS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_INTS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_LONGS
import io.github.charlietap.chasm.compiler.backend.abi.FILL_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal enum class StorageElement(
    val descriptor: String,
    private val primitiveArrayType: Int?,
    val loadOpcode: Int,
    val storeOpcode: Int,
    val narrowingOpcode: Int?,
    val rangeFill: MethodRef,
) {
    BYTE("B", Opcodes.T_BYTE, Opcodes.BALOAD, Opcodes.BASTORE, Opcodes.I2B, FILL_BYTES),
    CHAR("C", Opcodes.T_CHAR, Opcodes.CALOAD, Opcodes.CASTORE, Opcodes.I2C, FILL_CHARS),
    INT("I", Opcodes.T_INT, Opcodes.IALOAD, Opcodes.IASTORE, null, FILL_INTS),
    LONG("J", Opcodes.T_LONG, Opcodes.LALOAD, Opcodes.LASTORE, null, FILL_LONGS),
    FLOAT("F", Opcodes.T_FLOAT, Opcodes.FALOAD, Opcodes.FASTORE, null, FILL_FLOATS),
    DOUBLE("D", Opcodes.T_DOUBLE, Opcodes.DALOAD, Opcodes.DASTORE, null, FILL_DOUBLES),
    REFERENCE(OBJECT_TYPE, null, Opcodes.AALOAD, Opcodes.AASTORE, null, FILL_OBJECTS),
    ;

    val arrayDescriptor: String = "[$descriptor"

    val wholeFill: MethodRef = MethodRef(ARRAYS, "fill", listOf(arrayDescriptor, descriptor), "V")

    fun newArray(visitor: MethodVisitor) {

        val arrayType = primitiveArrayType
        if (arrayType == null) {
            visitor.visitTypeInsn(Opcodes.ANEWARRAY, OBJECT_CLASS)
        } else {
            visitor.visitIntInsn(Opcodes.NEWARRAY, arrayType)
        }
    }

    fun narrow(visitor: MethodVisitor) {

        val opcode = narrowingOpcode
        if (opcode != null) {
            visitor.visitInsn(opcode)
        }
    }
}

internal fun storageElement(type: FieldType): StorageElement {
    return when (val storage = type.storageType) {
        is StorageType.Packed -> when (storage.type) {
            PackedType.I8 -> StorageElement.BYTE
            PackedType.I16 -> StorageElement.CHAR
        }
        is StorageType.Value -> when (val value = storage.type) {
            is ValueType.Number -> when (value.numberType) {
                NumberType.I32 -> StorageElement.INT
                NumberType.I64 -> StorageElement.LONG
                NumberType.F32 -> StorageElement.FLOAT
                NumberType.F64 -> StorageElement.DOUBLE
            }
            else -> StorageElement.REFERENCE
        }
    }
}

private const val ARRAYS = "Ljava/util/Arrays;"
