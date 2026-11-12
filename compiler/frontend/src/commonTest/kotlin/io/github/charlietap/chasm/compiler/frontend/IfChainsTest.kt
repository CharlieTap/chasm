package io.github.charlietap.chasm.compiler.frontend

import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.FusedOperand
import io.github.charlietap.chasm.cir.NumericCondition
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.type.BlockType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import io.github.charlietap.chasm.cir.Instruction as CirInstruction
import io.github.charlietap.chasm.cir.NumericInstruction as CirNumericInstruction

class IfChainsTest {

    private val binary = definedType(
        recursiveType = functionRecursiveType(
            functionType(
                params = resultType(listOf(i32ValueType(), i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
        ),
    )

    @Test
    fun `four tests of one local against dense constants become one jump table`() {
        val code = compile(chain(0 to 2, 0 to 0, 0 to 3, 0 to 1))

        val table = code.filterIsInstance<AdminInstruction.JumpTableS>().single()
        val targets = table.targets
        assertEquals(0, table.operandSlot)
        assertEquals(5, targets.size)
        assertEquals(listOf(0, 1, 2, 3), listOf(targets[2], targets[0], targets[3], targets[1]).map { target -> arm(code, target) })
        assertEquals(-1, arm(code, targets[4]))
        assertEquals(emptyList(), tests(code))
    }

    @Test
    fun `constants missing from the range take the default`() {
        val code = compile(chain(0 to 0, 0 to 1, 0 to 3, 0 to 4))

        val targets = code.filterIsInstance<AdminInstruction.JumpTableS>().single().targets
        assertEquals(6, targets.size)
        assertEquals(targets[5], targets[2])
        assertEquals(-1, arm(code, targets[2]))
        assertEquals(3, arm(code, targets[4]))
    }

    @Test
    fun `a range away from zero is rebased before the table`() {
        val code = compile(chain(1 to 65344, 1 to 65345, 1 to 65346, 1 to 65347))

        val table = code.filterIsInstance<AdminInstruction.JumpTableS>().single()
        val rebase = code[code.indexOf(table) - 1] as CirNumericInstruction.I32AddSi
        assertEquals(1, rebase.leftSlot)
        assertEquals(-65344, rebase.right)
        assertEquals(rebase.destinationSlot, table.operandSlot)
        assertEquals(5, table.targets.size)
        assertEquals(listOf(0, 1, 2, 3, -1), table.targets.map { target -> arm(code, target) })
    }

    @Test
    fun `a sparse tail stays a compare behind the table default`() {
        val code = compile(chain(0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 1000))

        val table = code.filterIsInstance<AdminInstruction.JumpTableS>().single()
        assertEquals(5, table.targets.size)
        assertEquals(listOf(1000), tests(code))
        assertEquals(NumericCondition.I32Eq(FusedOperand.FrameSlot(0), FusedOperand.I32Const(1000)), (code[table.targets[4]] as AdminInstruction.JumpIfConditionMismatch).condition)
    }

    @Test
    fun `a dense run after a sparse constant still becomes a table`() {
        val code = compile(chain(0 to 1000, 0 to 0, 0 to 1, 0 to 2, 0 to 3))

        val table = code.filterIsInstance<AdminInstruction.JumpTableS>().single()
        assertEquals(listOf(1000), tests(code))
        assertEquals(listOf(1, 2, 3, 4, -1), table.targets.map { target -> arm(code, target) })
    }

    @Test
    fun `three tests stay compares`() {
        val code = compile(chain(0 to 0, 0 to 1, 0 to 2))

        assertEquals(listOf(0, 1, 2), tests(code))
        assertTrue(code.none { instruction -> instruction is AdminInstruction.JumpTableS })
    }

    @Test
    fun `sparse constants stay compares`() {
        val code = compile(chain(0 to 0, 0 to 100, 0 to 200, 0 to 300))

        assertEquals(listOf(0, 100, 200, 300), tests(code))
        assertTrue(code.none { instruction -> instruction is AdminInstruction.JumpTableS })
    }

    @Test
    fun `a repeated constant ends the chain`() {
        val code = compile(chain(0 to 0, 0 to 1, 0 to 2, 0 to 0))

        assertEquals(listOf(0, 1, 2, 0), tests(code))
        assertTrue(code.none { instruction -> instruction is AdminInstruction.JumpTableS })
    }

    @Test
    fun `a test of another local ends the chain`() {
        val code = compile(chain(0 to 0, 0 to 1, 1 to 2, 0 to 3, 0 to 4))

        assertEquals(listOf(0, 1, 2, 3, 4), tests(code))
        assertTrue(code.none { instruction -> instruction is AdminInstruction.JumpTableS })
    }

    @Test
    fun `code between two tests ends the chain`() {
        val write = listOf(NumericInstruction.I32Const(7), VariableInstruction.LocalSet(Index.LocalIndex(0u)))
        val code = compile(chain(0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4, between = mapOf(2 to write)))

        assertEquals(listOf(0, 1, 2, 3, 4), tests(code))
        assertTrue(code.none { instruction -> instruction is AdminInstruction.JumpTableS })
    }

    private fun chain(
        vararg tests: Pair<Int, Int>,
        between: Map<Int, List<Instruction>> = emptyMap(),
    ): Expression {
        val body = ArrayList<Instruction>()
        tests.forEachIndexed { index, (local, constant) ->
            between[index]?.let(body::addAll)
            body += VariableInstruction.LocalGet(Index.LocalIndex(local.toUInt()))
            body += NumericInstruction.I32Const(constant)
            body += NumericInstruction.I32Eq
            body += ControlInstruction.If(BlockType.ValType(i32ValueType()))
            body += NumericInstruction.I32Const(ARM + index)
            body += ControlInstruction.Else
        }
        body += NumericInstruction.I32Const(ARM - 1)
        body += ControlInstruction.End(tests.size)
        return Expression(body)
    }

    private fun compile(
        body: Expression,
    ): List<CirInstruction> {
        val module = module(definedTypes = listOf(binary), functions = listOf(function(body = body)))
        return ModuleCompiler(module).unwrap().functions[0].instructions.toList()
    }

    private fun arm(
        code: List<CirInstruction>,
        target: Int,
    ): Int = (code[target] as CirNumericInstruction.I32ConstS).value - ARM

    private fun tests(
        code: List<CirInstruction>,
    ): List<Int> = code.filterIsInstance<AdminInstruction.JumpIfConditionMismatch>().map { test ->
        ((test.condition as NumericCondition.I32Eq).right as FusedOperand.I32Const).const
    }

    private companion object {
        const val ARM = 77_000
    }
}
