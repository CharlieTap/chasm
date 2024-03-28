package io.github.charlietap.chasm.fixture.instruction

import io.github.charlietap.chasm.ast.module.Index

fun localIndex(
    index: UInt = 0u,
) = Index.LocalIndex(index)

fun globalIndex(
    index: UInt = 0u,
) = Index.GlobalIndex(index)

fun dataIndex(
    index: UInt = 0u,
) = Index.DataIndex(index)

fun elementIndex(
    index: UInt = 0u,
) = Index.ElementIndex(index)

fun functionIndex(
    index: UInt = 0u,
) = Index.FunctionIndex(index)

fun labelIndex(
    index: UInt = 0u,
) = Index.LabelIndex(index)

fun memoryIndex(
    index: UInt = 0u,
) = Index.MemoryIndex(index)

fun tableIndex(
    index: UInt = 0u,
) = Index.TableIndex(index)

fun typeIndex(
    index: UInt = 0u,
) = Index.TypeIndex(index)
