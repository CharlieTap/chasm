package io.github.charlietap.chasm.validator.validator.instruction

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.LabelKind
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionBlockValidatorTest {

    @Test
    fun `can validate deeply nested blocks without recursion`() {
        val context = context()
        val depth = 50_000
        val instructions = buildList {
            repeat(depth) {
                add(ControlInstruction.Block(BlockType.Empty))
            }
            add(ControlInstruction.End(depth))
        }

        val result = InstructionBlockValidator(context, instructions)

        assertEquals(Ok(Unit), result)
    }

    @Test
    fun `rejects deeply nested blocks without recursing through the error path`() {
        val context = context()
        val depth = 50_000
        val instructions = buildList {
            repeat(depth) {
                add(ControlInstruction.Block(BlockType.Empty))
            }
            add(ControlInstruction.End(depth + 1))
        }

        val result = InstructionBlockValidator(context, instructions)

        assertEquals(Err(TypeValidatorError.TypeMismatch), result)
    }

    @Test
    fun `resets unreachable state when entering an else branch`() {
        val context = context()
        val instructions = listOf(
            NumericInstruction.I32Const(0),
            ControlInstruction.If(BlockType.Empty),
            ControlInstruction.Unreachable,
            ControlInstruction.Else,
            ControlInstruction.Nop,
            ControlInstruction.End(1),
        )

        val result = InstructionBlockValidator(context, instructions)

        assertEquals(Ok(Unit), result)
    }

    @Test
    fun `preserves multi-value parameter order across else branches`() {
        val i32 = ValueType.Number(NumberType.I32)
        val i64 = ValueType.Number(NumberType.I64)
        val blockType = functionType(
            params = ResultType(listOf(i32, i64)),
            results = ResultType(listOf(i32, i64)),
        )
        val context = context(blockType)
        val instructions = listOf(
            NumericInstruction.I32Const(0),
            NumericInstruction.I64Const(0),
            NumericInstruction.I32Const(0),
            ControlInstruction.If(BlockType.SignedTypeIndex(0)),
            ControlInstruction.Else,
            ControlInstruction.End(1),
            ParametricInstruction.Drop,
            ParametricInstruction.Drop,
        )

        val result = InstructionBlockValidator(context, instructions)

        assertEquals(Ok(Unit), result)
    }

    @Test
    fun `propagates results through a counted end`() {
        val i32 = ValueType.Number(NumberType.I32)
        val instructions = listOf(
            ControlInstruction.Block(BlockType.ValType(i32)),
            ControlInstruction.Block(BlockType.ValType(i32)),
            NumericInstruction.I32Const(0),
            ControlInstruction.End(2),
            ParametricInstruction.Drop,
        )

        val result = InstructionBlockValidator(context(), instructions)

        assertEquals(Ok(Unit), result)
    }

    @Test
    fun `validates try table bodies through the flat stream`() {
        val instructions = listOf(
            ControlInstruction.TryTable(BlockType.Empty, emptyList()),
            ControlInstruction.End(1),
        )

        val result = InstructionBlockValidator(context(), instructions)

        assertEquals(Ok(Unit), result)
    }

    @Test
    fun `rejects an else marker outside an if then branch`() {
        val context = context()

        val result = InstructionBlockValidator(context, listOf(ControlInstruction.Else))

        assertEquals(Err(TypeValidatorError.TypeMismatch), result)
    }

    @Test
    fun `rejects an else marker while a nested block is open`() {
        val context = context()
        val instructions = listOf(
            NumericInstruction.I32Const(0),
            ControlInstruction.If(BlockType.Empty),
            ControlInstruction.Block(BlockType.Empty),
            ControlInstruction.Else,
        )

        val result = InstructionBlockValidator(context, instructions)

        assertEquals(Err(TypeValidatorError.TypeMismatch), result)
    }

    @Test
    fun `rejects a duplicate else marker`() {
        val context = context()
        val instructions = listOf(
            NumericInstruction.I32Const(0),
            ControlInstruction.If(BlockType.Empty),
            ControlInstruction.Else,
            ControlInstruction.Else,
        )

        val result = InstructionBlockValidator(context, instructions)

        assertEquals(Err(TypeValidatorError.TypeMismatch), result)
    }

    @Test
    fun `rejects an unclosed block`() {
        val context = context()

        val result = InstructionBlockValidator(context, listOf(ControlInstruction.Block(BlockType.Empty)))

        assertEquals(Err(TypeValidatorError.TypeMismatch), result)
    }

    @Test
    fun `rejects an end marker that closes the function label`() {
        val context = context()

        val result = InstructionBlockValidator(context, listOf(ControlInstruction.End(1)))

        assertEquals(Err(TypeValidatorError.TypeMismatch), result)
    }

    private fun context(
        functionType: FunctionType? = null,
    ): ModuleValidationContext {
        val definedTypes = functionType?.let { type ->
            listOf(definedType(recursiveType = functionRecursiveType(type)))
        } ?: emptyList()
        return ModuleValidationContext(ModuleConfig(), module(definedTypes = definedTypes)).apply {
            definedTypesValidated = definedTypes.size
            labels.push(
                Label(
                    kind = LabelKind.Function,
                    inputs = ResultType(emptyList()),
                    outputs = ResultType(emptyList()),
                    operandsDepth = 0,
                    localChangesDepth = 0,
                    unreachable = false,
                ),
            )
        }
    }
}
