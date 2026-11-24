@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.compiler.backend.jvm

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.backend.abi.ALLOCATION_REMAINING_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_OUT_OF_BOUNDS
import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_RTT_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.NULL_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_FRAME
import io.github.charlietap.chasm.compiler.backend.abi.REFILL_ALLOCATION
import io.github.charlietap.chasm.compiler.backend.abi.RTT_DEPTH_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.RTT_SUPERTYPES_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.analysis.DataflowSlotKindAnalyzer
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.SlotKindFlow
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeSlotKinds
import io.github.charlietap.chasm.compiler.backend.analysis.form.InstructionForms
import io.github.charlietap.chasm.compiler.backend.analysis.frameFacts
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.compiler.backend.analysis.instructionTable
import io.github.charlietap.chasm.compiler.backend.jvm.declarations.DeclarationCallGenerator
import io.github.charlietap.chasm.compiler.backend.jvm.declarations.DeclarationsLowerer
import io.github.charlietap.chasm.compiler.backend.jvm.lower.Locals
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.compiled.CHUNK_METHOD_SEPARATOR
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TypeInsnNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.BasicVerifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ManagedLoweringTest {

    private val module = decodeModule(fixture("managed_lowering"))
    private val plans = StoragePlans(module.definedTypes, STORAGE_FAMILY)
    private val functions = lowerExports(module, JvmOptions())

    @Test
    fun `struct new charges the meter before allocating`() {

        val instructions = function("new_circle").instructions.toList()
        val circle = internalName(plans.struct(CIRCLE).className)
        val allocation = instructions.indexOfFirst { instruction -> instruction is TypeInsnNode && instruction.opcode == Opcodes.NEW && instruction.desc == circle }
        val charge = instructions.indexOfFirst { instruction ->
            instruction is FieldInsnNode && instruction.opcode == Opcodes.PUTFIELD && instruction.name == ALLOCATION_REMAINING_FIELD
        }
        val refill = instructions.indexOfFirst { instruction -> instruction.invokes(REFILL_ALLOCATION) }

        assertTrue(allocation >= 0)
        assertTrue(charge in 0 until allocation)
        assertTrue(refill in 0 until allocation)
    }

    @Test
    fun `struct get traps on null before the cast`() {

        val method = function("get_shape")
        val instructions = method.instructions.toList()
        val nullCheck = instructions.indexOfFirst { instruction -> instruction.opcode == Opcodes.IFNULL }
        val cast = instructions.indexOfFirst { instruction -> instruction.opcode == Opcodes.CHECKCAST }

        assertTrue(nullCheck in 0 until cast)
        assertTrue(trapsWith(method, instructions[nullCheck] as JumpInsnNode, NULL_REFERENCE))
    }

    @Test
    fun `struct get of an inherited field casts to the type's own class`() {

        val circle = plans.struct(CIRCLE)
        val lane = circle.fields[0].lanes.single()
        val own = internalName(circle.className)
        val declaring = internalName(STORAGE_FAMILY.storageClass(circle.className).declaringClass(lane).name)
        val method = function("get_inherited")
        val reads = method.instructions.filterIsInstance<FieldInsnNode>().filter { instruction -> instruction.opcode == Opcodes.GETFIELD }

        assertEquals(listOf(own), casts(method))
        assertEquals(listOf(declaring), reads.map { read -> read.owner })
        assertNotEquals(own, declaring)
    }

    @Test
    fun `a parameter slot with a number and a reference home initialises both`() {

        val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
        val function = code.functions.single { function -> function.index.index == exportIndex(module, "parameter_homes") }
        val forms = InstructionForms(function, ModuleIndexSpaces(module))
        val frame = frameFacts(forms)
        val table = instructionTable(forms, objectReferences = true)
        val flow = DataflowSlotKindAnalyzer().analyze(function, table, frame, BooleanArray(frame.frameSlots) { true })
        val objectHome = SlotKindFlow.homeBit(SlotKindFlow.OBJECT)
        val mask = flow.homeMask(0)

        assertTrue(mask and objectHome != 0 && mask and objectHome.inv() != 0, "parameter 0 has homes $mask")
        assertTrue(functions.containsKey("parameter_homes"))
    }

    @Test
    fun `ref test against a final type compares the RTT only`() {

        val final = fieldsRead(function("is_circle"))
        val open = fieldsRead(function("is_shape"))

        assertTrue(COMPILED_RTT_FIELD in final)
        assertTrue(RTT_SUPERTYPES_FIELD !in final)
        assertTrue(RTT_DEPTH_FIELD !in final)
        assertTrue(function("is_circle").instructions.any { instruction -> instruction.opcode == Opcodes.IF_ACMPEQ })
        assertTrue(RTT_SUPERTYPES_FIELD in open)
    }

    @Test
    fun `array get traps outside the bounds`() {

        val method = function("byte_at")
        val instructions = method.instructions.toList()
        val below = instructions.single { instruction -> instruction.opcode == Opcodes.IFLT } as JumpInsnNode
        val above = instructions.single { instruction -> instruction.opcode == Opcodes.IF_ICMPGE } as JumpInsnNode
        val load = instructions.indexOfFirst { instruction -> instruction.opcode == Opcodes.BALOAD }

        assertTrue(trapsWith(method, below, ARRAY_OUT_OF_BOUNDS))
        assertTrue(trapsWith(method, above, ARRAY_OUT_OF_BOUNDS))
        assertTrue(instructions.indexOf(below) < load)
        assertTrue(instructions.indexOf(above) < load)
    }

    @Test
    fun `a slot holding a number then a reference gets two locals`() {

        val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
        val function = code.functions.single { function -> function.index.index == exportIndex(module, "mixed") }
        val forms = InstructionForms(function, ModuleIndexSpaces(module))
        val frame = frameFacts(forms)
        val table = instructionTable(forms, objectReferences = true)
        val flow = DataflowSlotKindAnalyzer().analyze(function, table, frame, BooleanArray(frame.frameSlots) { true })
        val kinds = analyzeSlotKinds(forms, frame.type.params.types, frame.frameSlots, frame.localTypes, frame.initialValues)
        val locals = Locals(BINDING, emptyList(), kinds, flow, frame.frameSlots)
        val objectHome = SlotKindFlow.homeBit(SlotKindFlow.OBJECT)

        val mixed = (0 until frame.frameSlots).filter { slot ->
            val mask = flow.homeMask(slot)
            mask and objectHome != 0 && mask and objectHome.inv() != 0
        }

        assertTrue(mixed.isNotEmpty())
        mixed.forEach { slot ->
            assertTrue(locals.holdsNumber(slot))
            assertTrue(locals.holdsObject(slot))
            assertNotEquals(locals.slot(slot), locals.objectSlot(slot))
            assertEquals(OBJECT, frameType(locals, locals.objectSlot(slot)))
        }
    }

    private fun frameType(
        locals: Locals,
        local: Int,
    ): Any {

        var next = 0
        for (type in locals.frameTypes) {
            if (next == local) {
                return type
            }
            next += if (type == Opcodes.LONG || type == Opcodes.DOUBLE) 2 else 1
        }

        error("no local $local")
    }

    @Test
    fun `a split function carries references in an object frame`() {

        val methods = loweredMethods(module, JvmOptions(splitInstructions = 1)).getValue("carries")
        val chunks = methods.filter { method -> CHUNK_METHOD_SEPARATOR in method.name }
        val dispatcher = methods.single { method -> CHUNK_METHOD_SEPARATOR !in method.name }

        assertTrue(chunks.size > 1)
        chunks.forEach { chunk -> assertTrue(chunk.desc.endsWith("I[L$OBJECT;)I"), chunk.desc) }
        assertTrue(dispatcher.instructions.any { instruction -> instruction is TypeInsnNode && instruction.opcode == Opcodes.ANEWARRAY && instruction.desc == OBJECT })
        assertTrue(dispatcher.instructions.any { instruction -> instruction.opcode == Opcodes.AASTORE })
        assertTrue(chunks.any { chunk -> chunk.instructions.any { instruction -> instruction.opcode == Opcodes.AALOAD } })
    }

    @Test
    fun `no generated method references the root frame helpers`() {

        val effects = decodeModule(fixture("call_effects"))
        val whole = lowerExports(effects, JvmOptions())
        val split = loweredMethods(effects, JvmOptions(splitInstructions = 2)).values.flatten()
        (whole.values + split).forEach { method ->
            method.instructions.filterIsInstance<MethodInsnNode>().forEach { call ->
                assertTrue(call.owner != COMPILED_CALLS || call.name !in ROOT_HELPERS, method.name)
                assertTrue(call.owner != COMPILED_GC, method.name)
            }
        }
        whole.values.forEach { method ->
            assertTrue(method.instructions.none { instruction -> instruction.invokes(FRAME) || instruction.invokes(OPEN_FRAME) }, method.name)
        }
    }

    private fun function(name: String): MethodNode {
        return checkNotNull(functions[name]) { "no method for $name" }
    }

    private fun casts(method: MethodNode): List<String> {
        return method.instructions.filterIsInstance<TypeInsnNode>().filter { instruction -> instruction.opcode == Opcodes.CHECKCAST }.map { cast -> cast.desc }
    }

    private fun fieldsRead(method: MethodNode): List<String> {
        return method.instructions.filterIsInstance<FieldInsnNode>().filter { instruction -> instruction.opcode == Opcodes.GETFIELD }.map { field -> field.name }
    }

    private fun trapsWith(
        method: MethodNode,
        jump: JumpInsnNode,
        trap: MethodRef,
    ): Boolean {
        val target = method.instructions.indexOf(jump.label)
        val next = method.instructions.toList().drop(target).firstOrNull { instruction -> instruction.opcode >= 0 }
        return next?.invokes(trap) == true
    }

    private fun AbstractInsnNode.invokes(method: MethodRef): Boolean {
        return this is MethodInsnNode && owner == Type.getType(method.owner).internalName && name == method.name
    }

    private fun lowerExports(
        module: Module,
        options: JvmOptions,
    ): Map<String, MethodNode> {
        return loweredMethods(module, options).mapValues { (_, methods) -> methods.single() }
    }

    private fun loweredMethods(
        module: Module,
        options: JvmOptions,
    ): Map<String, List<MethodNode>> {

        val code = ModuleCompiler(module).getOrThrow { error -> IllegalStateException(error.toString()) }
        val output = Generated()
        ModuleLowerer(
            module = module,
            code = code,
            options = options,
            output = output,
            declarations = ModuleDeclarationsFactory(module),
            parallelism = 1,
            functionLowerer = ::FunctionLowerer,
            signatureLowerer = ::SignatureLowerer,
            entryLowerer = ::EntryLowerer,
            constantsLowerer = ::ConstantsLowerer,
            bindingLowerer = ::BindingLowerer,
            declarationCallGenerator = ::DeclarationCallGenerator,
            declarationsLowerer = ::DeclarationsLowerer,
            moduleClassLowerer = ::ModuleClassLowerer,
            structsLowerer = ::ModuleStructsLowerer,
        )

        val classes = output.classes.filterKeys { name -> CODE_CLASS in name || CHUNKS_CLASS in name }.values.map { bytes ->
            ClassNode().also { node -> ClassReader(bytes).accept(node, 0) }
        }
        classes.forEach { owner ->
            owner.methods.forEach { method -> Analyzer(BasicVerifier()).analyze(owner.name, method) }
        }
        GeneratedClassLoader(output).verifyEveryClass()

        val methods = classes.flatMap { owner -> owner.methods }
        return module.exports.mapNotNull { export ->
            val function = export.descriptor as? Export.Descriptor.Function ?: return@mapNotNull null
            val name = "f${function.functionIndex.idx}"
            val lowered = methods.filter { method -> method.name == name || method.name.startsWith("$name$CHUNK_METHOD_SEPARATOR") }
            if (lowered.isEmpty()) null else export.name.name to lowered
        }.toMap()
    }

    private fun exportIndex(
        module: Module,
        name: String,
    ): Int {
        val export = module.exports.single { export -> export.name.name == name }
        return (export.descriptor as Export.Descriptor.Function).functionIndex.idx.toInt()
    }

    private companion object {
        const val SHAPE = 1
        const val CIRCLE = 2
        const val BINDING = "Lcom/example/Mod\$Binding;"
        const val OBJECT = "java/lang/Object"
        const val COMPILED_GC = "io/github/charlietap/chasm/runtime/compiled/CompiledGc"
        const val COMPILED_CALLS = "io/github/charlietap/chasm/runtime/compiled/CompiledCalls"
        val ROOT_HELPERS = setOf("openRoots", "clearRoots")
        const val CODE_CLASS = "\$Code"
        const val CHUNKS_CLASS = "\$Chunks"
    }
}
