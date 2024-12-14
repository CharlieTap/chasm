package io.github.charlietap.chasm.fixture.ast.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.instruction.expression

fun dataSegment(
    idx: Index.DataIndex = Index.DataIndex(0u),
    initData: UByteArray = ubyteArrayOf(),
    mode: DataSegment.Mode = dataSegmentMode(),
) = DataSegment(
    idx = idx,
    initData = initData,
    mode = mode,
)

fun dataSegmentMode(): DataSegment.Mode = passiveDataSegmentMode()

fun activeDataSegmentMode(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    offset: Expression = expression(),
): DataSegment.Mode.Active = DataSegment.Mode.Active(
    memoryIndex = memoryIndex,
    offset = offset,
)

fun passiveDataSegmentMode() = DataSegment.Mode.Passive
