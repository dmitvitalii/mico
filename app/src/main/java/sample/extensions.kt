package sample

import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import java.io.File

fun Handler.postDelayed(delayMillis: Long, runnable: () -> Unit) = postDelayed(runnable, delayMillis)

@RequiresApi(Build.VERSION_CODES.P)
fun Handler.postDelayed(token: Any, delayMillis: Long, runnable: () -> Unit) = postDelayed(runnable, token, delayMillis)

fun File.ensureExists(): File {
    if (!exists()) {
        createNewFile()
    }
    return this
}


