package sample

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Handler
import android.widget.Toast
import java.io.File
import kotlin.concurrent.thread

class IllegalState : State {
    override fun next(action: Action): State = throw IllegalStateException("IllegalState.next() has been called.")
}

class SessionStateMachine(
    private val context: Context,
    private val userFile: File,
//    private val recorder: MediaRecorder,
    private val filePlayer: MediaPlayer/*,
    private val recordedPlayer: MediaPlayer*/
) {

    companion object {
        val recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(userFile.path)
        }

        val LISTEN_STATE = ListenState()
    }

    private lateinit var recorder: MediaRecorder
    private val recordTime
        get() = filePlayer.duration + 1000L
    private val recordedPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context, Uri.fromFile(userFile))
    }

    private var currentState: State = InitState().next(Action.Reset)

    fun next(action: Action) {
        currentState = currentState.next(action).launch()
    }

    fun session() {
        currentState = Session().performEach()
    }

    class InitState : State {

        override fun launch(): State {

            return this
        }

        override fun next(action: Action): State = IdleState()
    }

    class IdleState : State {

        override fun next(action: Action) =
            when (action) {
                Action.StartListen -> ListenState() // its not
                Action.StartRecord -> RecordState()
                else -> illegalState(context, action, this)
            }
    }

    class ListenState(
        private val filePlayer: MediaPlayer
    ) : State {

        override fun launch(): State {
            filePlayer.prepare()
            filePlayer.start()
            return this
        }

        override fun next(action: Action): State {
            return when (action) {
                Action.StartListen -> ListenState()
                Action.StartRecord -> RecordState()
                Action.Reset -> IdleState()
                Action.Stop -> {
                    filePlayer.stop()
                    IdleState()
                }
                else -> illegalState(action, this)
            }
        }
    }

    class RecordState : State {

        private val sessionHandler = Handler()
        private var recording = false

        private val onStop = {
            if (recording) {
                stop()
                Toast.makeText(context, "Stopped", Toast.LENGTH_LONG).show()
            }
        }

        private val init = thread(start = true) {
            recorder.prepare()
            recorder.start() // or resume?
            recording = true
            sessionHandler.postDelayed(recordTime, onStop)
        }

        private fun stop() {
            if (recording) {
                recorder.stop()
//                recorder.release()
                recording = false
            }
        }

        override fun next(action: Action): State {
            init.join()
            return when (action) {
                Action.Review -> {
                    stop()
                    ReviewState()
                }
                Action.StartListen -> ListenState()
                Action.Reset -> IdleState()
                Action.Stop -> {
                    stop()
                    InitState().next(action)
                }
                else -> illegalState(context, action, this)
            }
        }
    }

    class ReviewState : State {
        private val init = thread {
            filePlayer.setOnCompletionListener {
                recordedPlayer.prepare()
                recordedPlayer.start()
            }
            filePlayer.prepare()
            filePlayer.start()
        }

        override fun launch(): State {
            init.start()
            return this
        }

        override fun next(action: Action): State {
            init.join()
            return when (action) {
                Action.StartListen -> LISTEN_STATE
                Action.Review -> ReviewState()
                Action.Reset -> IdleState()
                Action.Stop -> {
                    filePlayer.stop()
                    recordedPlayer.stop()
                    IdleState()
                }
                else -> illegalState(context, action, this)
            }
        }
    }

    class Session : StateRunner {
        override fun performEach(): State {
            return ListenState()
                .next(Action.StartRecord)
                .next(Action.Review)
        }
    }
}

