package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Index.DataIndex
import io.github.charlietap.chasm.ast.module.Index.ElementIndex
import io.github.charlietap.chasm.ast.module.Index.FieldIndex
import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import io.github.charlietap.chasm.ast.module.Index.GlobalIndex
import io.github.charlietap.chasm.ast.module.Index.LabelIndex
import io.github.charlietap.chasm.ast.module.Index.LocalIndex
import io.github.charlietap.chasm.ast.module.Index.MemoryIndex
import io.github.charlietap.chasm.ast.module.Index.TableIndex
import io.github.charlietap.chasm.ast.module.Index.TagIndex
import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import io.github.charlietap.chasm.ir.module.Index as IRIndex

internal inline fun LocalIndexFactory(index: LocalIndex) = IRIndex.LocalIndex(
    idx = index.idx.toInt(),
)

internal inline fun GlobalIndexFactory(index: GlobalIndex) = IRIndex.GlobalIndex(
    idx = index.idx.toInt(),
)

internal inline fun DataIndexFactory(index: DataIndex) = IRIndex.DataIndex(
    idx = index.idx.toInt(),
)

internal inline fun ElementIndexFactory(index: ElementIndex) = IRIndex.ElementIndex(
    idx = index.idx.toInt(),
)

internal inline fun FunctionIndexFactory(index: FunctionIndex) = IRIndex.FunctionIndex(
    idx = index.idx.toInt(),
)

internal inline fun LabelIndexFactory(index: LabelIndex) = IRIndex.LabelIndex(
    idx = index.idx.toInt(),
)

internal inline fun MemoryIndexFactory(index: MemoryIndex) = IRIndex.MemoryIndex(
    idx = index.idx.toInt(),
)

internal inline fun TableIndexFactory(index: TableIndex) = IRIndex.TableIndex(
    idx = index.idx.toInt(),
)

internal inline fun TagIndexFactory(index: TagIndex) = IRIndex.TagIndex(
    idx = index.idx.toInt(),
)

internal inline fun TypeIndexFactory(index: TypeIndex) = IRIndex.TypeIndex(
    idx = index.idx.toInt(),
)

internal inline fun FieldIndexFactory(index: FieldIndex) = IRIndex.FieldIndex(
    idx = index.idx.toInt(),
)
