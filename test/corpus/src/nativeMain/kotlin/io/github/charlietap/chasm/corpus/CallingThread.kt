package io.github.charlietap.chasm.corpus

class CallingThread : FixtureThread {

    override fun <T> run(body: () -> T): T {
        return body()
    }
}
