package sample

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import java.io.File

/*actual*/ class PlaybackHandler(
    private val context: Context,
    private val audioFile: Int
) {
    private lateinit var recorder: MediaRecorder
    private val userFile: File
        get() = File(Environment.getExternalStorageDirectory(), "recorded.gpp")
    private val filePlayer: MediaPlayer
        get() = MediaPlayer.create(context, audioFile)
    private lateinit var recordedPlayer: MediaPlayer

    /*actual*/ fun init() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(userFile.path)
            prepare()
        }
    }

    /*actual*/ fun start() {
        play(GIVEN)
        record()
        play(RECORDED)
        review()
    }

    /*actual*/ fun review() {
        play(GIVEN)
        play(RECORDED)
    }

    /*actual*/ fun repeat() {
        init()
        start()
    }

    /*actual*/ fun record() {
        recorder.start()
//        recordedPlayer = MediaPlayer.create(context, Uri.fromFile(userFile))
    }

    /*actual*/ fun finish() {
        recorder.apply { stop(); release() }
        filePlayer.apply { stop(); release() }
        recordedPlayer.apply { stop(); release() }
    }

    fun stop() {
        recorder.stop()
        recordedPlayer = MediaPlayer.create(context, Uri.fromFile(userFile))
    }

    /*actual*/ fun play(name: String) {
        val player = when (name) {
            RECORDED -> recordedPlayer
            GIVEN -> filePlayer
            else -> filePlayer
        }
        if (player.isPlaying) player.stop()
        player.start()
    }
}
