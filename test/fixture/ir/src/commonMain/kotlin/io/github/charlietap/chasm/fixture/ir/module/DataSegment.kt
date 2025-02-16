package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.DataSegment
import io.github.charlietap.chasm.ir.module.Index

fun dataSegment(
    idx: Index.DataIndex = dataIndex(),
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
