package io.github.charlietap.chasm.compiler.backend.jvm.binding

import io.github.charlietap.chasm.compiler.backend.abi.DATA_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.ELEMENT_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.GLOBAL_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MODULE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.jvm.abi.UNSAFE_TYPE
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodNode

internal class BindingField(
    val name: String,
    val descriptor: String,
    val part: Int,
)

internal class BindingLayout {

    private val fields = LinkedHashMap<Resource, BindingField>()

    val resources: Collection<Resource> get() = fields.keys

    val classes: Int get() = maxOf(1, (fields.size + FIELDS_PER_CLASS - 1) / FIELDS_PER_CLASS)

    fun field(
        resource: Resource,
    ): BindingField = fields.getOrPut(resource) {
        val index = fields.size
        val name = when (resource) {
            Resource.Instance -> "instance"
            is Resource.Memory -> "memory${resource.index}"
            is Resource.Table -> "table${resource.index}"
            is Resource.Global -> "global${resource.index}"
            is Resource.Function -> "function${resource.index}"
            is Resource.Element -> "element${resource.index}"
            is Resource.Data -> "data${resource.index}"
            is Resource.TypeTest -> "typeTest$index"
            is Resource.Longs -> "longs$index"
            is Resource.FunctionReference -> "reference${resource.index}"
            is Resource.TagAddress -> "tag${resource.index}"
            is Resource.RuntimeType -> "type${resource.typeIndex}"
            Resource.Unsafe -> "unsafe"
            Resource.ByteArrayBase -> "byteArrayBase"
            is Resource.ExceptionTable -> error("$resource has no binding field")
        }
        BindingField(name, bindingDescriptor(resource), index / FIELDS_PER_CLASS)
    }

    fun resolve(
        nodes: List<MethodNode>,
        resources: List<Resource>,
        bindingType: String,
    ) = nodes.forEach { node ->
        val instructions = node.instructions.iterator()
        while (instructions.hasNext()) {
            val instruction = instructions.next() as? FieldInsnNode ?: continue
            if (instruction.owner != PENDING_OWNER) continue
            val field = field(resources[instruction.name.substring(1).toInt()])
            if (instruction.name[0] == PART_PLACEHOLDER) {
                if (field.part == 0) {
                    instructions.remove()
                } else {
                    instruction.owner = internalName(bindingType)
                    instruction.name = bindingPartField(field.part)
                    instruction.desc = bindingPartType(bindingType, field.part)
                }
            } else {
                instruction.owner = internalName(bindingPartType(bindingType, field.part))
                instruction.name = field.name
                instruction.desc = field.descriptor
            }
        }
    }

    fun resourcesOf(
        part: Int,
    ): List<Resource> = fields.keys.drop(part * FIELDS_PER_CLASS).take(FIELDS_PER_CLASS)

    fun fieldOf(
        resource: Resource,
    ): BindingField = checkNotNull(fields[resource]) { "$resource is not bound" }
}

internal const val FIELDS_PER_CLASS = 8_000

internal fun bindingDescriptor(
    resource: Resource,
): String = when (resource) {
    Resource.Instance -> MODULE_INSTANCE
    is Resource.Memory -> MEMORY_INSTANCE
    is Resource.Table -> TABLE_INSTANCE
    is Resource.Global -> GLOBAL_INSTANCE
    is Resource.Function -> FUNCTION_INSTANCE
    is Resource.Element -> ELEMENT_INSTANCE
    is Resource.Data -> DATA_INSTANCE
    is Resource.TypeTest -> OBJECT_TYPE
    is Resource.Longs -> "[J"
    is Resource.FunctionReference -> "J"
    is Resource.TagAddress -> "I"
    is Resource.RuntimeType -> "I"
    Resource.Unsafe -> UNSAFE_TYPE
    Resource.ByteArrayBase -> "J"
    is Resource.ExceptionTable -> error("$resource has no binding field")
}

internal const val PENDING_OWNER = "chasm/PendingBinding"
internal const val PENDING_OWNER_TYPE = "L$PENDING_OWNER;"
internal const val PART_PLACEHOLDER = 'p'
internal const val FIELD_PLACEHOLDER = 'f'

internal fun bindingPartType(
    bindingType: String,
    part: Int,
): String = if (part == 0) bindingType else bindingType.removeSuffix(";") + "Part$part;"

internal fun bindingPartField(
    part: Int,
): String = "part$part"
