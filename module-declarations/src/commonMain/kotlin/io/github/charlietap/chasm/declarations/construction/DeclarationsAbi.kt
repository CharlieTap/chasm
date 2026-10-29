package io.github.charlietap.chasm.declarations.construction

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.NumberType

object HeapTypes {

    const val FUNC = 0
    const val NO_FUNC = 1
    const val EXTERN = 2
    const val NO_EXTERN = 3
    const val EXCEPTION = 4
    const val NO_EXCEPTION = 5
    const val ANY = 6
    const val EQ = 7
    const val STRUCT = 8
    const val ARRAY = 9
    const val I31 = 10
    const val NONE = 11

    fun abstract(
        tag: Int,
    ): AbstractHeapType = when (tag) {
        FUNC -> AbstractHeapType.Func
        NO_FUNC -> AbstractHeapType.NoFunc
        EXTERN -> AbstractHeapType.Extern
        NO_EXTERN -> AbstractHeapType.NoExtern
        EXCEPTION -> AbstractHeapType.Exception
        NO_EXCEPTION -> AbstractHeapType.NoException
        ANY -> AbstractHeapType.Any
        EQ -> AbstractHeapType.Eq
        STRUCT -> AbstractHeapType.Struct
        ARRAY -> AbstractHeapType.Array
        I31 -> AbstractHeapType.I31
        NONE -> AbstractHeapType.None
        else -> error("unknown abstract heap type $tag")
    }

    fun tag(
        type: AbstractHeapType,
    ): Int = when (type) {
        AbstractHeapType.Func -> FUNC
        AbstractHeapType.NoFunc -> NO_FUNC
        AbstractHeapType.Extern -> EXTERN
        AbstractHeapType.NoExtern -> NO_EXTERN
        AbstractHeapType.Exception -> EXCEPTION
        AbstractHeapType.NoException -> NO_EXCEPTION
        AbstractHeapType.Any -> ANY
        AbstractHeapType.Eq -> EQ
        AbstractHeapType.Struct -> STRUCT
        AbstractHeapType.Array -> ARRAY
        AbstractHeapType.I31 -> I31
        AbstractHeapType.None -> NONE
        is AbstractHeapType.Bottom -> throw IllegalArgumentException("heap type $type cannot be declared")
    }
}

object ValueTypes {

    const val I32 = 0
    const val I64 = 1
    const val F32 = 2
    const val F64 = 3

    const val STORAGE_VALUE = 0
    const val STORAGE_I8 = 1
    const val STORAGE_I16 = 2

    fun number(
        tag: Int,
    ): NumberType = when (tag) {
        I32 -> NumberType.I32
        I64 -> NumberType.I64
        F32 -> NumberType.F32
        F64 -> NumberType.F64
        else -> error("unknown number type $tag")
    }

    fun tag(
        type: NumberType,
    ): Int = when (type) {
        NumberType.I32 -> I32
        NumberType.I64 -> I64
        NumberType.F32 -> F32
        NumberType.F64 -> F64
    }
}

object ElementModes {
    const val PASSIVE = 0
    const val ACTIVE = 1
    const val DECLARATIVE = 2
}

object ExportKinds {

    const val FUNCTION = 0
    const val TABLE = 1
    const val MEMORY = 2
    const val GLOBAL = 3
    const val TAG = 4

    fun descriptor(
        kind: Int,
        index: Int,
    ): Export.Descriptor {
        val idx = index.toUInt()
        return when (kind) {
            FUNCTION -> Export.Descriptor.Function(Index.FunctionIndex(idx))
            TABLE -> Export.Descriptor.Table(Index.TableIndex(idx))
            MEMORY -> Export.Descriptor.Memory(Index.MemoryIndex(idx))
            GLOBAL -> Export.Descriptor.Global(Index.GlobalIndex(idx))
            TAG -> Export.Descriptor.Tag(Index.TagIndex(idx))
            else -> error("unknown export kind $kind")
        }
    }

    fun kind(
        descriptor: Export.Descriptor,
    ): Int = when (descriptor) {
        is Export.Descriptor.Function -> FUNCTION
        is Export.Descriptor.Table -> TABLE
        is Export.Descriptor.Memory -> MEMORY
        is Export.Descriptor.Global -> GLOBAL
        is Export.Descriptor.Tag -> TAG
    }

    fun index(
        descriptor: Export.Descriptor,
    ): Int = when (descriptor) {
        is Export.Descriptor.Function -> descriptor.functionIndex.idx
        is Export.Descriptor.Table -> descriptor.tableIndex.idx
        is Export.Descriptor.Memory -> descriptor.memoryIndex.idx
        is Export.Descriptor.Global -> descriptor.globalIndex.idx
        is Export.Descriptor.Tag -> descriptor.tagIndex.idx
    }.toInt()
}

object ConstantOps {

    const val I32_ADD = 0
    const val I32_SUB = 1
    const val I32_MUL = 2
    const val I64_ADD = 3
    const val I64_SUB = 4
    const val I64_MUL = 5
    const val REF_I31 = 6
    const val ANY_CONVERT_EXTERN = 7
    const val EXTERN_CONVERT_ANY = 8

    fun instruction(
        op: Int,
    ): ConstantInstruction = when (op) {
        I32_ADD -> ConstantInstruction.I32Add
        I32_SUB -> ConstantInstruction.I32Sub
        I32_MUL -> ConstantInstruction.I32Mul
        I64_ADD -> ConstantInstruction.I64Add
        I64_SUB -> ConstantInstruction.I64Sub
        I64_MUL -> ConstantInstruction.I64Mul
        REF_I31 -> ConstantInstruction.RefI31
        ANY_CONVERT_EXTERN -> ConstantInstruction.AnyConvertExtern
        EXTERN_CONVERT_ANY -> ConstantInstruction.ExternConvertAny
        else -> error("unknown constant op $op")
    }

    fun op(
        instruction: ConstantInstruction,
    ): Int = when (instruction) {
        ConstantInstruction.I32Add -> I32_ADD
        ConstantInstruction.I32Sub -> I32_SUB
        ConstantInstruction.I32Mul -> I32_MUL
        ConstantInstruction.I64Add -> I64_ADD
        ConstantInstruction.I64Sub -> I64_SUB
        ConstantInstruction.I64Mul -> I64_MUL
        ConstantInstruction.RefI31 -> REF_I31
        ConstantInstruction.AnyConvertExtern -> ANY_CONVERT_EXTERN
        ConstantInstruction.ExternConvertAny -> EXTERN_CONVERT_ANY
        else -> -1
    }
}

internal class IntColumn {

    private var values = IntArray(16)

    var size = 0
        private set

    operator fun get(
        index: Int,
    ): Int = values[index]

    fun unpack(
        part: String,
    ) {
        var position = 0
        while (position < part.length) {
            var value = 0L
            var shift = 0
            var group: Int
            do {
                group = part[position++].code
                value = value or ((group and 0x7F).toLong() shl shift)
                shift += 7
            } while (group and 0x80 != 0)
            if (shift < Long.SIZE_BITS && group and 0x40 != 0) value = value or (-1L shl shift)
            add(value.toInt())
        }
    }

    fun toArray(): IntArray = values.copyOf(size)

    private fun add(
        value: Int,
    ) {
        if (size == values.size) values = values.copyOf(values.size * 2)
        values[size++] = value
    }
}
