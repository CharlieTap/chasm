package io.github.charlietap.chasm.aot

internal interface StagedClassOutput : ClassOutput {

    fun commit()

    fun discard()
}
