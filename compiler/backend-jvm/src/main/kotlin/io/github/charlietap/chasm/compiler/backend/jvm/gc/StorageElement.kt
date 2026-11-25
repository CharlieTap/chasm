package io.github.charlietap.chasm.compiler.backend.jvm.gc

import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ArrayElementKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.arrayElementKind
import io.github.charlietap.chasm.type.FieldType
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal enum class StorageElement(
    val kind: ArrayElementKind,
    private val primitiveArrayType: Int?,
    val loadOpcode: Int,
    val storeOpcode: Int,
    val narrowingOpcode: Int?,
) {
    BYTE(ArrayElementKind.BYTE, Opcodes.T_BYTE, Opcodes.BALOAD, Opcodes.BASTORE, Opcodes.I2B),
    CHAR(ArrayElementKind.CHAR, Opcodes.T_CHAR, Opcodes.CALOAD, Opcodes.CASTORE, Opcodes.I2C),
    INT(ArrayElementKind.INT, Opcodes.T_INT, Opcodes.IALOAD, Opcodes.IASTORE, null),
    LONG(ArrayElementKind.LONG, Opcodes.T_LONG, Opcodes.LALOAD, Opcodes.LASTORE, null),
    FLOAT(ArrayElementKind.FLOAT, Opcodes.T_FLOAT, Opcodes.FALOAD, Opcodes.FASTORE, null),
    DOUBLE(ArrayElementKind.DOUBLE, Opcodes.T_DOUBLE, Opcodes.DALOAD, Opcodes.DASTORE, null),
    REFERENCE(ArrayElementKind.REFERENCE, null, Opcodes.AALOAD, Opcodes.AASTORE, null),
    ;

    val wholeFill: MethodRef = MethodRef(ARRAYS, "fill", listOf(kind.arrayDescriptor, kind.descriptor), "V")

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
    return when (arrayElementKind(type)) {
        ArrayElementKind.BYTE -> StorageElement.BYTE
        ArrayElementKind.CHAR -> StorageElement.CHAR
        ArrayElementKind.INT -> StorageElement.INT
        ArrayElementKind.LONG -> StorageElement.LONG
        ArrayElementKind.FLOAT -> StorageElement.FLOAT
        ArrayElementKind.DOUBLE -> StorageElement.DOUBLE
        ArrayElementKind.REFERENCE -> StorageElement.REFERENCE
    }
}

private const val ARRAYS = "Ljava/util/Arrays;"
