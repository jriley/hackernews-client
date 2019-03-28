package dev.jriley.nyt

import android.util.Log
import timber.log.Timber

class LogTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.INFO

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        when (priority) {
            Log.VERBOSE -> return
            Log.DEBUG -> return
        }
        // TODO Other stuff with the Higher Priorities
    }
}