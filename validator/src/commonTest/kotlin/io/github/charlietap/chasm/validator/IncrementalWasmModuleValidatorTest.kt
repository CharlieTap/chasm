package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IncrementalWasmModuleValidatorTest {

    @Test
    fun `a valid function validates as it does serially`() {
        val body = listOf(NOP to ControlInstruction.Nop, NOP to ControlInstruction.Nop)

        assertEquals(Ok(Unit), validateIncrementally(body))
        assertTrue(validateSerially(body).isOk)
    }

    @Test
    fun `an invalid function reports the error a serial validator does`() {
        val body = listOf(NOP to ControlInstruction.Nop, DROP to ParametricInstruction.Drop)

        val incremental = validateIncrementally(body)

        assertTrue(incremental.isErr)
        assertEquals(validateSerially(body).toString(), incremental.toString())
    }

    @Test
    fun `a function must leave nothing but its results`() {
        val body = listOf(I32_CONST to NumericInstruction.I32Const(1))

        val incremental = validateIncrementally(body)

        assertTrue(incremental.isErr)
        assertEquals(validateSerially(body).toString(), incremental.toString())
    }

    @Test
    fun `a function that is never ended does not affect the next`() {
        val module = module()
        val validator = IncrementalWasmModuleValidator(ModuleConfig(), module, null)
        val functionValidator = validator.functionValidator()

        functionValidator.begin(module.functions[0].typeIndex, emptyList())
        functionValidator.instruction(I32_CONST, NumericInstruction.I32Const(1))

        functionValidator.begin(module.functions[0].typeIndex, emptyList())
        functionValidator.instruction(NOP, ControlInstruction.Nop)

        assertEquals(Ok(Unit), functionValidator.end())
    }

    @Test
    fun `the prelude and remainder of a valid module validate`() {
        val validator = IncrementalWasmModuleValidator(ModuleConfig(), module(), null)

        assertEquals(Ok(Unit), validator.validatePrelude())
        assertEquals(Ok(Unit), validator.validateRemainder(emptyList()))
    }

    private fun validateIncrementally(body: List<Pair<Int, Instruction>>): Result<Unit, ModuleValidatorError> {
        val module = module()
        val validator = IncrementalWasmModuleValidator(ModuleConfig(), module, null)
        assertEquals(Ok(Unit), validator.validatePrelude())

        val functionValidator = validator.functionValidator()
        functionValidator.begin(module.functions[0].typeIndex, emptyList())
        body.forEach { (opcode, instruction) ->
            functionValidator.instruction(opcode, instruction)
        }
        return functionValidator.end()
    }

    private fun validateSerially(body: List<Pair<Int, Instruction>>): Result<Module, ModuleValidatorError> =
        WasmModuleValidator(ModuleConfig(), module(Expression(body.map { it.second })))

    /** A module with one function of type `[] -> []`. */
    private fun module(body: Expression = Expression(emptyList())): Module {
        val recursiveType = functionRecursiveType()
        return module(
            types = listOf(type(recursiveType = recursiveType)),
            definedTypes = listOf(definedType(recursiveType = recursiveType)),
            functions = listOf(function(body = body)),
        )
    }

    private companion object {
        const val NOP = 0x01
        const val DROP = 0x1A
        const val I32_CONST = 0x41
    }
}
