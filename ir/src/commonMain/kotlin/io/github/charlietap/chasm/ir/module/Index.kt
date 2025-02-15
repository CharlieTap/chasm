package io.github.charlietap.chasm.ir.module

import kotlin.jvm.JvmInline

sealed interface Index {

    val idx: Int

    @JvmInline
    value class LocalIndex(override val idx: Int) : Index

    @JvmInline
    value class GlobalIndex(override val idx: Int) : Index

    @JvmInline
    value class DataIndex(override val idx: Int) : Index

    @JvmInline
    value class ElementIndex(override val idx: Int) : Index

    @JvmInline
    value class FunctionIndex(override val idx: Int) : Index

    @JvmInline
    value class LabelIndex(override val idx: Int) : Index

    @JvmInline
    value class MemoryIndex(override val idx: Int) : Index

    @JvmInline
    value class TableIndex(override val idx: Int) : Index

    @JvmInline
    value class TagIndex(override val idx: Int) : Index

    @JvmInline
    value class TypeIndex(override val idx: Int) : Index

    @JvmInline
    value class FieldIndex(override val idx: Int) : Index
}
