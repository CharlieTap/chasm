package io.github.charlietap.chasm.corpus

import android.app.Instrumentation
import android.os.Bundle

class InstrumentationThreadConditions(
    private val instrumentation: Instrumentation,
) : ThreadConditions {

    override fun record(
        priority: Int,
        cpus: String,
        pinnedCpu: Int?,
    ) {

        val pinned = pinnedCpu?.let { cpu -> " pinnedCpu=$cpu" }.orEmpty()
        val status = Bundle()
        status.putString(THREAD_STATUS_KEY, "priority=$priority cpus=$cpus$pinned")

        instrumentation.sendStatus(IN_PROGRESS, status)
    }

    private companion object {
        const val THREAD_STATUS_KEY = "corpus.thread"
        const val IN_PROGRESS = 2
    }
}
