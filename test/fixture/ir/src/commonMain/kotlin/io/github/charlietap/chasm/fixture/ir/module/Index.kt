package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.ir.module.Index

fun localIndex(
    index: Int = 0,
) = Index.LocalIndex(index)

fun globalIndex(
    index: Int = 0,
) = Index.GlobalIndex(index)

fun dataIndex(
    index: Int = 0,
) = Index.DataIndex(index)

fun elementIndex(
    index: Int = 0,
) = Index.ElementIndex(index)

fun functionIndex(
    index: Int = 0,
) = Index.FunctionIndex(index)

fun labelIndex(
    index: Int = 0,
) = Index.LabelIndex(index)

fun memoryIndex(
    index: Int = 0,
) = Index.MemoryIndex(index)

fun tableIndex(
    index: Int = 0,
) = Index.TableIndex(index)

fun typeIndex(
    index: Int = 0,
) = Index.TypeIndex(index)

fun fieldIndex(
    index: Int = 0,
) = Index.FieldIndex(index)

fun tagIndex(
    index: Int = 0,
) = Index.TagIndex(index)
