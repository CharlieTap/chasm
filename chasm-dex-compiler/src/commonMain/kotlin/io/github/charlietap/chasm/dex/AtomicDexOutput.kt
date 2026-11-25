package io.github.charlietap.chasm.dex

internal interface AtomicDexOutput : DexOutput {

    fun commit()

    fun discard()
}
