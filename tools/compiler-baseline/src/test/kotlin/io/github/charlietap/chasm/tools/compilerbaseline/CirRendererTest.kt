package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.cir.AbstractHeapType
import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.CirTag
import io.github.charlietap.chasm.cir.Frame
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.FusedOperand
import io.github.charlietap.chasm.cir.HeapType
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.InstructionIndex
import io.github.charlietap.chasm.cir.MemArg
import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NumericCondition
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.cir.TransferSource
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.fixture.cir.allInstructions
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CirRendererTest {

    private val renderer = CirRenderer()

    @Test
    fun `every instruction variant has a sample`() {
        val variants = leaves(Instruction::class)

        assertEquals(variants, allInstructions.map { instruction -> instruction::class }.toSet())
        assertEquals(variants.size, allInstructions.size)
    }

    @Test
    fun `every variant has its own tag`() {
        for (root in listOf(Instruction::class, FusedOperand::class, NumericCondition::class, TransferSource::class)) {
            val tags = leaves(root).map { variant -> variant.findAnnotation<CirTag>()?.name }

            assertTrue(tags.none { tag -> tag == null }, "${root.simpleName} has variants without a tag")
            assertEquals(tags.size, tags.toSet().size, "${root.simpleName} has variants sharing a tag")
        }
    }

    @Test
    fun `every instruction is rendered with its tag`() {
        val instructions = renderer.module(CirModule(functions = listOf(function(allInstructions))))
            .getValue("functions").jsonArray.single().jsonObject
            .getValue("instructions").jsonArray

        val tags = instructions.map { instruction -> instruction.jsonObject.getValue("tag").jsonPrimitive.content }

        assertEquals(allInstructions.map { instruction -> instruction::class.findAnnotation<CirTag>()!!.name }, tags)
    }

    @Test
    fun `instructions are written with snake case keys and readable operands`() {
        val module = CirModule(
            functions = listOf(
                function(
                    listOf(
                        MemoryInstruction.I32StoreIs(
                            value = 0,
                            addressSlot = 1,
                            memory = MemoryIndex(0),
                            memArg = MemArg(4),
                        ),
                        NumericInstruction.F32AddSi(leftSlot = 3, right = 1.0f, destinationSlot = 4),
                        AdminInstruction.JumpIfCondition(
                            condition = NumericCondition.I32LtU(FusedOperand.FrameSlot(0), FusedOperand.I32Const(100)),
                            target = InstructionIndex(0),
                        ),
                        ReferenceInstruction.RefTestS(referenceSlot = 2, destinationSlot = 5, typeTest = funcRef),
                    ),
                ),
            ),
        )

        val expected = """
            {"schema_version":2,"functions":[{"index":0,"type":0,"frame":{"frame_slots":6,"activation_header_slot":0},"instructions":[
            {"tag":"memory.i32.store.is","value":0,"address_slot":1,"memory":0,"mem_arg":4},
            {"tag":"numeric.f32.add.si","left_slot":3,"right":"0x3f800000","destination_slot":4},
            {"tag":"admin.jump_if_condition","condition":{"kind":"i32.lt_u","left":{"kind":"frame_slot","offset":0},"right":{"kind":"i32_const","const":100}},"target":0},
            {"tag":"reference.ref_test","reference_slot":2,"destination_slot":5,"type_test":"ref null func"}
            ],"checkpoints":[]}]}
        """.trimIndent().replace("\n", "")

        assertEquals(expected, render(module))
    }

    @Test
    fun `floats are written by their exact bits`() {
        val module = CirModule(
            functions = listOf(
                function(
                    listOf(
                        NumericInstruction.F32AddSi(leftSlot = 0, right = Float.fromBits(0xffc00001.toInt()), destinationSlot = 0),
                        NumericInstruction.F32AddSi(leftSlot = 0, right = Float.fromBits(0x80000000.toInt()), destinationSlot = 1),
                        NumericInstruction.F64AddSi(leftSlot = 0, right = Double.fromBits(-0x7ffffffffffff), destinationSlot = 2),
                    ),
                ),
            ),
        )

        val operands = Json.parseToJsonElement(render(module)).jsonObject
            .getValue("functions").jsonArray.single().jsonObject
            .getValue("instructions").jsonArray
            .map { instruction -> instruction.jsonObject.getValue("right").jsonPrimitive.content }

        assertEquals(listOf("0xffc00001", "0x80000000", "0xfff8000000000001"), operands)
    }

    private fun render(module: CirModule): String = Json.encodeToString(JsonObject.serializer(), renderer.module(module))

    private fun leaves(root: KClass<*>): Set<KClass<*>> = root.sealedSubclasses.flatMapTo(mutableSetOf()) { subclass ->
        if (subclass.isSealed) leaves(subclass) else setOf(subclass)
    }

    private fun function(
        instructions: List<Instruction>,
    ) = CirFunction(
        index = FunctionIndex(0),
        type = TypeIndex(0),
        frame = Frame(frameSlots = 6, activationHeaderSlot = 0),
        instructions = instructions.toTypedArray(),
    )

    private companion object {
        val funcRef = ReferenceType(nullable = true, heapType = HeapType.Abstract(AbstractHeapType.Func))
    }
}
