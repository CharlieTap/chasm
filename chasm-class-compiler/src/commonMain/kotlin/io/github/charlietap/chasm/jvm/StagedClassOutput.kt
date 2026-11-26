package io.github.charlietap.chasm.jvm

internal interface StagedClassOutput : ClassOutput {

    fun commit()

    fun discard()
}
