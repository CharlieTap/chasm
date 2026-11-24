package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.GlobalIndex
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.CHECK_ARRAY_LENGTH
import io.github.charlietap.chasm.compiler.backend.abi.CODE_INSTALLATION
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_GET
import io.github.charlietap.chasm.compiler.backend.abi.I31_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_GLOBAL
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_GLOBAL_VALUE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_SEGMENT
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_STORAGE_ELEMENTS
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TABLE
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.INITIALIZE_CONSTANTS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.constantsClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.binding.FIELD_PLACEHOLDER
import io.github.charlietap.chasm.compiler.backend.jvm.binding.PART_PLACEHOLDER
import io.github.charlietap.chasm.compiler.backend.jvm.binding.PENDING_OWNER
import io.github.charlietap.chasm.compiler.backend.jvm.binding.bindingDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.gc.OBJECT_CLASS
import io.github.charlietap.chasm.compiler.backend.jvm.gc.StorageElement
import io.github.charlietap.chasm.compiler.backend.jvm.gc.allocateStruct
import io.github.charlietap.chasm.compiler.backend.jvm.gc.putField
import io.github.charlietap.chasm.compiler.backend.jvm.gc.storageElement
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodNode

internal typealias ConstantsLowerer = (ModuleLowering, Module, JvmOutput) -> Unit

internal fun ConstantsLowerer(
    lowering: ModuleLowering,
    module: Module,
    output: JvmOutput,
) {

    val expressions = lowering.constants
    if (!expressions.needed) {
        return
    }

    val owner = internalName(constantsClassDescriptor(lowering.entryPath))
    val writer = ConstantsWriter(lowering, owner)
    val importedGlobals = module.imports.count { import -> import.descriptor is Import.Descriptor.Global }
    val importedTables = module.imports.count { import -> import.descriptor is Import.Descriptor.Table }

    expressions.globals.forEach { defined ->
        writer.global(importedGlobals + defined, module.globals[defined].initExpression)
    }
    expressions.tables.forEach { defined ->
        writer.table(importedTables + defined, module.tables[defined].initExpression)
    }
    expressions.elements.forEach { index ->
        writer.element(index, module.elementSegments[index].initExpressions)
    }

    output.classFile(owner, writer.write())
}

private class ConstantsWriter(
    private val lowering: ModuleLowering,
    private val owner: String,
) {

    private val methods = ArrayList<MethodNode>()
    private val initializers = ArrayList<String>()
    private val resources = ArrayList<Resource>()
    private val resourceIds = HashMap<Resource, Int>()
    private val initializerDescriptor = "($CODE_INSTALLATION${lowering.bindingType})V"
    private val chunkDescriptor = "($CODE_INSTALLATION${lowering.bindingType}$OBJECT_ARRAY)V"

    fun global(
        global: Int,
        expression: Expression,
    ) {
        initializer("global$global") {
            Evaluator(this).evaluate(expression)
            pushResource(Resource.ReferenceGlobal(global))
            visitVarInsn(Opcodes.ALOAD, FIRST_SLOT)
            visitFieldInsn(Opcodes.PUTFIELD, internalName(REFERENCE_GLOBAL), REFERENCE_GLOBAL_VALUE, OBJECT_TYPE)
        }
    }

    fun table(
        table: Int,
        expression: Expression,
    ) {
        initializer("table$table") {
            Evaluator(this).evaluate(expression)
            pushResource(Resource.ReferenceTable(table))
            visitFieldInsn(Opcodes.GETFIELD, internalName(REFERENCE_TABLE), REFERENCE_STORAGE_ELEMENTS, OBJECT_ARRAY)
            visitVarInsn(Opcodes.ALOAD, FIRST_SLOT)
            invoke(StorageElement.REFERENCE.wholeFill, Opcodes.INVOKESTATIC)
        }
    }

    fun element(
        element: Int,
        expressions: List<Expression>,
    ) {

        val chunks = expressions.chunked(EXPRESSIONS_PER_METHOD).mapIndexed { chunk, members ->
            val name = "element${element}_$chunk"
            method(name, chunkDescriptor) {
                members.forEachIndexed { offset, expression ->
                    Evaluator(this).evaluate(expression)
                    visitVarInsn(Opcodes.ALOAD, SEGMENT_LOCAL)
                    visitLdcInsn(chunk * EXPRESSIONS_PER_METHOD + offset)
                    visitVarInsn(Opcodes.ALOAD, FIRST_SLOT)
                    visitInsn(Opcodes.AASTORE)
                }
            }
            name
        }

        initializer("element$element") {
            visitLdcInsn(expressions.size)
            visitTypeInsn(Opcodes.ANEWARRAY, OBJECT_CLASS)
            visitVarInsn(Opcodes.ASTORE, SEGMENT_LOCAL)
            chunks.forEach { chunk ->
                visitVarInsn(Opcodes.ALOAD, INSTALLATION_LOCAL)
                visitVarInsn(Opcodes.ALOAD, BINDING_LOCAL)
                visitVarInsn(Opcodes.ALOAD, SEGMENT_LOCAL)
                visitMethodInsn(Opcodes.INVOKESTATIC, owner, chunk, chunkDescriptor, false)
            }
            pushResource(Resource.ReferenceSegment(element))
            visitVarInsn(Opcodes.ALOAD, SEGMENT_LOCAL)
            visitFieldInsn(Opcodes.PUTFIELD, internalName(REFERENCE_SEGMENT), REFERENCE_STORAGE_ELEMENTS, OBJECT_ARRAY)
        }
    }

    fun write(): ByteArray {

        val groups = initializers.chunked(EXPRESSIONS_PER_METHOD).mapIndexed { group, members ->
            val name = "initialize$group"
            method(name, initializerDescriptor) {
                members.forEach { initializer -> invokeInitializer(initializer) }
            }
            name
        }
        method(INITIALIZE_CONSTANTS, initializerDescriptor, Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC) {
            groups.forEach { group -> invokeInitializer(group) }
        }

        lowering.layout.resolve(methods, resources, lowering.bindingType)

        val writer = supportClassWriter()
        writer.visit(CLASS_FILE_VERSION, Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER, owner, null, ModuleLowering.OBJECT, null)
        methods.forEach { method -> method.accept(writer) }
        writer.visitEnd()

        return writer.toByteArray()
    }

    private inline fun initializer(
        name: String,
        body: MethodNode.() -> Unit,
    ) {
        method(name, initializerDescriptor, body = body)
        initializers += name
    }

    private inline fun method(
        name: String,
        descriptor: String,
        access: Int = Opcodes.ACC_PRIVATE or Opcodes.ACC_STATIC,
        body: MethodNode.() -> Unit,
    ) {

        val method = MethodNode(access, name, descriptor, null, null)
        method.visitCode()
        method.body()
        method.visitInsn(Opcodes.RETURN)
        method.visitMaxs(0, 0)
        method.visitEnd()

        methods += method
    }

    private fun MethodNode.invokeInitializer(name: String) {
        visitVarInsn(Opcodes.ALOAD, INSTALLATION_LOCAL)
        visitVarInsn(Opcodes.ALOAD, BINDING_LOCAL)
        visitMethodInsn(Opcodes.INVOKESTATIC, owner, name, initializerDescriptor, false)
    }

    fun MethodVisitor.pushResource(resource: Resource) {

        val id = resourceIds.getOrPut(resource) { resources.size.also { resources += resource } }

        visitVarInsn(Opcodes.ALOAD, BINDING_LOCAL)
        visitFieldInsn(Opcodes.GETFIELD, PENDING_OWNER, "$PART_PLACEHOLDER$id", OBJECT_TYPE)
        visitFieldInsn(Opcodes.GETFIELD, PENDING_OWNER, "$FIELD_PLACEHOLDER$id", bindingDescriptor(resource))
    }

    private inner class Evaluator(
        private val method: MethodNode,
    ) {

        private val stackKinds = ArrayList<StackValueKind>()

        fun evaluate(expression: Expression) {
            expression.instructions.forEach(::instruction)
        }

        private fun instruction(instruction: Instruction) {
            when (instruction) {
                is NumericInstruction.I32Const -> push(StackValueKind.NARROW) { method.visitLdcInsn(instruction.value) }
                is NumericInstruction.I64Const -> push(StackValueKind.WIDE) { method.visitLdcInsn(instruction.value) }
                is NumericInstruction.F32Const -> push(StackValueKind.NARROW) { method.visitLdcInsn(instruction.bits) }
                is NumericInstruction.F64Const -> push(StackValueKind.WIDE) { method.visitLdcInsn(instruction.bits) }
                NumericInstruction.I32Add -> binary(Opcodes.IADD)
                NumericInstruction.I32Sub -> binary(Opcodes.ISUB)
                NumericInstruction.I32Mul -> binary(Opcodes.IMUL)
                NumericInstruction.I64Add -> binary(Opcodes.LADD)
                NumericInstruction.I64Sub -> binary(Opcodes.LSUB)
                NumericInstruction.I64Mul -> binary(Opcodes.LMUL)
                is ReferenceInstruction.RefNull -> push(StackValueKind.OBJECT) { method.visitInsn(Opcodes.ACONST_NULL) }
                is ReferenceInstruction.RefFunc -> push(StackValueKind.OBJECT) {
                    method.pushResource(Resource.FunctionObject(instruction.funcIdx.idx.toInt()))
                }
                is VariableInstruction.GlobalGet -> globalGet(instruction.globalIdx.idx.toInt())
                is AggregateInstruction.StructNew -> structNew(instruction.typeIndex.idx.toInt(), withFields = true)
                is AggregateInstruction.StructNewDefault -> structNew(instruction.typeIndex.idx.toInt(), withFields = false)
                is AggregateInstruction.ArrayNew -> arrayNew(instruction.typeIndex.idx.toInt())
                is AggregateInstruction.ArrayNewDefault -> arrayNewDefault(instruction.typeIndex.idx.toInt())
                is AggregateInstruction.ArrayNewFixed -> arrayNewFixed(instruction.typeIndex.idx.toInt(), instruction.size.toInt())
                AggregateInstruction.RefI31 -> refI31()
                AggregateInstruction.AnyConvertExtern, AggregateInstruction.ExternConvertAny -> Unit
                else -> error("$instruction is not a managed constant expression")
            }
        }

        private fun globalGet(global: Int) {

            val type = lowering.declarations.globalType(GlobalIndex(global))
            if (type is ValueType.Reference) {
                push(StackValueKind.OBJECT) {
                    method.pushResource(Resource.ReferenceGlobal(global))
                    method.visitFieldInsn(Opcodes.GETFIELD, internalName(REFERENCE_GLOBAL), REFERENCE_GLOBAL_VALUE, OBJECT_TYPE)
                }
                return
            }

            val number = (type as ValueType.Number).numberType
            val kind = if (number == NumberType.I32 || number == NumberType.F32) StackValueKind.NARROW else StackValueKind.WIDE
            push(kind) {
                method.pushResource(Resource.Global(global))
                method.invoke(GLOBAL_GET, Opcodes.INVOKEVIRTUAL)
                if (kind == StackValueKind.NARROW) {
                    method.visitInsn(Opcodes.L2I)
                }
            }
        }

        private fun structNew(
            type: Int,
            withFields: Boolean,
        ) {

            val storage = lowering.storagePlans.struct(type)
            val first = stackKinds.size - if (withFields) storage.fields.size else 0

            method.allocateStruct(storage) {
                pushResource(Resource.Rtt(type))
            }
            if (withFields) {
                storage.fields.forEachIndexed { index, field ->
                    val valueSlot = slot(first + index)
                    val load = when {
                        field.lanes.size == 2 -> Opcodes.LLOAD
                        field.lanes.single().kind == LaneKind.REFERENCE -> Opcodes.ALOAD
                        else -> Opcodes.ILOAD
                    }
                    method.visitInsn(Opcodes.DUP)
                    method.putField(storage, field, valueSlot) {
                        visitVarInsn(load, valueSlot)
                    }
                }
            }

            replace(first)
        }

        private fun arrayNew(type: Int) {

            val element = arrayElement(type)
            val first = stackKinds.size - 2

            newElements(type, element, slot(first + 1))
            method.visitInsn(Opcodes.DUP)
            pushElement(element, slot(first))
            method.invoke(element.wholeFill, Opcodes.INVOKESTATIC)

            wrapArray(type, element, first)
        }

        private fun arrayNewDefault(type: Int) {

            val element = arrayElement(type)
            val first = stackKinds.size - 1

            newElements(type, element, slot(first))
            wrapArray(type, element, first)
        }

        private fun arrayNewFixed(
            type: Int,
            size: Int,
        ) {

            val element = arrayElement(type)
            val first = stackKinds.size - size

            method.visitLdcInsn(size)
            element.newArray(method)
            for (index in 0 until size) {
                method.visitInsn(Opcodes.DUP)
                method.visitLdcInsn(index)
                pushElement(element, slot(first + index))
                method.visitInsn(element.storeOpcode)
            }

            wrapArray(type, element, first)
        }

        private fun newElements(
            type: Int,
            element: StorageElement,
            lengthSlot: Int,
        ) {
            method.visitVarInsn(Opcodes.ILOAD, lengthSlot)
            method.visitLdcInsn(lowering.storagePlans.array(type).elementShift)
            method.invoke(CHECK_ARRAY_LENGTH, Opcodes.INVOKESTATIC)
            element.newArray(method)
        }

        private fun wrapArray(
            type: Int,
            element: StorageElement,
            first: Int,
        ) {

            val className = internalName(lowering.storagePlans.array(type).className)
            method.visitVarInsn(Opcodes.ASTORE, slot(first))

            method.visitTypeInsn(Opcodes.NEW, className)
            method.visitInsn(Opcodes.DUP)
            method.pushResource(Resource.Rtt(type))
            method.visitVarInsn(Opcodes.ALOAD, slot(first))
            method.visitMethodInsn(Opcodes.INVOKESPECIAL, className, "<init>", "($RTT_TYPE${element.arrayDescriptor})V", false)

            replace(first)
        }

        private fun refI31() {

            val first = stackKinds.size - 1
            method.visitTypeInsn(Opcodes.NEW, internalName(I31_REFERENCE))
            method.visitInsn(Opcodes.DUP)
            method.visitVarInsn(Opcodes.ILOAD, slot(first))
            method.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName(I31_REFERENCE), "<init>", "(I)V", false)

            replace(first)
        }

        private fun arrayElement(type: Int): StorageElement {
            return storageElement(lowering.declarations.arrayElement(TypeIndex(type)))
        }

        private fun pushElement(
            element: StorageElement,
            valueSlot: Int,
        ) {
            when (element) {
                StorageElement.BYTE, StorageElement.CHAR, StorageElement.INT -> method.visitVarInsn(Opcodes.ILOAD, valueSlot)
                StorageElement.LONG -> method.visitVarInsn(Opcodes.LLOAD, valueSlot)
                StorageElement.FLOAT -> {
                    method.visitVarInsn(Opcodes.ILOAD, valueSlot)
                    method.invoke(INT_BITS_TO_FLOAT, Opcodes.INVOKESTATIC)
                }
                StorageElement.DOUBLE -> {
                    method.visitVarInsn(Opcodes.LLOAD, valueSlot)
                    method.invoke(LONG_BITS_TO_DOUBLE, Opcodes.INVOKESTATIC)
                }
                StorageElement.REFERENCE -> method.visitVarInsn(Opcodes.ALOAD, valueSlot)
            }
            element.narrow(method)
        }

        private fun binary(opcode: Int) {

            val left = slot(stackKinds.size - 2)
            val right = slot(stackKinds.size - 1)
            val wide = stackKinds.last() == StackValueKind.WIDE
            val load = if (wide) Opcodes.LLOAD else Opcodes.ILOAD

            method.visitVarInsn(load, left)
            method.visitVarInsn(load, right)
            method.visitInsn(opcode)
            method.visitVarInsn(if (wide) Opcodes.LSTORE else Opcodes.ISTORE, left)

            stackKinds.removeAt(stackKinds.lastIndex)
        }

        private inline fun push(
            kind: StackValueKind,
            emission: () -> Unit,
        ) {

            emission()
            val store = when (kind) {
                StackValueKind.NARROW -> Opcodes.ISTORE
                StackValueKind.WIDE -> Opcodes.LSTORE
                StackValueKind.OBJECT -> Opcodes.ASTORE
            }
            method.visitVarInsn(store, slot(stackKinds.size))

            stackKinds += kind
        }

        private fun replace(first: Int) {

            method.visitVarInsn(Opcodes.ASTORE, slot(first))
            while (stackKinds.size > first) {
                stackKinds.removeAt(stackKinds.lastIndex)
            }

            stackKinds += StackValueKind.OBJECT
        }

        private fun slot(depth: Int): Int {
            return FIRST_SLOT + 2 * depth
        }
    }

    private enum class StackValueKind {
        NARROW,
        WIDE,
        OBJECT,
    }

    private fun MethodVisitor.invoke(
        method: MethodRef,
        opcode: Int,
    ) {
        visitMethodInsn(opcode, internalName(method.owner), method.name, method.descriptor, false)
    }

    private companion object {
        const val OBJECT_ARRAY = "[Ljava/lang/Object;"
        const val INSTALLATION_LOCAL = 0
        const val BINDING_LOCAL = 1
        const val SEGMENT_LOCAL = 2
        const val FIRST_SLOT = 3
        const val EXPRESSIONS_PER_METHOD = 256
    }
}
