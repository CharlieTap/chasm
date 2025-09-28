package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.fixture.ir.instruction.arrayNewDataInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.arrayNewDefaultInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.arrayNewElementInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.arrayNewFixedInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.arrayNewInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.structNewDefaultInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.structNewInstruction
import io.github.charlietap.chasm.fixture.ir.module.export
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.functionIndex
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.module.Export
import kotlin.test.Test
import kotlin.test.assertEquals

class GCPassTest {

    @Test
    fun `makes no alterations to functions with gc instructions when GC strategy is MANUAL`() {

        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions = listOf(
                            structNewInstruction(),
                        ),
                    ),
                ),
            ),
        )

        val config = runtimeConfig(
            gcStrategy = GCStrategy.MANUAL,
        )
        val context = passContext(config, module)

        val result = GCPass(context, module)

        assertEquals(module, result)
    }

    @Test
    fun `appends a Pause instruction to exported functions with ARENA strategy`() {

        val instructions = listOf(
            structNewInstruction(),
        )

        val functionIdx = functionIndex(0)

        val module = module(
            functions = listOf(
                function(
                    idx = functionIdx,
                    body = expression(
                        instructions,
                    ),
                ),
            ),
            exports = listOf(
                export(
                    descriptor = Export.Descriptor.Function(functionIdx),
                ),
            ),
        )

        val config = runtimeConfig(
            gcStrategy = GCStrategy.ARENA,
        )
        val context = passContext(config, module)

        val result = GCPass(context, module)

        val expectedInstructions = instructions + listOf(AdminInstruction.Pause)
        assertEquals(
            expectedInstructions,
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `appends nothing to non-exported functions with ARENA strategy`() {
        val instructions = listOf(
            structNewInstruction(),
        )

        val module = module(
            functions = listOf(
                function(
                    body = expression(
                        instructions,
                    ),
                ),
            ),
        )

        val config = runtimeConfig(
            gcStrategy = GCStrategy.ARENA,
        )
        val context = passContext(config, module)

        val result = GCPass(context, module)

        assertEquals(module.functions[0].body.instructions, result.functions[0].body.instructions)
    }

    @Test
    fun `appends PauseIf instructions for each allocating instruction with TRADITIONAL strategy`() {

        val instructions = listOf(
            structNewInstruction(),
            structNewDefaultInstruction(),
            arrayNewInstruction(),
            arrayNewDataInstruction(),
            arrayNewFixedInstruction(),
            arrayNewElementInstruction(),
            arrayNewDefaultInstruction(),
        )

        val functionIdx = functionIndex(0)

        val module = module(
            functions = listOf(
                function(
                    idx = functionIdx,
                    body = expression(
                        instructions,
                    ),
                ),
            ),
            exports = listOf(
                export(
                    descriptor = Export.Descriptor.Function(functionIdx),
                ),
            ),
        )

        val config = runtimeConfig(
            gcStrategy = GCStrategy.TRADITIONAL,
        )
        val context = passContext(config, module)

        val result = GCPass(context, module)

        val expectedInstructions = listOf(
            structNewInstruction(),
            AdminInstruction.PauseIf,
            structNewDefaultInstruction(),
            AdminInstruction.PauseIf,
            arrayNewInstruction(),
            AdminInstruction.PauseIf,
            arrayNewDataInstruction(),
            AdminInstruction.PauseIf,
            arrayNewFixedInstruction(),
            AdminInstruction.PauseIf,
            arrayNewElementInstruction(),
            AdminInstruction.PauseIf,
            arrayNewDefaultInstruction(),
            AdminInstruction.PauseIf,
        )

        assertEquals(
            expectedInstructions,
            result.functions[0].body.instructions,
        )
    }

    @Test
    fun `can skip modules without GC instructions`() {

        val module = module(
            functions = listOf(
                function(),
            ),
        )

        val config = runtimeConfig(
            gcStrategy = GCStrategy.ARENA,
        )
        val context = passContext(config, module)

        val result = GCPass(context, module)

        assertEquals(module, result)
    }

    @Test
    fun `can handle multiple exported functions with ARENA strategy`() {
        val exportedFunction1 = function(
            idx = functionIndex(0),
            body = expression(
                listOf(structNewInstruction()),
            ),
        )

        val exportedFunction2 = function(
            idx = functionIndex(1),
            body = expression(
                listOf(structNewInstruction()),
            ),
        )

        val nonExportedFunction = function(
            idx = functionIndex(2),
            body = expression(
                listOf(structNewInstruction()),
            ),
        )

        val module = module(
            functions = listOf(
                exportedFunction1,
                exportedFunction2,
                nonExportedFunction,
            ),
            exports = listOf(
                export(
                    descriptor = Export.Descriptor.Function(functionIndex(0)),
                ),
                export(
                    descriptor = Export.Descriptor.Function(functionIndex(1)),
                ),
            ),
        )

        val config = runtimeConfig(
            gcStrategy = GCStrategy.ARENA,
        )
        val context = passContext(config, module)

        val result = GCPass(context, module)

        assertEquals(
            exportedFunction1.body.instructions + listOf(AdminInstruction.Pause),
            result.functions[0].body.instructions,
        )

        assertEquals(
            exportedFunction2.body.instructions + listOf(AdminInstruction.Pause),
            result.functions[1].body.instructions,
        )

        assertEquals(
            nonExportedFunction.body.instructions,
            result.functions[2].body.instructions,
        )
    }
}
