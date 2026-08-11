package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContrivedCompilerBaselineTest {

    @Test
    fun `covers compiler fusions and lowering variants`() {
        val module = generateContrivedBaseline().modules.single()
        val functions = module.functions.associateBy(CompilerBaselineFunction::name)
        val instructions = module.functions.flatMap(CompilerBaselineFunction::instructions).toSet()

        module.functions
            .filter { function -> function.name.substringBefore('.') in tagNamespaces }
            .forEach { function ->
                assertTrue(function.name in function.instructions, "${function.name} did not produce its named tag")
            }

        assertEquals(
            expectedConditionTags,
            instructions.filterTo(mutableSetOf()) { tag ->
                tag.startsWith("admin.jump_condition.")
            },
        )
        assertContainsShapes(instructions, "numeric.i32.add", 2)
        assertContainsShapes(instructions, "parametric.select", 3)
        assertContainsShapes(instructions, "memory.i32.load", 1)
        assertContainsShapes(instructions, "memory.i32.store", 2)
        assertContainsShapes(instructions, "memory.copy", 3)
        assertContainsShapes(instructions, "table.get", 1)
        assertContainsShapes(instructions, "table.copy", 3)
        assertContainsShapes(instructions, "aggregate.array.new", 2)
        assertContainsShapes(instructions, "aggregate.array.set", 2)
        assertContainsShapes(instructions, "aggregate.array.copy", 3)
        assertTrue("table.set.si" in instructions)
        assertTrue("table.set.ss" in instructions)
        assertTrue(controlTags.all(instructions::contains))
        assertTrue(aggregateFusionTags.all(instructions::contains))
        assertTrue("admin.pause_if" in instructions)

        assertEquals(
            listOf("numeric.i32.add.ss", "admin.end_function"),
            functions.getValue("lowering.producer_to_local").instructions,
        )
        assertFalse(
            functions.getValue("lowering.producer_to_local_tee").instructions.any { tag ->
                tag.startsWith("variable.local_set")
            },
        )
        assertEquals(
            listOf("admin.end_function"),
            functions.getValue("lowering.identity_local_write").instructions,
        )
        assertFalse(
            functions.getValue("lowering.no_op_reinterpret").instructions.any { tag ->
                "reinterpret" in tag
            },
        )
        assertEquals(
            "admin.copy_slot",
            functions.getValue("lowering.indirect_target_staging").instructions.first(),
        )

        val takenOnlyBranch = functions.getValue("lowering.taken_only_branch_copy").instructions
        assertTrue(takenOnlyBranch.indexOf("admin.end_function") < takenOnlyBranch.lastIndex)
        assertEquals("admin.jump", takenOnlyBranch.last())
    }

    private fun generateContrivedBaseline(): CompilerBaseline {
        val resourceLoader = CompilerBaselineResourceLoader(
            classLoader = checkNotNull(Thread.currentThread().contextClassLoader),
        )
        return ModuleCompilerBaselineGenerator(
            importResolver = EmptyCompilerBaselineImportResolver(),
            moduleDecoder = ChasmModuleDecoder(),
            moduleInstantiator = ChasmModuleInstantiator(),
            instructionCollectorFactory = DefaultProgramInstructionCollectorFactory(
                tagTranslator = CompilerInstructionTagTranslator(),
            ),
            runtimeConfig = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
        ).generate(
            CompilerBaselineFixture(
                name = "contrived",
                bytes = resourceLoader.read("compiler-baseline/contrived.wasm"),
            ),
        )
    }

    private fun assertContainsShapes(instructions: Set<String>, operation: String, arity: Int) {
        assertTrue(shapes(arity).all { shape -> "$operation.$shape" in instructions })
    }
}

private val tagNamespaces = setOf(
    "admin",
    "aggregate",
    "control",
    "memory",
    "numeric",
    "parametric",
    "reference",
    "table",
    "variable",
)

private val controlTags = setOf(
    "admin.jump_if.i",
    "admin.jump_if.s",
    "admin.jump_if_zero.i",
    "admin.jump_if_zero.s",
    "admin.jump_if_copy.i",
    "admin.jump_if_copy.s",
    "control.call.wasm.locals",
    "control.call.wasm.no_locals",
    "control.call_indirect.i",
    "control.call_indirect.s",
    "control.call_ref",
    "control.return_call.wasm",
    "control.return_call_indirect.i",
    "control.return_call_indirect.s",
    "control.return_call_ref",
)

private val aggregateFusionTags = setOf(
    "aggregate.ref_cast_struct_get",
    "aggregate.struct_get_struct_get",
    "aggregate.local_set_struct_get",
)

private val numericConditions = buildList {
    add("i32.eqz" to 1)
    add("i64.eqz" to 1)
    listOf("eq", "ne", "lt_s", "lt_u", "gt_s", "gt_u", "le_s", "le_u", "ge_s", "ge_u")
        .forEach { operation ->
            add("i32.$operation" to 2)
            add("i64.$operation" to 2)
        }
    listOf("eq", "ne", "lt", "gt", "le", "ge").forEach { operation ->
        add("f32.$operation" to 2)
        add("f64.$operation" to 2)
    }
}

private val expectedConditionTags: Set<String> = buildSet {
    numericConditions.forEach { (condition, arity) ->
        add("admin.jump_condition.$condition.${"s".repeat(arity)}.match")
        add("admin.jump_condition.$condition.${"i".repeat(arity)}.mismatch")
    }
    add("admin.jump_condition.i32.eq.is.match")
    add("admin.jump_condition.i32.eq.si.match")
    add("admin.jump_condition.i32.eqz.i.match")
}

private fun shapes(arity: Int): List<String> = if (arity == 0) {
    listOf("")
} else {
    shapes(arity - 1).flatMap { prefix -> listOf("${prefix}i", "${prefix}s") }
}
