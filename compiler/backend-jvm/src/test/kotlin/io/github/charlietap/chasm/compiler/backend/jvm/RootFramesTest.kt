package io.github.charlietap.chasm.compiler.backend.jvm

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OPEN_ROOTS
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RootFramesTest {

    private val module = decodeModule(fixture("call_effects"))
    private val methods: Map<String, MethodNode> = functionMethods()

    @Test
    fun `a function holding references opens a root frame when something it calls may collect`() {
        listOf("calls_chain", "calls_import", "calls_indirect", "calls_indirect_dynamic", "calls_ref", "calls_tail_alloc")
            .forEach { name -> assertEquals(true, opensRoots(name), name) }
    }

    @Test
    fun `a function holding references opens no root frame when nothing it runs collects`() {
        listOf("get", "calls_get", "touch_arrays", "touch_references", "even", "odd").forEach { name ->
            assertEquals(false, opensRoots(name), name)
        }
    }

    @Test
    fun `a tail call to a function that collects needs no root frame in the caller`() {
        listOf("tail_alloc", "tail_indirect", "tail_ref").forEach { name -> assertEquals(false, opensRoots(name), name) }
    }

    @Test
    fun `a reference written before a call is published and one written after the last call is not`() {
        val (aroundBefore, aroundAfter) = publicationsAroundCall("copies_around_call")
        val (afterBefore, afterAfter) = publicationsAroundCall("copies_after_call")

        assertEquals(afterBefore + 1, aroundBefore)
        assertEquals(0, aroundAfter)
        assertEquals(0, afterAfter)
    }

    private fun publicationsAroundCall(
        name: String,
    ): Pair<Int, Int> {
        val calls = checkNotNull(methods[name]).instructions.filterIsInstance<MethodInsnNode>()
        val bridge = calls.indexOfLast { call -> call.invokes(BRIDGE_CALL) }
        return calls.take(bridge).count { call -> call.invokes(FRAME) } to calls.drop(bridge + 1).count { call -> call.invokes(FRAME) }
    }

    private fun opensRoots(
        name: String,
    ): Boolean {
        val method = checkNotNull(methods[name]) { "no method for $name" }
        return method.instructions.any { instruction -> instruction is MethodInsnNode && instruction.invokes(OPEN_ROOTS) }
    }

    private fun MethodInsnNode.invokes(
        method: MethodRef,
    ): Boolean = owner == Type.getType(method.owner).internalName && name == method.name

    private fun functionMethods(): Map<String, MethodNode> {
        val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }
        val output = Generated()
        ModuleLowerer(module, code, JvmOptions(), output, ModuleDeclarationsFactory(module))
        val byIndex = output.classes.values.flatMap { bytes ->
            ClassNode().also { ClassReader(bytes).accept(it, 0) }.methods
        }.associateBy { method -> method.name }
        return module.exports.mapNotNull { export ->
            val function = export.descriptor as? Export.Descriptor.Function ?: return@mapNotNull null
            byIndex["f${function.functionIndex.idx}"]?.let { method -> export.name.name to method }
        }.toMap()
    }
}
