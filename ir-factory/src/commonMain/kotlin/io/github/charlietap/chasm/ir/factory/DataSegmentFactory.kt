package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Index.DataIndex
import io.github.charlietap.chasm.ir.module.DataSegment as IRDataSegment
import io.github.charlietap.chasm.ir.module.Index.DataIndex as IRDataIndex

internal fun DataSegmentFactory(
    dataSegment: DataSegment,
): IRDataSegment = DataSegmentFactory(
    dataSegment = dataSegment,
    dataIndexFactory = ::DataIndexFactory,
    modeFactory = ::DataSegmentModeFactory,
)

internal inline fun DataSegmentFactory(
    dataSegment: DataSegment,
    dataIndexFactory: IRFactory<DataIndex, IRDataIndex>,
    modeFactory: IRFactory<DataSegment.Mode, IRDataSegment.Mode>,
): IRDataSegment {
    return IRDataSegment(
        idx = dataIndexFactory(dataSegment.idx),
        initData = dataSegment.initData,
        mode = modeFactory(dataSegment.mode),
    )
}

internal fun DataSegmentModeFactory(
    mode: DataSegment.Mode,
): IRDataSegment.Mode {
    return when (mode) {
        is DataSegment.Mode.Active -> IRDataSegment.Mode.Active(
            memoryIndex = MemoryIndexFactory(mode.memoryIndex),
            offset = ExpressionFactory(mode.offset),
        )
        DataSegment.Mode.Passive -> IRDataSegment.Mode.Passive
    }
}
