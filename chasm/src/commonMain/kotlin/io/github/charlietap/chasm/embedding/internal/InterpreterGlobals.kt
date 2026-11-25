package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.runtime.host.withScope
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.type.Mutability

internal interface GlobalAccess {

    fun readGlobal(global: Global): Long

    fun readGlobalReference(global: Global): Any?

    fun writeGlobal(
        global: Global,
        value: Long,
    )

    fun writeGlobalReference(
        global: Global,
        value: Any?,
    )
}

internal class InterpreterGlobals(
    private val handles: EngineHandles,
    private val codec: ReferenceCodec,
    private val heap: WasmHeap,
) : GlobalAccess {

    override fun readGlobal(global: Global): Long {

        val payload = handles.globalPayload(global)
        require(payload.referenceType == null) { "the global holds a reference" }

        return payload.instance.value
    }

    override fun readGlobalReference(global: Global): Any? {

        val payload = handles.globalPayload(global)
        requireNotNull(payload.referenceType) { "the global holds a number" }

        return codec.decode(payload.instance.value)
    }

    override fun writeGlobal(
        global: Global,
        value: Long,
    ) {

        val payload = handles.globalPayload(global)
        require(payload.referenceType == null) { "the global holds a reference" }
        requireMutable(payload.instance)

        payload.instance.value = value
    }

    override fun writeGlobalReference(
        global: Global,
        value: Any?,
    ) {

        val payload = handles.globalPayload(global)
        val type = requireNotNull(payload.referenceType) { "the global holds a number" }
        requireMutable(payload.instance)

        withAccessorFailures {
            heap.withScope {
                payload.instance.value = codec.encode(type, value)
            }
        }
    }

    private fun requireMutable(instance: GlobalInstance) {
        require(instance.type.mutability == Mutability.Var) { "the global is immutable" }
    }
}
