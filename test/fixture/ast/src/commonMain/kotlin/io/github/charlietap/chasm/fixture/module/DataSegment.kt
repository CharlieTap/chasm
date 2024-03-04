package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.DataSegment

fun dataSegment(
    idx: Index.DataIndex = Index.DataIndex(0u),
    initData: UByteArray = ubyteArrayOf(),
    mode: DataSegment.Mode = DataSegment.Mode.Passive,
) = DataSegment(
    idx = idx,
    initData = initData,
    mode = mode,
)
