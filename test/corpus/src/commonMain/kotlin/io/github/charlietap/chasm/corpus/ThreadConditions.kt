package io.github.charlietap.chasm.corpus

interface ThreadConditions {

    fun record(
        priority: Int,
        cpus: String,
        pinnedCpu: Int?,
    )
}
