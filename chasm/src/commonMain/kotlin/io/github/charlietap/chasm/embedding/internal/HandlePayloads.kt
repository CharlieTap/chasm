package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance

internal sealed interface InstancePayload {

    val external: ExternalValue
}

internal class MemoryPayload(
    override val external: ExternalValue.Memory,
    val instance: MemoryInstance,
) : InstancePayload {

    override fun equals(other: Any?): Boolean {
        return other is MemoryPayload && other.external == external
    }

    override fun hashCode(): Int {
        return external.hashCode()
    }
}

internal class GlobalPayload(
    override val external: ExternalValue.Global,
    val instance: GlobalInstance,
    val referenceType: ResolvedReferenceType?,
) : InstancePayload {

    override fun equals(other: Any?): Boolean {
        return other is GlobalPayload && other.external == external
    }

    override fun hashCode(): Int {
        return external.hashCode()
    }
}

internal class TablePayload(
    override val external: ExternalValue.Table,
    val instance: TableInstance,
    val referenceType: ResolvedReferenceType,
) : InstancePayload {

    override fun equals(other: Any?): Boolean {
        return other is TablePayload && other.external == external
    }

    override fun hashCode(): Int {
        return external.hashCode()
    }
}
