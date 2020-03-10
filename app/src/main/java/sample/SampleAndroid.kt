package sample

import android.Manifest.permission.*
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*

actual class Sample {
    actual fun checkMe() = 44
}

actual object Platform {
    actual val name: String = "Android"
}


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissions = arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, INTERNET)
        ActivityCompat.requestPermissions(this, permissions, Sample().checkMe())
        setContentView(R.layout.activity_main)

        val userFile = File(Environment.getExternalStorageDirectory(), "recorded.gpp").apply {
            ensureExists()
            deleteOnExit()
        }
        val stateMachine = SessionStateMachine(
            this, userFile,
//            recorder = MediaRecorder().apply {
//                setAudioSource(MediaRecorder.AudioSource.MIC)
//                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                setOutputFile(userFile.path)
//            },
            filePlayer = MediaPlayer.create(this, R.raw.thephrase)/*,
            recordedPlayer = MediaPlayer.create(this, Uri.fromFile(userFile))*/
        )
        configView()
        mapHandles(stateMachine)
    }

    private fun mapHandles(stateMachine: SessionStateMachine) {
        recordButton runs { stateMachine.next(Action.StartRecord) }
        stopButton runs { stateMachine.next(Action.Stop) }
        playButton runs { stateMachine.next(Action.StartListen) }
        repeatButton runs { stateMachine.next(Action.Review) }
    }

    private fun configView() {
        main_text.text = hello()
    }
}

private infix fun Button.runs(callback: () -> Any) = setOnClickListener { callback() }
