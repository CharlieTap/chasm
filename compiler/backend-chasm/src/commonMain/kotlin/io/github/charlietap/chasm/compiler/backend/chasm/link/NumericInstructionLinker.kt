package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32LoadIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32LoadSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F64LoadIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F64LoadSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load16SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load16SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load16UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load16USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load8SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load8SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load8UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Load8USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32LoadIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32LoadSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load8SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load8SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load8UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load8USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64LoadIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64LoadSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32AbsIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32AbsSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32AddIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32AddIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32AddSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32AddSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32CeilIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32CeilSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConstSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI32SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI32SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI32UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI32USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI64SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI64SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI64UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32ConvertI64USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32CopysignIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32CopysignIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32CopysignSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32CopysignSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32DemoteF64IDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32DemoteF64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32DivIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32DivIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32DivSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32DivSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32EqIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32EqIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32EqSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32EqSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32FloorIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32FloorSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32GeIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32GeIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32GeSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32GeSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32GtIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32GtIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32GtSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32GtSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32LeIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32LeIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32LeSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32LeSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32LtIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32LtIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32LtSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32LtSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MaxIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MaxIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MaxSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MaxSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MinIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MinIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MinSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MinSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MulIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MulIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MulSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32MulSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NeIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NeIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NeSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NeSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NearestIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NearestSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NegIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NegSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32SqrtIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32SqrtSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32SubIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32SubIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32SubSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32SubSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32TruncIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32TruncSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64AbsIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64AbsSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64AddIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64AddIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64AddSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64AddSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64CeilIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64CeilSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConstSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI32SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI32SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI32UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI32USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI64SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI64SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI64UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64ConvertI64USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64CopysignIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64CopysignIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64CopysignSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64CopysignSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64DivIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64DivIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64DivSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64DivSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64EqIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64EqIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64EqSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64EqSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64FloorIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64FloorSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64GeIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64GeIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64GeSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64GeSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64GtIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64GtIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64GtSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64GtSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64LeIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64LeIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64LeSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64LeSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64LtIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64LtIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64LtSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64LtSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MaxIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MaxIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MaxSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MaxSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MinIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MinIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MinSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MinSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MulIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MulIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MulSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64MulSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NeIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NeIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NeSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NeSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NearestIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NearestSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NegIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64NegSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64PromoteF32IDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64PromoteF32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64SqrtIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64SqrtSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64SubIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64SubIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64SubSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64SubSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64TruncIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F64TruncSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32AddIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32AddSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32AddSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32AndIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32AndIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32AndSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32AndSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32BitFieldExtractSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ClzIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ClzSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ConstSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32CtzIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32CtzSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32DivSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32DivSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32DivSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32DivSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32DivUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32DivUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32DivUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32DivUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32EqIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32EqIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32EqSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32EqSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32EqzIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32EqzSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32Extend16SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32Extend16SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32Extend8SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32Extend8SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GeSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GeSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GeSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GeSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GeUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GeUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GeUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GeUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GtSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GtSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GtSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GtSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GtUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GtUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GtUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32GtUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LeSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LeSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LeSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LeSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LeUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LeUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LeUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LeUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LtSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LtSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LtSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LtSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LtUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LtUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LtUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32LtUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32MulIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32MulIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32MulSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32MulSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32NeIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32NeIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32NeSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32NeSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32OrIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32OrIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32OrSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32OrSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32PopcntIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32PopcntSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RemSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RemSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RemSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RemSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RemUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RemUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RemUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RemUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RotlIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RotlIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RotlSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RotlSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RotrIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RotrIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RotrSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32RotrSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShlIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShlIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShlSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShlSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShrSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShrSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShrSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShrSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShrUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShrUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShrUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32ShrUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32SubIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32SubIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32SubSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32SubSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF32SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF32SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF32UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF32USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF64SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF64SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF64UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncF64USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF32SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF32SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF32UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF32USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF64SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF64SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF64UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32TruncSatF64USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32WrapI64IDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32WrapI64SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32XorIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32XorIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32XorSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I32XorSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128IiiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128IiisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128IisiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128IissDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128IsiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128IsisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128IssiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128IsssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128SiiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128SiisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128SisiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128SissDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128SsiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128SsisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128SssiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Add128SsssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64AddIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64AddIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64AddSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64AddSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64AndIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64AndIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64AndSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64AndSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ClzIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ClzSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ConstSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64CtzIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64CtzSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64DivSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64DivSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64DivSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64DivSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64DivUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64DivUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64DivUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64DivUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64EqIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64EqIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64EqSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64EqSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64EqzIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64EqzSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Extend16SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Extend16SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Extend32SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Extend32SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Extend8SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Extend8SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ExtendI32SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ExtendI32SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ExtendI32UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ExtendI32USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GeSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GeSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GeSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GeSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GeUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GeUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GeUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GeUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GtSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GtSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GtSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GtSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GtUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GtUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GtUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64GtUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LeSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LeSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LeSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LeSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LeUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LeUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LeUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LeUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LtSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LtSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LtSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LtSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LtUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LtUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LtUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64LtUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulWideSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulWideSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulWideSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulWideSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulWideUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulWideUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulWideUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64MulWideUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64NeIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64NeIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64NeSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64NeSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64OrIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64OrIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64OrSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64OrSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64PopcntIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64PopcntSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RemSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RemSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RemSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RemSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RemUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RemUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RemUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RemUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RotlIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RotlIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RotlSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RotlSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RotrIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RotrIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RotrSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64RotrSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShlIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShlIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShlSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShlSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShrSIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShrSIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShrSSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShrSSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShrUIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShrUIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShrUSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64ShrUSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128IiiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128IiisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128IisiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128IissDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128IsiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128IsisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128IssiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128IsssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128SiiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128SiisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128SisiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128SissDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128SsiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128SsisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128SssiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64Sub128SsssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64SubIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64SubIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64SubSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64SubSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF32SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF32SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF32UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF32USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF64SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF64SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF64UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncF64USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF32SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF32SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF32UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF32USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF64SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF64SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF64UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64TruncSatF64USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64XorIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64XorIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64XorSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.I64XorSsDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

internal fun linkInstructions0(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    0 -> {
        val i = instruction as NumericInstruction.I32ConstS
        I32ConstSDispatcher(i.value, i.destinationSlot)
    }
    1 -> {
        val i = instruction as NumericInstruction.I32BitFieldExtractS
        I32BitFieldExtractSDispatcher(i.operandSlot, i.shift, i.mask, i.destinationSlot)
    }
    2 -> {
        val i = instruction as NumericInstruction.I64ConstS
        I64ConstSDispatcher(i.value, i.destinationSlot)
    }
    3 -> {
        val i = instruction as NumericInstruction.F32ConstS
        F32ConstSDispatcher(i.bits, i.destinationSlot)
    }
    4 -> {
        val i = instruction as NumericInstruction.F64ConstS
        F64ConstSDispatcher(i.bits, i.destinationSlot)
    }
    5 -> {
        val i = instruction as NumericInstruction.I32AddIs
        I32AddIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    6 -> {
        val i = instruction as NumericInstruction.I32AddSi
        I32AddSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    7 -> {
        val i = instruction as NumericInstruction.I32AddSs
        I32AddSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    8 -> {
        val i = instruction as NumericInstruction.I32SubIi
        I32SubIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    9 -> {
        val i = instruction as NumericInstruction.I32SubIs
        I32SubIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    10 -> {
        val i = instruction as NumericInstruction.I32SubSi
        I32SubSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    11 -> {
        val i = instruction as NumericInstruction.I32SubSs
        I32SubSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    12 -> {
        val i = instruction as NumericInstruction.I32MulIi
        I32MulIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    13 -> {
        val i = instruction as NumericInstruction.I32MulIs
        I32MulIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    14 -> {
        val i = instruction as NumericInstruction.I32MulSi
        I32MulSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    15 -> {
        val i = instruction as NumericInstruction.I32MulSs
        I32MulSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    16 -> {
        val i = instruction as NumericInstruction.I32DivSIi
        I32DivSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    17 -> {
        val i = instruction as NumericInstruction.I32DivSIs
        I32DivSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    18 -> {
        val i = instruction as NumericInstruction.I32DivSSi
        I32DivSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    19 -> {
        val i = instruction as NumericInstruction.I32DivSSs
        I32DivSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    20 -> {
        val i = instruction as NumericInstruction.I32DivUIi
        I32DivUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    21 -> {
        val i = instruction as NumericInstruction.I32DivUIs
        I32DivUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    22 -> {
        val i = instruction as NumericInstruction.I32DivUSi
        I32DivUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    23 -> {
        val i = instruction as NumericInstruction.I32DivUSs
        I32DivUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    24 -> {
        val i = instruction as NumericInstruction.I32RemSIi
        I32RemSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    25 -> {
        val i = instruction as NumericInstruction.I32RemSIs
        I32RemSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    26 -> {
        val i = instruction as NumericInstruction.I32RemSSi
        I32RemSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    27 -> {
        val i = instruction as NumericInstruction.I32RemSSs
        I32RemSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    28 -> {
        val i = instruction as NumericInstruction.I32RemUIi
        I32RemUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    29 -> {
        val i = instruction as NumericInstruction.I32RemUIs
        I32RemUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    30 -> {
        val i = instruction as NumericInstruction.I32RemUSi
        I32RemUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    31 -> {
        val i = instruction as NumericInstruction.I32RemUSs
        I32RemUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    32 -> {
        val i = instruction as NumericInstruction.I32AndIi
        I32AndIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    33 -> {
        val i = instruction as NumericInstruction.I32AndIs
        I32AndIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    34 -> {
        val i = instruction as NumericInstruction.I32AndSi
        I32AndSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    35 -> {
        val i = instruction as NumericInstruction.I32AndSs
        I32AndSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    36 -> {
        val i = instruction as NumericInstruction.I32OrIi
        I32OrIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    37 -> {
        val i = instruction as NumericInstruction.I32OrIs
        I32OrIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    38 -> {
        val i = instruction as NumericInstruction.I32OrSi
        I32OrSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    39 -> {
        val i = instruction as NumericInstruction.I32OrSs
        I32OrSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    40 -> {
        val i = instruction as NumericInstruction.I32XorIi
        I32XorIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    41 -> {
        val i = instruction as NumericInstruction.I32XorIs
        I32XorIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    42 -> {
        val i = instruction as NumericInstruction.I32XorSi
        I32XorSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    43 -> {
        val i = instruction as NumericInstruction.I32XorSs
        I32XorSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    44 -> {
        val i = instruction as NumericInstruction.I32ShlIi
        I32ShlIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    45 -> {
        val i = instruction as NumericInstruction.I32ShlIs
        I32ShlIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    46 -> {
        val i = instruction as NumericInstruction.I32ShlSi
        I32ShlSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    47 -> {
        val i = instruction as NumericInstruction.I32ShlSs
        I32ShlSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions1(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    48 -> {
        val i = instruction as NumericInstruction.I32ShrSIi
        I32ShrSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    49 -> {
        val i = instruction as NumericInstruction.I32ShrSIs
        I32ShrSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    50 -> {
        val i = instruction as NumericInstruction.I32ShrSSi
        I32ShrSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    51 -> {
        val i = instruction as NumericInstruction.I32ShrSSs
        I32ShrSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    52 -> {
        val i = instruction as NumericInstruction.I32ShrUIi
        I32ShrUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    53 -> {
        val i = instruction as NumericInstruction.I32ShrUIs
        I32ShrUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    54 -> {
        val i = instruction as NumericInstruction.I32ShrUSi
        I32ShrUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    55 -> {
        val i = instruction as NumericInstruction.I32ShrUSs
        I32ShrUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    56 -> {
        val i = instruction as NumericInstruction.I32RotlIi
        I32RotlIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    57 -> {
        val i = instruction as NumericInstruction.I32RotlIs
        I32RotlIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    58 -> {
        val i = instruction as NumericInstruction.I32RotlSi
        I32RotlSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    59 -> {
        val i = instruction as NumericInstruction.I32RotlSs
        I32RotlSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    60 -> {
        val i = instruction as NumericInstruction.I32RotrIi
        I32RotrIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    61 -> {
        val i = instruction as NumericInstruction.I32RotrIs
        I32RotrIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    62 -> {
        val i = instruction as NumericInstruction.I32RotrSi
        I32RotrSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    63 -> {
        val i = instruction as NumericInstruction.I32RotrSs
        I32RotrSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    64 -> {
        val i = instruction as NumericInstruction.I64AddIi
        I64AddIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    65 -> {
        val i = instruction as NumericInstruction.I64AddIs
        I64AddIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    66 -> {
        val i = instruction as NumericInstruction.I64AddSi
        I64AddSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    67 -> {
        val i = instruction as NumericInstruction.I64AddSs
        I64AddSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    68 -> {
        val i = instruction as NumericInstruction.I64SubIi
        I64SubIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    69 -> {
        val i = instruction as NumericInstruction.I64SubIs
        I64SubIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    70 -> {
        val i = instruction as NumericInstruction.I64SubSi
        I64SubSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    71 -> {
        val i = instruction as NumericInstruction.I64SubSs
        I64SubSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    72 -> {
        val i = instruction as NumericInstruction.I64MulIi
        I64MulIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    73 -> {
        val i = instruction as NumericInstruction.I64MulIs
        I64MulIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    74 -> {
        val i = instruction as NumericInstruction.I64MulSi
        I64MulSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    75 -> {
        val i = instruction as NumericInstruction.I64MulSs
        I64MulSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    76 -> {
        val i = instruction as NumericInstruction.I64Add128Iiii
        I64Add128IiiiDispatcher(i.leftLow, i.leftHigh, i.rightLow, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    77 -> {
        val i = instruction as NumericInstruction.I64Add128Iiis
        I64Add128IiisDispatcher(i.leftLow, i.leftHigh, i.rightLow, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    78 -> {
        val i = instruction as NumericInstruction.I64Add128Iisi
        I64Add128IisiDispatcher(i.leftLow, i.leftHigh, i.rightLowSlot, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    79 -> {
        val i = instruction as NumericInstruction.I64Add128Iiss
        I64Add128IissDispatcher(i.leftLow, i.leftHigh, i.rightLowSlot, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    80 -> {
        val i = instruction as NumericInstruction.I64Add128Isii
        I64Add128IsiiDispatcher(i.leftLow, i.leftHighSlot, i.rightLow, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    81 -> {
        val i = instruction as NumericInstruction.I64Add128Isis
        I64Add128IsisDispatcher(i.leftLow, i.leftHighSlot, i.rightLow, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    82 -> {
        val i = instruction as NumericInstruction.I64Add128Issi
        I64Add128IssiDispatcher(i.leftLow, i.leftHighSlot, i.rightLowSlot, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    83 -> {
        val i = instruction as NumericInstruction.I64Add128Isss
        I64Add128IsssDispatcher(i.leftLow, i.leftHighSlot, i.rightLowSlot, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    84 -> {
        val i = instruction as NumericInstruction.I64Add128Siii
        I64Add128SiiiDispatcher(i.leftLowSlot, i.leftHigh, i.rightLow, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    85 -> {
        val i = instruction as NumericInstruction.I64Add128Siis
        I64Add128SiisDispatcher(i.leftLowSlot, i.leftHigh, i.rightLow, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    86 -> {
        val i = instruction as NumericInstruction.I64Add128Sisi
        I64Add128SisiDispatcher(i.leftLowSlot, i.leftHigh, i.rightLowSlot, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    87 -> {
        val i = instruction as NumericInstruction.I64Add128Siss
        I64Add128SissDispatcher(i.leftLowSlot, i.leftHigh, i.rightLowSlot, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    88 -> {
        val i = instruction as NumericInstruction.I64Add128Ssii
        I64Add128SsiiDispatcher(i.leftLowSlot, i.leftHighSlot, i.rightLow, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    89 -> {
        val i = instruction as NumericInstruction.I64Add128Ssis
        I64Add128SsisDispatcher(i.leftLowSlot, i.leftHighSlot, i.rightLow, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    90 -> {
        val i = instruction as NumericInstruction.I64Add128Sssi
        I64Add128SssiDispatcher(i.leftLowSlot, i.leftHighSlot, i.rightLowSlot, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    91 -> {
        val i = instruction as NumericInstruction.I64Add128Ssss
        I64Add128SsssDispatcher(i.leftLowSlot, i.leftHighSlot, i.rightLowSlot, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    92 -> {
        val i = instruction as NumericInstruction.I64Sub128Iiii
        I64Sub128IiiiDispatcher(i.leftLow, i.leftHigh, i.rightLow, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    93 -> {
        val i = instruction as NumericInstruction.I64Sub128Iiis
        I64Sub128IiisDispatcher(i.leftLow, i.leftHigh, i.rightLow, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    94 -> {
        val i = instruction as NumericInstruction.I64Sub128Iisi
        I64Sub128IisiDispatcher(i.leftLow, i.leftHigh, i.rightLowSlot, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    95 -> {
        val i = instruction as NumericInstruction.I64Sub128Iiss
        I64Sub128IissDispatcher(i.leftLow, i.leftHigh, i.rightLowSlot, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions2(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    96 -> {
        val i = instruction as NumericInstruction.I64Sub128Isii
        I64Sub128IsiiDispatcher(i.leftLow, i.leftHighSlot, i.rightLow, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    97 -> {
        val i = instruction as NumericInstruction.I64Sub128Isis
        I64Sub128IsisDispatcher(i.leftLow, i.leftHighSlot, i.rightLow, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    98 -> {
        val i = instruction as NumericInstruction.I64Sub128Issi
        I64Sub128IssiDispatcher(i.leftLow, i.leftHighSlot, i.rightLowSlot, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    99 -> {
        val i = instruction as NumericInstruction.I64Sub128Isss
        I64Sub128IsssDispatcher(i.leftLow, i.leftHighSlot, i.rightLowSlot, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    100 -> {
        val i = instruction as NumericInstruction.I64Sub128Siii
        I64Sub128SiiiDispatcher(i.leftLowSlot, i.leftHigh, i.rightLow, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    101 -> {
        val i = instruction as NumericInstruction.I64Sub128Siis
        I64Sub128SiisDispatcher(i.leftLowSlot, i.leftHigh, i.rightLow, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    102 -> {
        val i = instruction as NumericInstruction.I64Sub128Sisi
        I64Sub128SisiDispatcher(i.leftLowSlot, i.leftHigh, i.rightLowSlot, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    103 -> {
        val i = instruction as NumericInstruction.I64Sub128Siss
        I64Sub128SissDispatcher(i.leftLowSlot, i.leftHigh, i.rightLowSlot, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    104 -> {
        val i = instruction as NumericInstruction.I64Sub128Ssii
        I64Sub128SsiiDispatcher(i.leftLowSlot, i.leftHighSlot, i.rightLow, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    105 -> {
        val i = instruction as NumericInstruction.I64Sub128Ssis
        I64Sub128SsisDispatcher(i.leftLowSlot, i.leftHighSlot, i.rightLow, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    106 -> {
        val i = instruction as NumericInstruction.I64Sub128Sssi
        I64Sub128SssiDispatcher(i.leftLowSlot, i.leftHighSlot, i.rightLowSlot, i.rightHigh, i.destinationLowSlot, i.destinationHighSlot)
    }
    107 -> {
        val i = instruction as NumericInstruction.I64Sub128Ssss
        I64Sub128SsssDispatcher(i.leftLowSlot, i.leftHighSlot, i.rightLowSlot, i.rightHighSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    108 -> {
        val i = instruction as NumericInstruction.I64MulWideSIi
        I64MulWideSIiDispatcher(i.left, i.right, i.destinationLowSlot, i.destinationHighSlot)
    }
    109 -> {
        val i = instruction as NumericInstruction.I64MulWideSIs
        I64MulWideSIsDispatcher(i.left, i.rightSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    110 -> {
        val i = instruction as NumericInstruction.I64MulWideSSi
        I64MulWideSSiDispatcher(i.leftSlot, i.right, i.destinationLowSlot, i.destinationHighSlot)
    }
    111 -> {
        val i = instruction as NumericInstruction.I64MulWideSSs
        I64MulWideSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    112 -> {
        val i = instruction as NumericInstruction.I64MulWideUIi
        I64MulWideUIiDispatcher(i.left, i.right, i.destinationLowSlot, i.destinationHighSlot)
    }
    113 -> {
        val i = instruction as NumericInstruction.I64MulWideUIs
        I64MulWideUIsDispatcher(i.left, i.rightSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    114 -> {
        val i = instruction as NumericInstruction.I64MulWideUSi
        I64MulWideUSiDispatcher(i.leftSlot, i.right, i.destinationLowSlot, i.destinationHighSlot)
    }
    115 -> {
        val i = instruction as NumericInstruction.I64MulWideUSs
        I64MulWideUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationLowSlot, i.destinationHighSlot)
    }
    116 -> {
        val i = instruction as NumericInstruction.I64DivSIi
        I64DivSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    117 -> {
        val i = instruction as NumericInstruction.I64DivSIs
        I64DivSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    118 -> {
        val i = instruction as NumericInstruction.I64DivSSi
        I64DivSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    119 -> {
        val i = instruction as NumericInstruction.I64DivSSs
        I64DivSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    120 -> {
        val i = instruction as NumericInstruction.I64DivUIi
        I64DivUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    121 -> {
        val i = instruction as NumericInstruction.I64DivUIs
        I64DivUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    122 -> {
        val i = instruction as NumericInstruction.I64DivUSi
        I64DivUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    123 -> {
        val i = instruction as NumericInstruction.I64DivUSs
        I64DivUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    124 -> {
        val i = instruction as NumericInstruction.I64RemSIi
        I64RemSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    125 -> {
        val i = instruction as NumericInstruction.I64RemSIs
        I64RemSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    126 -> {
        val i = instruction as NumericInstruction.I64RemSSi
        I64RemSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    127 -> {
        val i = instruction as NumericInstruction.I64RemSSs
        I64RemSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    128 -> {
        val i = instruction as NumericInstruction.I64RemUIi
        I64RemUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    129 -> {
        val i = instruction as NumericInstruction.I64RemUIs
        I64RemUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    130 -> {
        val i = instruction as NumericInstruction.I64RemUSi
        I64RemUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    131 -> {
        val i = instruction as NumericInstruction.I64RemUSs
        I64RemUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    132 -> {
        val i = instruction as NumericInstruction.I64AndIi
        I64AndIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    133 -> {
        val i = instruction as NumericInstruction.I64AndIs
        I64AndIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    134 -> {
        val i = instruction as NumericInstruction.I64AndSi
        I64AndSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    135 -> {
        val i = instruction as NumericInstruction.I64AndSs
        I64AndSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    136 -> {
        val i = instruction as NumericInstruction.I64OrIi
        I64OrIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    137 -> {
        val i = instruction as NumericInstruction.I64OrIs
        I64OrIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    138 -> {
        val i = instruction as NumericInstruction.I64OrSi
        I64OrSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    139 -> {
        val i = instruction as NumericInstruction.I64OrSs
        I64OrSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    140 -> {
        val i = instruction as NumericInstruction.I64XorIi
        I64XorIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    141 -> {
        val i = instruction as NumericInstruction.I64XorIs
        I64XorIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    142 -> {
        val i = instruction as NumericInstruction.I64XorSi
        I64XorSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    143 -> {
        val i = instruction as NumericInstruction.I64XorSs
        I64XorSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions3(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    144 -> {
        val i = instruction as NumericInstruction.I64ShlIi
        I64ShlIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    145 -> {
        val i = instruction as NumericInstruction.I64ShlIs
        I64ShlIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    146 -> {
        val i = instruction as NumericInstruction.I64ShlSi
        I64ShlSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    147 -> {
        val i = instruction as NumericInstruction.I64ShlSs
        I64ShlSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    148 -> {
        val i = instruction as NumericInstruction.I64ShrSIi
        I64ShrSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    149 -> {
        val i = instruction as NumericInstruction.I64ShrSIs
        I64ShrSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    150 -> {
        val i = instruction as NumericInstruction.I64ShrSSi
        I64ShrSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    151 -> {
        val i = instruction as NumericInstruction.I64ShrSSs
        I64ShrSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    152 -> {
        val i = instruction as NumericInstruction.I64ShrUIi
        I64ShrUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    153 -> {
        val i = instruction as NumericInstruction.I64ShrUIs
        I64ShrUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    154 -> {
        val i = instruction as NumericInstruction.I64ShrUSi
        I64ShrUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    155 -> {
        val i = instruction as NumericInstruction.I64ShrUSs
        I64ShrUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    156 -> {
        val i = instruction as NumericInstruction.I64RotlIi
        I64RotlIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    157 -> {
        val i = instruction as NumericInstruction.I64RotlIs
        I64RotlIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    158 -> {
        val i = instruction as NumericInstruction.I64RotlSi
        I64RotlSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    159 -> {
        val i = instruction as NumericInstruction.I64RotlSs
        I64RotlSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    160 -> {
        val i = instruction as NumericInstruction.I64RotrIi
        I64RotrIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    161 -> {
        val i = instruction as NumericInstruction.I64RotrIs
        I64RotrIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    162 -> {
        val i = instruction as NumericInstruction.I64RotrSi
        I64RotrSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    163 -> {
        val i = instruction as NumericInstruction.I64RotrSs
        I64RotrSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    164 -> {
        val i = instruction as NumericInstruction.I32EqzI
        I32EqzIDispatcher(i.operand, i.destinationSlot)
    }
    165 -> {
        val i = instruction as NumericInstruction.I32EqzS
        I32EqzSDispatcher(i.operandSlot, i.destinationSlot)
    }
    166 -> {
        val i = instruction as NumericInstruction.I64EqzI
        I64EqzIDispatcher(i.operand, i.destinationSlot)
    }
    167 -> {
        val i = instruction as NumericInstruction.I64EqzS
        I64EqzSDispatcher(i.operandSlot, i.destinationSlot)
    }
    168 -> {
        val i = instruction as NumericInstruction.I32ClzI
        I32ClzIDispatcher(i.operand, i.destinationSlot)
    }
    169 -> {
        val i = instruction as NumericInstruction.I32ClzS
        I32ClzSDispatcher(i.operandSlot, i.destinationSlot)
    }
    170 -> {
        val i = instruction as NumericInstruction.I32CtzI
        I32CtzIDispatcher(i.operand, i.destinationSlot)
    }
    171 -> {
        val i = instruction as NumericInstruction.I32CtzS
        I32CtzSDispatcher(i.operandSlot, i.destinationSlot)
    }
    172 -> {
        val i = instruction as NumericInstruction.I32PopcntI
        I32PopcntIDispatcher(i.operand, i.destinationSlot)
    }
    173 -> {
        val i = instruction as NumericInstruction.I32PopcntS
        I32PopcntSDispatcher(i.operandSlot, i.destinationSlot)
    }
    174 -> {
        val i = instruction as NumericInstruction.I64ClzI
        I64ClzIDispatcher(i.operand, i.destinationSlot)
    }
    175 -> {
        val i = instruction as NumericInstruction.I64ClzS
        I64ClzSDispatcher(i.operandSlot, i.destinationSlot)
    }
    176 -> {
        val i = instruction as NumericInstruction.I64CtzI
        I64CtzIDispatcher(i.operand, i.destinationSlot)
    }
    177 -> {
        val i = instruction as NumericInstruction.I64CtzS
        I64CtzSDispatcher(i.operandSlot, i.destinationSlot)
    }
    178 -> {
        val i = instruction as NumericInstruction.I64PopcntI
        I64PopcntIDispatcher(i.operand, i.destinationSlot)
    }
    179 -> {
        val i = instruction as NumericInstruction.I64PopcntS
        I64PopcntSDispatcher(i.operandSlot, i.destinationSlot)
    }
    180 -> {
        val i = instruction as NumericInstruction.I32EqIi
        I32EqIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    181 -> {
        val i = instruction as NumericInstruction.I32EqIs
        I32EqIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    182 -> {
        val i = instruction as NumericInstruction.I32EqSi
        I32EqSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    183 -> {
        val i = instruction as NumericInstruction.I32EqSs
        I32EqSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    184 -> {
        val i = instruction as NumericInstruction.I32NeIi
        I32NeIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    185 -> {
        val i = instruction as NumericInstruction.I32NeIs
        I32NeIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    186 -> {
        val i = instruction as NumericInstruction.I32NeSi
        I32NeSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    187 -> {
        val i = instruction as NumericInstruction.I32NeSs
        I32NeSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    188 -> {
        val i = instruction as NumericInstruction.I32LtSIi
        I32LtSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    189 -> {
        val i = instruction as NumericInstruction.I32LtSIs
        I32LtSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    190 -> {
        val i = instruction as NumericInstruction.I32LtSSi
        I32LtSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    191 -> {
        val i = instruction as NumericInstruction.I32LtSSs
        I32LtSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions4(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    192 -> {
        val i = instruction as NumericInstruction.I32LtUIi
        I32LtUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    193 -> {
        val i = instruction as NumericInstruction.I32LtUIs
        I32LtUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    194 -> {
        val i = instruction as NumericInstruction.I32LtUSi
        I32LtUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    195 -> {
        val i = instruction as NumericInstruction.I32LtUSs
        I32LtUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    196 -> {
        val i = instruction as NumericInstruction.I32GtSIi
        I32GtSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    197 -> {
        val i = instruction as NumericInstruction.I32GtSIs
        I32GtSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    198 -> {
        val i = instruction as NumericInstruction.I32GtSSi
        I32GtSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    199 -> {
        val i = instruction as NumericInstruction.I32GtSSs
        I32GtSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    200 -> {
        val i = instruction as NumericInstruction.I32GtUIi
        I32GtUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    201 -> {
        val i = instruction as NumericInstruction.I32GtUIs
        I32GtUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    202 -> {
        val i = instruction as NumericInstruction.I32GtUSi
        I32GtUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    203 -> {
        val i = instruction as NumericInstruction.I32GtUSs
        I32GtUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    204 -> {
        val i = instruction as NumericInstruction.I32LeSIi
        I32LeSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    205 -> {
        val i = instruction as NumericInstruction.I32LeSIs
        I32LeSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    206 -> {
        val i = instruction as NumericInstruction.I32LeSSi
        I32LeSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    207 -> {
        val i = instruction as NumericInstruction.I32LeSSs
        I32LeSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    208 -> {
        val i = instruction as NumericInstruction.I32LeUIi
        I32LeUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    209 -> {
        val i = instruction as NumericInstruction.I32LeUIs
        I32LeUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    210 -> {
        val i = instruction as NumericInstruction.I32LeUSi
        I32LeUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    211 -> {
        val i = instruction as NumericInstruction.I32LeUSs
        I32LeUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    212 -> {
        val i = instruction as NumericInstruction.I32GeSIi
        I32GeSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    213 -> {
        val i = instruction as NumericInstruction.I32GeSIs
        I32GeSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    214 -> {
        val i = instruction as NumericInstruction.I32GeSSi
        I32GeSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    215 -> {
        val i = instruction as NumericInstruction.I32GeSSs
        I32GeSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    216 -> {
        val i = instruction as NumericInstruction.I32GeUIi
        I32GeUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    217 -> {
        val i = instruction as NumericInstruction.I32GeUIs
        I32GeUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    218 -> {
        val i = instruction as NumericInstruction.I32GeUSi
        I32GeUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    219 -> {
        val i = instruction as NumericInstruction.I32GeUSs
        I32GeUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    220 -> {
        val i = instruction as NumericInstruction.I64EqIi
        I64EqIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    221 -> {
        val i = instruction as NumericInstruction.I64EqIs
        I64EqIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    222 -> {
        val i = instruction as NumericInstruction.I64EqSi
        I64EqSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    223 -> {
        val i = instruction as NumericInstruction.I64EqSs
        I64EqSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    224 -> {
        val i = instruction as NumericInstruction.I64NeIi
        I64NeIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    225 -> {
        val i = instruction as NumericInstruction.I64NeIs
        I64NeIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    226 -> {
        val i = instruction as NumericInstruction.I64NeSi
        I64NeSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    227 -> {
        val i = instruction as NumericInstruction.I64NeSs
        I64NeSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    228 -> {
        val i = instruction as NumericInstruction.I64LtSIi
        I64LtSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    229 -> {
        val i = instruction as NumericInstruction.I64LtSIs
        I64LtSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    230 -> {
        val i = instruction as NumericInstruction.I64LtSSi
        I64LtSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    231 -> {
        val i = instruction as NumericInstruction.I64LtSSs
        I64LtSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    232 -> {
        val i = instruction as NumericInstruction.I64LtUIi
        I64LtUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    233 -> {
        val i = instruction as NumericInstruction.I64LtUIs
        I64LtUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    234 -> {
        val i = instruction as NumericInstruction.I64LtUSi
        I64LtUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    235 -> {
        val i = instruction as NumericInstruction.I64LtUSs
        I64LtUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    236 -> {
        val i = instruction as NumericInstruction.I64GtSIi
        I64GtSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    237 -> {
        val i = instruction as NumericInstruction.I64GtSIs
        I64GtSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    238 -> {
        val i = instruction as NumericInstruction.I64GtSSi
        I64GtSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    239 -> {
        val i = instruction as NumericInstruction.I64GtSSs
        I64GtSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions5(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    240 -> {
        val i = instruction as NumericInstruction.I64GtUIi
        I64GtUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    241 -> {
        val i = instruction as NumericInstruction.I64GtUIs
        I64GtUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    242 -> {
        val i = instruction as NumericInstruction.I64GtUSi
        I64GtUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    243 -> {
        val i = instruction as NumericInstruction.I64GtUSs
        I64GtUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    244 -> {
        val i = instruction as NumericInstruction.I64LeSIi
        I64LeSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    245 -> {
        val i = instruction as NumericInstruction.I64LeSIs
        I64LeSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    246 -> {
        val i = instruction as NumericInstruction.I64LeSSi
        I64LeSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    247 -> {
        val i = instruction as NumericInstruction.I64LeSSs
        I64LeSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    248 -> {
        val i = instruction as NumericInstruction.I64LeUIi
        I64LeUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    249 -> {
        val i = instruction as NumericInstruction.I64LeUIs
        I64LeUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    250 -> {
        val i = instruction as NumericInstruction.I64LeUSi
        I64LeUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    251 -> {
        val i = instruction as NumericInstruction.I64LeUSs
        I64LeUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    252 -> {
        val i = instruction as NumericInstruction.I64GeSIi
        I64GeSIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    253 -> {
        val i = instruction as NumericInstruction.I64GeSIs
        I64GeSIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    254 -> {
        val i = instruction as NumericInstruction.I64GeSSi
        I64GeSSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    255 -> {
        val i = instruction as NumericInstruction.I64GeSSs
        I64GeSSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    256 -> {
        val i = instruction as NumericInstruction.I64GeUIi
        I64GeUIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    257 -> {
        val i = instruction as NumericInstruction.I64GeUIs
        I64GeUIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    258 -> {
        val i = instruction as NumericInstruction.I64GeUSi
        I64GeUSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    259 -> {
        val i = instruction as NumericInstruction.I64GeUSs
        I64GeUSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    260 -> {
        val i = instruction as NumericInstruction.I32Extend16SI
        I32Extend16SIDispatcher(i.operand, i.destinationSlot)
    }
    261 -> {
        val i = instruction as NumericInstruction.I32Extend16SS
        I32Extend16SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    262 -> {
        val i = instruction as NumericInstruction.I32Extend8SI
        I32Extend8SIDispatcher(i.operand, i.destinationSlot)
    }
    263 -> {
        val i = instruction as NumericInstruction.I32Extend8SS
        I32Extend8SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    264 -> {
        val i = instruction as NumericInstruction.I32TruncF32SI
        I32TruncF32SIDispatcher(i.operand, i.destinationSlot)
    }
    265 -> {
        val i = instruction as NumericInstruction.I32TruncF32SS
        I32TruncF32SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    266 -> {
        val i = instruction as NumericInstruction.I32TruncF32UI
        I32TruncF32UIDispatcher(i.operand, i.destinationSlot)
    }
    267 -> {
        val i = instruction as NumericInstruction.I32TruncF32US
        I32TruncF32USDispatcher(i.operandSlot, i.destinationSlot)
    }
    268 -> {
        val i = instruction as NumericInstruction.I32TruncF64SI
        I32TruncF64SIDispatcher(i.operand, i.destinationSlot)
    }
    269 -> {
        val i = instruction as NumericInstruction.I32TruncF64SS
        I32TruncF64SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    270 -> {
        val i = instruction as NumericInstruction.I32TruncF64UI
        I32TruncF64UIDispatcher(i.operand, i.destinationSlot)
    }
    271 -> {
        val i = instruction as NumericInstruction.I32TruncF64US
        I32TruncF64USDispatcher(i.operandSlot, i.destinationSlot)
    }
    272 -> {
        val i = instruction as NumericInstruction.I32TruncSatF32SI
        I32TruncSatF32SIDispatcher(i.operand, i.destinationSlot)
    }
    273 -> {
        val i = instruction as NumericInstruction.I32TruncSatF32SS
        I32TruncSatF32SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    274 -> {
        val i = instruction as NumericInstruction.I32TruncSatF32UI
        I32TruncSatF32UIDispatcher(i.operand, i.destinationSlot)
    }
    275 -> {
        val i = instruction as NumericInstruction.I32TruncSatF32US
        I32TruncSatF32USDispatcher(i.operandSlot, i.destinationSlot)
    }
    276 -> {
        val i = instruction as NumericInstruction.I32TruncSatF64SI
        I32TruncSatF64SIDispatcher(i.operand, i.destinationSlot)
    }
    277 -> {
        val i = instruction as NumericInstruction.I32TruncSatF64SS
        I32TruncSatF64SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    278 -> {
        val i = instruction as NumericInstruction.I32TruncSatF64UI
        I32TruncSatF64UIDispatcher(i.operand, i.destinationSlot)
    }
    279 -> {
        val i = instruction as NumericInstruction.I32TruncSatF64US
        I32TruncSatF64USDispatcher(i.operandSlot, i.destinationSlot)
    }
    280 -> {
        val i = instruction as NumericInstruction.I32WrapI64I
        I32WrapI64IDispatcher(i.operand, i.destinationSlot)
    }
    281 -> {
        val i = instruction as NumericInstruction.I32WrapI64S
        I32WrapI64SDispatcher(i.operandSlot, i.destinationSlot)
    }
    282 -> {
        val i = instruction as NumericInstruction.I64Extend16SI
        I64Extend16SIDispatcher(i.operand, i.destinationSlot)
    }
    283 -> {
        val i = instruction as NumericInstruction.I64Extend16SS
        I64Extend16SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    284 -> {
        val i = instruction as NumericInstruction.I64Extend32SI
        I64Extend32SIDispatcher(i.operand, i.destinationSlot)
    }
    285 -> {
        val i = instruction as NumericInstruction.I64Extend32SS
        I64Extend32SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    286 -> {
        val i = instruction as NumericInstruction.I64Extend8SI
        I64Extend8SIDispatcher(i.operand, i.destinationSlot)
    }
    287 -> {
        val i = instruction as NumericInstruction.I64Extend8SS
        I64Extend8SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions6(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    288 -> {
        val i = instruction as NumericInstruction.I64ExtendI32SI
        I64ExtendI32SIDispatcher(i.operand, i.destinationSlot)
    }
    289 -> {
        val i = instruction as NumericInstruction.I64ExtendI32SS
        I64ExtendI32SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    290 -> {
        val i = instruction as NumericInstruction.I64ExtendI32UI
        I64ExtendI32UIDispatcher(i.operand, i.destinationSlot)
    }
    291 -> {
        val i = instruction as NumericInstruction.I64ExtendI32US
        I64ExtendI32USDispatcher(i.operandSlot, i.destinationSlot)
    }
    292 -> {
        val i = instruction as NumericInstruction.I64TruncF32SI
        I64TruncF32SIDispatcher(i.operand, i.destinationSlot)
    }
    293 -> {
        val i = instruction as NumericInstruction.I64TruncF32SS
        I64TruncF32SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    294 -> {
        val i = instruction as NumericInstruction.I64TruncF32UI
        I64TruncF32UIDispatcher(i.operand, i.destinationSlot)
    }
    295 -> {
        val i = instruction as NumericInstruction.I64TruncF32US
        I64TruncF32USDispatcher(i.operandSlot, i.destinationSlot)
    }
    296 -> {
        val i = instruction as NumericInstruction.I64TruncF64SI
        I64TruncF64SIDispatcher(i.operand, i.destinationSlot)
    }
    297 -> {
        val i = instruction as NumericInstruction.I64TruncF64SS
        I64TruncF64SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    298 -> {
        val i = instruction as NumericInstruction.I64TruncF64UI
        I64TruncF64UIDispatcher(i.operand, i.destinationSlot)
    }
    299 -> {
        val i = instruction as NumericInstruction.I64TruncF64US
        I64TruncF64USDispatcher(i.operandSlot, i.destinationSlot)
    }
    300 -> {
        val i = instruction as NumericInstruction.I64TruncSatF32SI
        I64TruncSatF32SIDispatcher(i.operand, i.destinationSlot)
    }
    301 -> {
        val i = instruction as NumericInstruction.I64TruncSatF32SS
        I64TruncSatF32SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    302 -> {
        val i = instruction as NumericInstruction.I64TruncSatF32UI
        I64TruncSatF32UIDispatcher(i.operand, i.destinationSlot)
    }
    303 -> {
        val i = instruction as NumericInstruction.I64TruncSatF32US
        I64TruncSatF32USDispatcher(i.operandSlot, i.destinationSlot)
    }
    304 -> {
        val i = instruction as NumericInstruction.I64TruncSatF64SI
        I64TruncSatF64SIDispatcher(i.operand, i.destinationSlot)
    }
    305 -> {
        val i = instruction as NumericInstruction.I64TruncSatF64SS
        I64TruncSatF64SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    306 -> {
        val i = instruction as NumericInstruction.I64TruncSatF64UI
        I64TruncSatF64UIDispatcher(i.operand, i.destinationSlot)
    }
    307 -> {
        val i = instruction as NumericInstruction.I64TruncSatF64US
        I64TruncSatF64USDispatcher(i.operandSlot, i.destinationSlot)
    }
    308 -> {
        val i = instruction as NumericInstruction.F32AddIi
        F32AddIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    309 -> {
        val i = instruction as NumericInstruction.F32AddIs
        F32AddIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    310 -> {
        val i = instruction as NumericInstruction.F32AddSi
        F32AddSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    311 -> {
        val i = instruction as NumericInstruction.F32AddSs
        F32AddSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    312 -> {
        val i = instruction as NumericInstruction.F32SubIi
        F32SubIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    313 -> {
        val i = instruction as NumericInstruction.F32SubIs
        F32SubIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    314 -> {
        val i = instruction as NumericInstruction.F32SubSi
        F32SubSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    315 -> {
        val i = instruction as NumericInstruction.F32SubSs
        F32SubSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    316 -> {
        val i = instruction as NumericInstruction.F32MulIi
        F32MulIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    317 -> {
        val i = instruction as NumericInstruction.F32MulIs
        F32MulIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    318 -> {
        val i = instruction as NumericInstruction.F32MulSi
        F32MulSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    319 -> {
        val i = instruction as NumericInstruction.F32MulSs
        F32MulSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    320 -> {
        val i = instruction as NumericInstruction.F32DivIi
        F32DivIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    321 -> {
        val i = instruction as NumericInstruction.F32DivIs
        F32DivIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    322 -> {
        val i = instruction as NumericInstruction.F32DivSi
        F32DivSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    323 -> {
        val i = instruction as NumericInstruction.F32DivSs
        F32DivSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    324 -> {
        val i = instruction as NumericInstruction.F32MinIi
        F32MinIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    325 -> {
        val i = instruction as NumericInstruction.F32MinIs
        F32MinIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    326 -> {
        val i = instruction as NumericInstruction.F32MinSi
        F32MinSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    327 -> {
        val i = instruction as NumericInstruction.F32MinSs
        F32MinSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    328 -> {
        val i = instruction as NumericInstruction.F32MaxIi
        F32MaxIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    329 -> {
        val i = instruction as NumericInstruction.F32MaxIs
        F32MaxIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    330 -> {
        val i = instruction as NumericInstruction.F32MaxSi
        F32MaxSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    331 -> {
        val i = instruction as NumericInstruction.F32MaxSs
        F32MaxSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    332 -> {
        val i = instruction as NumericInstruction.F32CopysignIi
        F32CopysignIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    333 -> {
        val i = instruction as NumericInstruction.F32CopysignIs
        F32CopysignIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    334 -> {
        val i = instruction as NumericInstruction.F32CopysignSi
        F32CopysignSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    335 -> {
        val i = instruction as NumericInstruction.F32CopysignSs
        F32CopysignSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions7(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    336 -> {
        val i = instruction as NumericInstruction.F32AbsI
        F32AbsIDispatcher(i.operand, i.destinationSlot)
    }
    337 -> {
        val i = instruction as NumericInstruction.F32AbsS
        F32AbsSDispatcher(i.operandSlot, i.destinationSlot)
    }
    338 -> {
        val i = instruction as NumericInstruction.F32NegI
        F32NegIDispatcher(i.operand, i.destinationSlot)
    }
    339 -> {
        val i = instruction as NumericInstruction.F32NegS
        F32NegSDispatcher(i.operandSlot, i.destinationSlot)
    }
    340 -> {
        val i = instruction as NumericInstruction.F32CeilI
        F32CeilIDispatcher(i.operand, i.destinationSlot)
    }
    341 -> {
        val i = instruction as NumericInstruction.F32CeilS
        F32CeilSDispatcher(i.operandSlot, i.destinationSlot)
    }
    342 -> {
        val i = instruction as NumericInstruction.F32FloorI
        F32FloorIDispatcher(i.operand, i.destinationSlot)
    }
    343 -> {
        val i = instruction as NumericInstruction.F32FloorS
        F32FloorSDispatcher(i.operandSlot, i.destinationSlot)
    }
    344 -> {
        val i = instruction as NumericInstruction.F32TruncI
        F32TruncIDispatcher(i.operand, i.destinationSlot)
    }
    345 -> {
        val i = instruction as NumericInstruction.F32TruncS
        F32TruncSDispatcher(i.operandSlot, i.destinationSlot)
    }
    346 -> {
        val i = instruction as NumericInstruction.F32NearestI
        F32NearestIDispatcher(i.operand, i.destinationSlot)
    }
    347 -> {
        val i = instruction as NumericInstruction.F32NearestS
        F32NearestSDispatcher(i.operandSlot, i.destinationSlot)
    }
    348 -> {
        val i = instruction as NumericInstruction.F32SqrtI
        F32SqrtIDispatcher(i.operand, i.destinationSlot)
    }
    349 -> {
        val i = instruction as NumericInstruction.F32SqrtS
        F32SqrtSDispatcher(i.operandSlot, i.destinationSlot)
    }
    350 -> {
        val i = instruction as NumericInstruction.F32EqIi
        F32EqIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    351 -> {
        val i = instruction as NumericInstruction.F32EqIs
        F32EqIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    352 -> {
        val i = instruction as NumericInstruction.F32EqSi
        F32EqSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    353 -> {
        val i = instruction as NumericInstruction.F32EqSs
        F32EqSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    354 -> {
        val i = instruction as NumericInstruction.F32NeIi
        F32NeIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    355 -> {
        val i = instruction as NumericInstruction.F32NeIs
        F32NeIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    356 -> {
        val i = instruction as NumericInstruction.F32NeSi
        F32NeSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    357 -> {
        val i = instruction as NumericInstruction.F32NeSs
        F32NeSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    358 -> {
        val i = instruction as NumericInstruction.F32LtIi
        F32LtIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    359 -> {
        val i = instruction as NumericInstruction.F32LtIs
        F32LtIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    360 -> {
        val i = instruction as NumericInstruction.F32LtSi
        F32LtSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    361 -> {
        val i = instruction as NumericInstruction.F32LtSs
        F32LtSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    362 -> {
        val i = instruction as NumericInstruction.F32GtIi
        F32GtIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    363 -> {
        val i = instruction as NumericInstruction.F32GtIs
        F32GtIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    364 -> {
        val i = instruction as NumericInstruction.F32GtSi
        F32GtSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    365 -> {
        val i = instruction as NumericInstruction.F32GtSs
        F32GtSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    366 -> {
        val i = instruction as NumericInstruction.F32LeIi
        F32LeIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    367 -> {
        val i = instruction as NumericInstruction.F32LeIs
        F32LeIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    368 -> {
        val i = instruction as NumericInstruction.F32LeSi
        F32LeSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    369 -> {
        val i = instruction as NumericInstruction.F32LeSs
        F32LeSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    370 -> {
        val i = instruction as NumericInstruction.F32GeIi
        F32GeIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    371 -> {
        val i = instruction as NumericInstruction.F32GeIs
        F32GeIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    372 -> {
        val i = instruction as NumericInstruction.F32GeSi
        F32GeSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    373 -> {
        val i = instruction as NumericInstruction.F32GeSs
        F32GeSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    374 -> {
        val i = instruction as NumericInstruction.F32ConvertI32SI
        F32ConvertI32SIDispatcher(i.operand, i.destinationSlot)
    }
    375 -> {
        val i = instruction as NumericInstruction.F32ConvertI32SS
        F32ConvertI32SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    376 -> {
        val i = instruction as NumericInstruction.F32ConvertI32UI
        F32ConvertI32UIDispatcher(i.operand, i.destinationSlot)
    }
    377 -> {
        val i = instruction as NumericInstruction.F32ConvertI32US
        F32ConvertI32USDispatcher(i.operandSlot, i.destinationSlot)
    }
    378 -> {
        val i = instruction as NumericInstruction.F32ConvertI64SI
        F32ConvertI64SIDispatcher(i.operand, i.destinationSlot)
    }
    379 -> {
        val i = instruction as NumericInstruction.F32ConvertI64SS
        F32ConvertI64SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    380 -> {
        val i = instruction as NumericInstruction.F32ConvertI64UI
        F32ConvertI64UIDispatcher(i.operand, i.destinationSlot)
    }
    381 -> {
        val i = instruction as NumericInstruction.F32ConvertI64US
        F32ConvertI64USDispatcher(i.operandSlot, i.destinationSlot)
    }
    382 -> {
        val i = instruction as NumericInstruction.F32DemoteF64I
        F32DemoteF64IDispatcher(i.operand, i.destinationSlot)
    }
    383 -> {
        val i = instruction as NumericInstruction.F32DemoteF64S
        F32DemoteF64SDispatcher(i.operandSlot, i.destinationSlot)
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions8(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    384 -> {
        val i = instruction as NumericInstruction.F64AddIi
        F64AddIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    385 -> {
        val i = instruction as NumericInstruction.F64AddIs
        F64AddIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    386 -> {
        val i = instruction as NumericInstruction.F64AddSi
        F64AddSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    387 -> {
        val i = instruction as NumericInstruction.F64AddSs
        F64AddSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    388 -> {
        val i = instruction as NumericInstruction.F64SubIi
        F64SubIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    389 -> {
        val i = instruction as NumericInstruction.F64SubIs
        F64SubIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    390 -> {
        val i = instruction as NumericInstruction.F64SubSi
        F64SubSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    391 -> {
        val i = instruction as NumericInstruction.F64SubSs
        F64SubSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    392 -> {
        val i = instruction as NumericInstruction.F64MulIi
        F64MulIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    393 -> {
        val i = instruction as NumericInstruction.F64MulIs
        F64MulIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    394 -> {
        val i = instruction as NumericInstruction.F64MulSi
        F64MulSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    395 -> {
        val i = instruction as NumericInstruction.F64MulSs
        F64MulSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    396 -> {
        val i = instruction as NumericInstruction.F64DivIi
        F64DivIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    397 -> {
        val i = instruction as NumericInstruction.F64DivIs
        F64DivIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    398 -> {
        val i = instruction as NumericInstruction.F64DivSi
        F64DivSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    399 -> {
        val i = instruction as NumericInstruction.F64DivSs
        F64DivSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    400 -> {
        val i = instruction as NumericInstruction.F64MinIi
        F64MinIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    401 -> {
        val i = instruction as NumericInstruction.F64MinIs
        F64MinIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    402 -> {
        val i = instruction as NumericInstruction.F64MinSi
        F64MinSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    403 -> {
        val i = instruction as NumericInstruction.F64MinSs
        F64MinSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    404 -> {
        val i = instruction as NumericInstruction.F64MaxIi
        F64MaxIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    405 -> {
        val i = instruction as NumericInstruction.F64MaxIs
        F64MaxIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    406 -> {
        val i = instruction as NumericInstruction.F64MaxSi
        F64MaxSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    407 -> {
        val i = instruction as NumericInstruction.F64MaxSs
        F64MaxSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    408 -> {
        val i = instruction as NumericInstruction.F64CopysignIi
        F64CopysignIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    409 -> {
        val i = instruction as NumericInstruction.F64CopysignIs
        F64CopysignIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    410 -> {
        val i = instruction as NumericInstruction.F64CopysignSi
        F64CopysignSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    411 -> {
        val i = instruction as NumericInstruction.F64CopysignSs
        F64CopysignSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    412 -> {
        val i = instruction as NumericInstruction.F64AbsI
        F64AbsIDispatcher(i.operand, i.destinationSlot)
    }
    413 -> {
        val i = instruction as NumericInstruction.F64AbsS
        F64AbsSDispatcher(i.operandSlot, i.destinationSlot)
    }
    414 -> {
        val i = instruction as NumericInstruction.F64NegI
        F64NegIDispatcher(i.operand, i.destinationSlot)
    }
    415 -> {
        val i = instruction as NumericInstruction.F64NegS
        F64NegSDispatcher(i.operandSlot, i.destinationSlot)
    }
    416 -> {
        val i = instruction as NumericInstruction.F64CeilI
        F64CeilIDispatcher(i.operand, i.destinationSlot)
    }
    417 -> {
        val i = instruction as NumericInstruction.F64CeilS
        F64CeilSDispatcher(i.operandSlot, i.destinationSlot)
    }
    418 -> {
        val i = instruction as NumericInstruction.F64FloorI
        F64FloorIDispatcher(i.operand, i.destinationSlot)
    }
    419 -> {
        val i = instruction as NumericInstruction.F64FloorS
        F64FloorSDispatcher(i.operandSlot, i.destinationSlot)
    }
    420 -> {
        val i = instruction as NumericInstruction.F64TruncI
        F64TruncIDispatcher(i.operand, i.destinationSlot)
    }
    421 -> {
        val i = instruction as NumericInstruction.F64TruncS
        F64TruncSDispatcher(i.operandSlot, i.destinationSlot)
    }
    422 -> {
        val i = instruction as NumericInstruction.F64NearestI
        F64NearestIDispatcher(i.operand, i.destinationSlot)
    }
    423 -> {
        val i = instruction as NumericInstruction.F64NearestS
        F64NearestSDispatcher(i.operandSlot, i.destinationSlot)
    }
    424 -> {
        val i = instruction as NumericInstruction.F64SqrtI
        F64SqrtIDispatcher(i.operand, i.destinationSlot)
    }
    425 -> {
        val i = instruction as NumericInstruction.F64SqrtS
        F64SqrtSDispatcher(i.operandSlot, i.destinationSlot)
    }
    426 -> {
        val i = instruction as NumericInstruction.F64EqIi
        F64EqIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    427 -> {
        val i = instruction as NumericInstruction.F64EqIs
        F64EqIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    428 -> {
        val i = instruction as NumericInstruction.F64EqSi
        F64EqSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    429 -> {
        val i = instruction as NumericInstruction.F64EqSs
        F64EqSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    430 -> {
        val i = instruction as NumericInstruction.F64NeIi
        F64NeIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    431 -> {
        val i = instruction as NumericInstruction.F64NeIs
        F64NeIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions9(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    432 -> {
        val i = instruction as NumericInstruction.F64NeSi
        F64NeSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    433 -> {
        val i = instruction as NumericInstruction.F64NeSs
        F64NeSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    434 -> {
        val i = instruction as NumericInstruction.F64LtIi
        F64LtIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    435 -> {
        val i = instruction as NumericInstruction.F64LtIs
        F64LtIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    436 -> {
        val i = instruction as NumericInstruction.F64LtSi
        F64LtSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    437 -> {
        val i = instruction as NumericInstruction.F64LtSs
        F64LtSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    438 -> {
        val i = instruction as NumericInstruction.F64GtIi
        F64GtIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    439 -> {
        val i = instruction as NumericInstruction.F64GtIs
        F64GtIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    440 -> {
        val i = instruction as NumericInstruction.F64GtSi
        F64GtSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    441 -> {
        val i = instruction as NumericInstruction.F64GtSs
        F64GtSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    442 -> {
        val i = instruction as NumericInstruction.F64LeIi
        F64LeIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    443 -> {
        val i = instruction as NumericInstruction.F64LeIs
        F64LeIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    444 -> {
        val i = instruction as NumericInstruction.F64LeSi
        F64LeSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    445 -> {
        val i = instruction as NumericInstruction.F64LeSs
        F64LeSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    446 -> {
        val i = instruction as NumericInstruction.F64GeIi
        F64GeIiDispatcher(i.left, i.right, i.destinationSlot)
    }
    447 -> {
        val i = instruction as NumericInstruction.F64GeIs
        F64GeIsDispatcher(i.left, i.rightSlot, i.destinationSlot)
    }
    448 -> {
        val i = instruction as NumericInstruction.F64GeSi
        F64GeSiDispatcher(i.leftSlot, i.right, i.destinationSlot)
    }
    449 -> {
        val i = instruction as NumericInstruction.F64GeSs
        F64GeSsDispatcher(i.leftSlot, i.rightSlot, i.destinationSlot)
    }
    450 -> {
        val i = instruction as NumericInstruction.F64ConvertI32SI
        F64ConvertI32SIDispatcher(i.operand, i.destinationSlot)
    }
    451 -> {
        val i = instruction as NumericInstruction.F64ConvertI32SS
        F64ConvertI32SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    452 -> {
        val i = instruction as NumericInstruction.F64ConvertI32UI
        F64ConvertI32UIDispatcher(i.operand, i.destinationSlot)
    }
    453 -> {
        val i = instruction as NumericInstruction.F64ConvertI32US
        F64ConvertI32USDispatcher(i.operandSlot, i.destinationSlot)
    }
    454 -> {
        val i = instruction as NumericInstruction.F64ConvertI64SI
        F64ConvertI64SIDispatcher(i.operand, i.destinationSlot)
    }
    455 -> {
        val i = instruction as NumericInstruction.F64ConvertI64SS
        F64ConvertI64SSDispatcher(i.operandSlot, i.destinationSlot)
    }
    456 -> {
        val i = instruction as NumericInstruction.F64ConvertI64UI
        F64ConvertI64UIDispatcher(i.operand, i.destinationSlot)
    }
    457 -> {
        val i = instruction as NumericInstruction.F64ConvertI64US
        F64ConvertI64USDispatcher(i.operandSlot, i.destinationSlot)
    }
    458 -> {
        val i = instruction as NumericInstruction.F64PromoteF32I
        F64PromoteF32IDispatcher(i.operand, i.destinationSlot)
    }
    459 -> {
        val i = instruction as NumericInstruction.F64PromoteF32S
        F64PromoteF32SDispatcher(i.operandSlot, i.destinationSlot)
    }
    460 -> {
        val i = instruction as MemoryInstruction.I32LoadI
        I32LoadIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    461 -> {
        val i = instruction as MemoryInstruction.I32LoadS
        I32LoadSDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    462 -> {
        val i = instruction as MemoryInstruction.I64LoadI
        I64LoadIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    463 -> {
        val i = instruction as MemoryInstruction.I64LoadS
        I64LoadSDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    464 -> {
        val i = instruction as MemoryInstruction.F32LoadI
        F32LoadIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    465 -> {
        val i = instruction as MemoryInstruction.F32LoadS
        F32LoadSDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    466 -> {
        val i = instruction as MemoryInstruction.F64LoadI
        F64LoadIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    467 -> {
        val i = instruction as MemoryInstruction.F64LoadS
        F64LoadSDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    468 -> {
        val i = instruction as MemoryInstruction.I32Load8SI
        I32Load8SIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    469 -> {
        val i = instruction as MemoryInstruction.I32Load8SS
        I32Load8SSDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    470 -> {
        val i = instruction as MemoryInstruction.I32Load8UI
        I32Load8UIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    471 -> {
        val i = instruction as MemoryInstruction.I32Load8US
        I32Load8USDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    472 -> {
        val i = instruction as MemoryInstruction.I32Load16SI
        I32Load16SIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    473 -> {
        val i = instruction as MemoryInstruction.I32Load16SS
        I32Load16SSDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    474 -> {
        val i = instruction as MemoryInstruction.I32Load16UI
        I32Load16UIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    475 -> {
        val i = instruction as MemoryInstruction.I32Load16US
        I32Load16USDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    476 -> {
        val i = instruction as MemoryInstruction.I64Load8SI
        I64Load8SIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    477 -> {
        val i = instruction as MemoryInstruction.I64Load8SS
        I64Load8SSDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    478 -> {
        val i = instruction as MemoryInstruction.I64Load8UI
        I64Load8UIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    479 -> {
        val i = instruction as MemoryInstruction.I64Load8US
        I64Load8USDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    else -> error("opcode $opcode is not linked here")
}
