package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ir.instruction.MemArg as IRMemArg
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction as IRMemoryInstruction
import io.github.charlietap.chasm.ir.module.Index.DataIndex as IRDataIndex
import io.github.charlietap.chasm.ir.module.Index.MemoryIndex as IRMemoryIndex

internal fun MemoryInstructionFactory(
    instruction: MemoryInstruction,
): IRMemoryInstruction = MemoryInstructionFactory(
    instruction = instruction,
    memoryIndexFactory = ::MemoryIndexFactory,
    dataIndexFactory = ::DataIndexFactory,
    memArgFactory = ::MemArgFactory,
)

internal inline fun MemoryInstructionFactory(
    instruction: MemoryInstruction,
    memoryIndexFactory: IRFactory<Index.MemoryIndex, IRMemoryIndex>,
    dataIndexFactory: IRFactory<Index.DataIndex, IRDataIndex>,
    memArgFactory: IRFactory<MemArg, IRMemArg>,
): IRMemoryInstruction {
    return when (instruction) {
        is MemoryInstruction.Load.I32Load -> IRMemoryInstruction.Load.I32Load(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I64Load -> IRMemoryInstruction.Load.I64Load(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.F32Load -> IRMemoryInstruction.Load.F32Load(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.F64Load -> IRMemoryInstruction.Load.F64Load(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I32Load8S -> IRMemoryInstruction.Load.I32Load8S(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I32Load8U -> IRMemoryInstruction.Load.I32Load8U(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I32Load16S -> IRMemoryInstruction.Load.I32Load16S(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I32Load16U -> IRMemoryInstruction.Load.I32Load16U(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I64Load8S -> IRMemoryInstruction.Load.I64Load8S(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I64Load8U -> IRMemoryInstruction.Load.I64Load8U(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I64Load16S -> IRMemoryInstruction.Load.I64Load16S(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I64Load16U -> IRMemoryInstruction.Load.I64Load16U(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I64Load32S -> IRMemoryInstruction.Load.I64Load32S(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Load.I64Load32U -> IRMemoryInstruction.Load.I64Load32U(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Store.I32Store -> IRMemoryInstruction.Store.I32Store(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Store.I64Store -> IRMemoryInstruction.Store.I64Store(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Store.F32Store -> IRMemoryInstruction.Store.F32Store(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Store.F64Store -> IRMemoryInstruction.Store.F64Store(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Store.I32Store8 -> IRMemoryInstruction.Store.I32Store8(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Store.I32Store16 -> IRMemoryInstruction.Store.I32Store16(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Store.I64Store8 -> IRMemoryInstruction.Store.I64Store8(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Store.I64Store16 -> IRMemoryInstruction.Store.I64Store16(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.Store.I64Store32 -> IRMemoryInstruction.Store.I64Store32(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )

        is MemoryInstruction.MemorySize -> IRMemoryInstruction.MemorySize(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
        )

        is MemoryInstruction.MemoryGrow -> IRMemoryInstruction.MemoryGrow(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
        )

        is MemoryInstruction.MemoryInit -> IRMemoryInstruction.MemoryInit(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            dataIndex = dataIndexFactory(instruction.dataIndex),
        )

        is MemoryInstruction.DataDrop -> IRMemoryInstruction.DataDrop(
            dataIdx = dataIndexFactory(instruction.dataIdx),
        )

        is MemoryInstruction.MemoryCopy -> IRMemoryInstruction.MemoryCopy(
            srcIndex = memoryIndexFactory(instruction.srcIndex),
            dstIndex = memoryIndexFactory(instruction.dstIndex),
        )

        is MemoryInstruction.MemoryFill -> IRMemoryInstruction.MemoryFill(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
        )
    }
}
