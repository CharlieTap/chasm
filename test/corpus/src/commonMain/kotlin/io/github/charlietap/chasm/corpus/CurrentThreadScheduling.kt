package io.github.charlietap.chasm.corpus

interface CurrentThreadScheduling {

    fun raisePriority()

    fun priority(): Int

    fun allowedCpus(): String

    fun maxFrequencyKhz(cpu: Int): Long

    fun pin(cpu: Int)
}
