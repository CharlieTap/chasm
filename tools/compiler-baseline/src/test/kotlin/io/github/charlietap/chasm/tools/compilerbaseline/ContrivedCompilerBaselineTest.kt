package io.github.charlietap.chasm.tools.compilerbaseline

import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ContrivedCompilerBaselineTest {

    private val bytes = CompilerBaselineResourceLoader(
        classLoader = checkNotNull(Thread.currentThread().contextClassLoader),
    ).read("compiler-baseline/contrived.wasm")

    @Test
    fun `the frontend covers compiler fusions and lowering variants`() {
        val functions = cirTags()
        val instructions = functions.values.flatten().toSet()

        val loweredNamedFunctions = functions.keys
            .filter { name -> name.substringBefore('.') in tagNamespaces && name !in linkedNamedFunctions }
            .filterNot { name -> name in functions.getValue(name) }
            .toSet()
        assertEquals(expectedLoweredNamedFunctions, loweredNamedFunctions)

        assertEquals(
            expectedConditionTags,
            instructions.filterTo(mutableSetOf()) { tag ->
                tag.startsWith("admin.jump_condition.") && tag.endsWith(".match")
            },
        )
        assertTrue(setOf("is", "si", "ss").all { shape -> "numeric.i32.add.$shape" in instructions })
        assertTrue(setOf("sii", "sis", "ssi", "sss").all { shape -> "parametric.select.$shape" in instructions })
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

        assertEquals(listOf("numeric.i32.add.ss", "control.return"), functions.getValue("lowering.producer_to_local"))
        assertFalse(functions.getValue("lowering.producer_to_local_tee").any { tag -> tag.startsWith("variable.local_set") })
        assertEquals(listOf("control.return"), functions.getValue("lowering.identity_local_write"))
        assertFalse(functions.getValue("lowering.no_op_reinterpret").any { tag -> "reinterpret" in tag })
        assertEquals("control.call_indirect.s", functions.getValue("lowering.indirect_target_staging").first())

        val takenOnlyBranch = functions.getValue("lowering.taken_only_branch_copy")
        assertTrue(takenOnlyBranch.indexOf("control.return") < takenOnlyBranch.lastIndex)
        assertEquals("admin.jump_copies", takenOnlyBranch.last())
    }

    @Test
    fun `linking chooses call dispatchers by callee and drops checkpoints in an unmetered store`() {
        val module = ModuleCompilerBaselineGenerator(
            importResolver = EmptyCompilerBaselineImportResolver(),
            moduleDecoder = ChasmModuleDecoder(),
            moduleInstantiator = ChasmModuleInstantiator(),
            runtimeConfig = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
        ).generate(CompilerBaselineFixture(name = "contrived", bytes = bytes)).modules.single()
        val functions = module.functions.associateBy(CompilerBaselineFunction::name)

        val withLocals = functions.getValue("control.call.wasm.locals").instructions.first()
        assertEquals("linkedWasmCallDispatcher", withLocals.dispatcher)
        assertEquals("func[1]", withLocals.fields?.get("entry_ip")?.jsonPrimitive?.content)
        val withoutLocals = functions.getValue("control.call.wasm.no_locals").instructions.first()
        assertTrue(withoutLocals.dispatcher.startsWith("LinkedWasmCallWithoutLocals"))
        assertEquals("func[0]", withoutLocals.fields?.get("entry_ip")?.jsonPrimitive?.content)
        assertEquals("ReturnWasmCallDispatcher", functions.getValue("control.return_call.wasm").dispatchers.single())
        assertEquals("ReturnWasmCallDispatcher", functions.getValue("control.return_call_ref").dispatchers.single())

        val dispatchers = module.functions.flatMap(CompilerBaselineFunction::dispatchers).toSet()
        assertFalse(dispatchers.any { dispatcher -> "Check" in dispatcher })
        assertTrue(module.functions.all { function -> function.unlinkedCheckpoints == listOf(0) })
        assertTrue(module.functions.all { function -> function.instructions.first().position == 1 })
    }

    @Test
    fun `linked dispatchers capture the values the frontend gave them`() {
        val linked = linkedContrived().functions.associateBy(CompilerBaselineFunction::name)
        val cir = cirFunctions()

        for (name in listOf("control.call.wasm.locals", "control.call.wasm.no_locals")) {
            val call = linked.getValue(name).instructions.first()
            val cirCall = cir.getValue(name).instruction(call.position)
            assertEquals(cirCall.int("call_frame_offset"), call.int("call_frame_offset"), name)
            assertEquals(cirCall.int("call_frame_offset"), call.fields?.getValue("activation_header")?.jsonObject?.int("caller_frame_delta"))
        }
        assertNotNull(linked.getValue("control.call.wasm.locals").instructions.first().variant)

        val throwInstruction = linked.getValue("exceptions.catch_payload").instructions.single { it.dispatcher == "ThrowDispatcher" }
        val cirThrow = cir.getValue("exceptions.catch_payload").instruction(throwInstruction.position)
        assertEquals(cirThrow.int("first_payload_slot"), throwInstruction.int("first_payload_slot"))
        assertEquals("tag[0]", throwInstruction.fields?.getValue("tag_address")?.jsonPrimitive?.content)

        val refFunc = linked.getValue("reference.ref_func").instructions.first()
        assertEquals("RefFuncSDispatcher", refFunc.dispatcher)
        assertEquals("func[2]", refFunc.fields?.getValue("reference")?.jsonPrimitive?.content)
        val refNull = linked.getValue("reference.ref_null").instructions.first()
        assertEquals("RefNullSDispatcher", refNull.dispatcher)
        assertEquals("null", refNull.fields?.getValue("reference")?.jsonObject?.getValue("kind")?.jsonPrimitive?.content)
    }

    private fun linkedContrived(): CompilerBaselineModule = ModuleCompilerBaselineGenerator(
        importResolver = EmptyCompilerBaselineImportResolver(),
        moduleDecoder = ChasmModuleDecoder(),
        moduleInstantiator = ChasmModuleInstantiator(),
        runtimeConfig = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
    ).generate(CompilerBaselineFixture(name = "contrived", bytes = bytes)).modules.single()

    private fun cirTags(): Map<String, List<String>> = cirFunctions().mapValues { (_, function) ->
        function.getValue("instructions").jsonArray
            .map { instruction -> tag(instruction.jsonObject) }
            .filter { tag -> tag != "admin.checkpoint" }
    }

    private fun cirFunctions(): Map<String, JsonObject> {
        val module = ChasmModuleDecoder().decode(ModuleConfig(decodeNameSection = true), bytes).unwrap()
        val names = functionNames(module)
        val cir = CirRenderer().module(ModuleCompiler(module).unwrap())
        return cir.getValue("functions").jsonArray.associate { element ->
            val function = element.jsonObject
            names.getValue(function.getValue("index").jsonPrimitive.int) to function
        }
    }

    private fun JsonObject.instruction(
        position: Int,
    ): JsonObject = getValue("instructions").jsonArray[position].jsonObject

    private fun JsonObject.int(
        name: String,
    ): Int = getValue(name).jsonPrimitive.int

    private fun CompilerBaselineInstruction.int(
        name: String,
    ): Int? = fields?.get(name)?.jsonPrimitive?.int

    private fun tag(
        instruction: JsonObject,
    ): String {
        val tag = instruction.getValue("tag").jsonPrimitive.content
        if (tag != "admin.jump_if_condition" && tag != "admin.jump_if_condition_mismatch") return tag
        val condition = instruction.getValue("condition").jsonObject
        val shape = condition.filterKeys { key -> key != "kind" }.values.joinToString("") { operand ->
            if (operand.jsonObject.getValue("kind").jsonPrimitive.content == "frame_slot") "s" else "i"
        }
        val polarity = if (tag.endsWith("_mismatch")) "mismatch" else "match"
        return "admin.jump_condition.${condition.getValue("kind").jsonPrimitive.content}.$shape.$polarity"
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

private val linkedNamedFunctions = setOf(
    "control.call.wasm.locals",
    "control.call.wasm.no_locals",
    "control.return_call.wasm",
)

private val controlTags = setOf(
    "admin.jump_if.s",
    "admin.jump_if_zero.s",
    "control.call",
    "control.call_indirect.i",
    "control.call_indirect.s",
    "control.return_call",
    "control.return_call_indirect.i",
    "control.return_call_indirect.s",
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
    }
    add("admin.jump_condition.i32.eq.is.match")
    add("admin.jump_condition.i32.eq.si.match")
}

private val expectedLoweredNamedFunctions: Set<String> = buildSet {
    add("numeric.i32.add.ii")
    addAll(
        listOf(
            "parametric.select.iii",
            "parametric.select.iis",
            "parametric.select.isi",
            "parametric.select.iss",
            "admin.jump_if.i",
            "admin.jump_if_zero.i",
            "admin.jump_if_copy.i",
            "admin.jump_if_copy.s",
            "control.call_ref",
            "control.return_call_ref",
        ),
    )
    numericConditions.forEach { (condition, arity) ->
        add("admin.jump_condition.$condition.${"i".repeat(arity)}.mismatch")
    }
    add("admin.jump_condition.i32.eqz.i.match")
}

private fun shapes(arity: Int): List<String> = if (arity == 0) {
    listOf("")
} else {
    shapes(arity - 1).flatMap { prefix -> listOf("${prefix}i", "${prefix}s") }
}

internal val CompilerBaselineFunction.dispatchers: List<String>
    get() = instructions.map(CompilerBaselineInstruction::dispatcher)
