package io.github.charlietap.chasm.runtime.compiled.gc.storage

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.compiled.gc.CompiledArray
import io.github.charlietap.chasm.runtime.compiled.gc.Rtt
import kotlin.jvm.JvmField

@InternalChasmApi
class I8Array(
    rtt: Rtt,
    @JvmField val data: ByteArray,
) : CompiledArray(rtt) {

    override fun storage(): Any {
        return data
    }

    override fun length(): Int {
        return data.size
    }
}

@InternalChasmApi
class I16Array(
    rtt: Rtt,
    @JvmField val data: CharArray,
) : CompiledArray(rtt) {

    override fun storage(): Any {
        return data
    }

    override fun length(): Int {
        return data.size
    }
}

@InternalChasmApi
class I32Array(
    rtt: Rtt,
    @JvmField val data: IntArray,
) : CompiledArray(rtt) {

    override fun storage(): Any {
        return data
    }

    override fun length(): Int {
        return data.size
    }
}

@InternalChasmApi
class I64Array(
    rtt: Rtt,
    @JvmField val data: LongArray,
) : CompiledArray(rtt) {

    override fun storage(): Any {
        return data
    }

    override fun length(): Int {
        return data.size
    }
}

@InternalChasmApi
class F32Array(
    rtt: Rtt,
    @JvmField val data: FloatArray,
) : CompiledArray(rtt) {

    override fun storage(): Any {
        return data
    }

    override fun length(): Int {
        return data.size
    }
}

@InternalChasmApi
class F64Array(
    rtt: Rtt,
    @JvmField val data: DoubleArray,
) : CompiledArray(rtt) {

    override fun storage(): Any {
        return data
    }

    override fun length(): Int {
        return data.size
    }
}

@InternalChasmApi
class RefArray(
    rtt: Rtt,
    @JvmField val data: Array<Any?>,
) : CompiledArray(rtt) {

    override fun storage(): Any {
        return data
    }

    override fun length(): Int {
        return data.size
    }
}
