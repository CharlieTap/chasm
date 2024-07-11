package io.github.charlietap.chasm.validator.resolver

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.type.InstructionType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun MemoryInstructionTypeResolverImpl(
    context: ValidationContext,
    instruction: MemoryInstruction,
): Result<InstructionType, ModuleValidatorError> = binding {
    when (instruction) {
        is MemoryInstruction.I32Load -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        is MemoryInstruction.I32Load16S -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        is MemoryInstruction.I32Load16U -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        is MemoryInstruction.I32Load8S -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        is MemoryInstruction.I32Load8U -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        is MemoryInstruction.I32Store -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.I32Store16 -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.I32Store8 -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.I64Load -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        is MemoryInstruction.I64Load16S -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        is MemoryInstruction.I64Load16U -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        is MemoryInstruction.I64Load32S -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        is MemoryInstruction.I64Load32U -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        is MemoryInstruction.I64Load8S -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        is MemoryInstruction.I64Load8U -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        is MemoryInstruction.I64Store -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I64))),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.I64Store16 -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I64))),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.I64Store32 -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I64))),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.I64Store8 -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I64))),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.F32Load -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        is MemoryInstruction.F32Store -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.F32))),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.F64Load -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        is MemoryInstruction.F64Store -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.F64))),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.MemorySize -> InstructionType(
            inputs = ResultType(emptyList()),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        is MemoryInstruction.MemoryGrow -> InstructionType(
            inputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
            outputs = ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        is MemoryInstruction.MemoryFill -> InstructionType(
            inputs = ResultType(
                listOf(
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I32),
                ),
            ),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.MemoryCopy -> InstructionType(
            inputs = ResultType(
                listOf(
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I32),
                ),
            ),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.MemoryInit -> InstructionType(
            inputs = ResultType(
                listOf(
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I32),
                    ValueType.Number(NumberType.I32),
                ),
            ),
            outputs = ResultType(emptyList()),
        )
        is MemoryInstruction.DataDrop -> InstructionType(
            inputs = ResultType(emptyList()),
            outputs = ResultType(emptyList()),
        )
    }
}
