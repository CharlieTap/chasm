package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.jvm.binding.FIELD_PLACEHOLDER
import io.github.charlietap.chasm.compiler.backend.jvm.binding.PART_PLACEHOLDER
import io.github.charlietap.chasm.compiler.backend.jvm.binding.PENDING_OWNER_TYPE
import io.github.charlietap.chasm.compiler.backend.jvm.binding.bindingDescriptor
import org.objectweb.asm.Opcodes

internal fun LowerContext.pushResource(
    resource: Resource,
) {
    val id = facts.resourceId(resource)
    emit.load(Opcodes.ALOAD, Locals.BINDING)
    emit.getField(PENDING_OWNER_TYPE, "$PART_PLACEHOLDER$id", OBJECT_DESCRIPTOR)
    emit.getField(PENDING_OWNER_TYPE, "$FIELD_PLACEHOLDER$id", bindingDescriptor(resource))
}

private const val OBJECT_DESCRIPTOR = "Ljava/lang/Object;"

internal fun LowerContext.pushContext() = emit.load(Opcodes.ALOAD, Locals.CONTEXT)
