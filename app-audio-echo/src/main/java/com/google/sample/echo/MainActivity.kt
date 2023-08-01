package com.google.sample.echo

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.sample.echo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {
    private val AUDIO_ECHO_REQUEST = 0

    private var nativeSampleRate: String? = null
    private var nativeSampleBufSize: String? = null

    private var echoDelayProgress = 0

    private var echoDecayProgress = 0f

    private var supportRecording = false
    private var isPlaying = false

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        queryNativeAudioParameters()
        echoDelayProgress = binding.delaySeekBar.progress * 1000 / binding.delaySeekBar.max
        binding.delaySeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val curVal = progress.toFloat() / binding.delaySeekBar.max
                binding.curDelay.text = String.format("%s", curVal)
                setSeekBarPromptPosition(binding.delaySeekBar, binding.curDelay)
                if (!fromUser) return
                echoDelayProgress = progress * 1000 / binding.delaySeekBar.max
                configureEcho(echoDelayProgress, echoDecayProgress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        binding.delaySeekBar.post {
            setSeekBarPromptPosition(
                binding.delaySeekBar,
                binding.curDelay
            )
        }
        echoDecayProgress = binding.decaySeekBar.progress.toFloat() / binding.decaySeekBar.max
        binding.decaySeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val curVal = progress.toFloat() / seekBar.max
                binding.curDecay.text = String.format("%s", curVal)
                setSeekBarPromptPosition(binding.decaySeekBar, binding.curDecay)
                if (!fromUser) return
                echoDecayProgress = curVal
                configureEcho(echoDelayProgress, echoDecayProgress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        binding.decaySeekBar.post {
            setSeekBarPromptPosition(
                binding.decaySeekBar,
                binding.curDecay
            )
        }

        // initialize native audio system
        updateNativeAudioUI()
        if (supportRecording) {
            createSLEngine(
                nativeSampleRate!!.toInt(), nativeSampleBufSize!!.toInt(),
                echoDelayProgress.toLong(),
                echoDecayProgress
            )
        }
    }

    private fun setSeekBarPromptPosition(seekBar: SeekBar?, label: TextView?) {
        val thumbX = seekBar!!.progress.toFloat() / seekBar.max *
                seekBar.width + seekBar.x
        label!!.x = thumbX - label.width / 2.0f
    }

    override fun onDestroy() {
        if (supportRecording) {
            if (isPlaying) {
                stopPlay()
            }
            deleteSLEngine()
            isPlaying = false
        }
        super.onDestroy()
    }

    private fun startEcho() {
        if (!supportRecording) {
            return
        }
        if (!isPlaying) {
            if (!createSLBufferQueueAudioPlayer()) {
                binding.statusView.text = getString(R.string.player_error_msg)
                return
            }
            if (!createAudioRecorder()) {
                deleteSLBufferQueueAudioPlayer()
                binding.statusView.text = getString(R.string.recorder_error_msg)
                return
            }
            startPlay() // startPlay() triggers startRecording()
            binding.statusView.text = getString(R.string.echoing_status_msg)
        } else {
            stopPlay() // stopPlay() triggers stopRecording()
            updateNativeAudioUI()
            deleteAudioRecorder()
            deleteSLBufferQueueAudioPlayer()
        }
        isPlaying = !isPlaying
        binding.captureControlButton.text =
            getString(if (isPlaying) R.string.cmd_stop_echo else R.string.cmd_start_echo)
    }

    fun onEchoClick(view: View?) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            binding.statusView.text = getString(R.string.request_permission_status_msg)
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO),
                AUDIO_ECHO_REQUEST
            )
            return
        }
        startEcho()
    }

    fun getLowLatencyParameters(view: View?) {
        updateNativeAudioUI()
    }

    private fun queryNativeAudioParameters() {
        supportRecording = true
        val myAudioMgr = getSystemService(AUDIO_SERVICE) as AudioManager
        if (myAudioMgr == null) {
            supportRecording = false
            return
        }

        nativeSampleRate = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
        nativeSampleBufSize = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)

        // hardcoded channel to mono: both sides -- C++ and Java sides
        val recBufSize = AudioRecord.getMinBufferSize(
            nativeSampleRate?.toInt() ?: 0,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (recBufSize == AudioRecord.ERROR ||
            recBufSize == AudioRecord.ERROR_BAD_VALUE
        ) {
            supportRecording = false
        }
    }

    private fun updateNativeAudioUI() {
        if (!supportRecording) {
            binding.statusView.text = getString(R.string.mic_error_msg)
            binding.captureControlButton.isEnabled = false
            return
        }
        binding.statusView.text = getString(
            R.string.fast_audio_info_msg,
            nativeSampleRate, nativeSampleBufSize
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        /*
         * if any permission failed, the sample could not play
         */
        if (AUDIO_ECHO_REQUEST != requestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size != 1 ||
            grantResults[0] != PackageManager.PERMISSION_GRANTED
        ) {
            /*
             * When user denied permission, throw a Toast to prompt that RECORD_AUDIO
             * is necessary; also display the status on UI
             * Then application goes back to the original state: it behaves as if the button
             * was not clicked. The assumption is that user will re-click the "start" button
             * (to retry), or shutdown the app in normal way.
             */
            binding.statusView.text = getString(R.string.permission_error_msg)
            Toast.makeText(
                applicationContext,
                getString(R.string.permission_prompt_msg),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        /*
         * When permissions are granted, we prompt the user the status. User would
         * re-try the "start" button to perform the normal operation. This saves us the extra
         * logic in code for async processing of the button listener.
         */
        binding.statusView.text =
            getString(R.string.permission_granted_msg, getString(R.string.cmd_start_echo))


        // The callback runs on app's thread, so we are safe to resume the action
        startEcho()
    }

    /*
     * Loading our lib
     */
    companion object {
        init {
            System.loadLibrary("echo")
        }
    }

    /*
     * jni function declarations
     */
    external fun createSLEngine(
        rate: Int, framesPerBuf: Int,
        delayInMs: Long, decay: Float
    )

    external fun deleteSLEngine()
    external fun configureEcho(delayInMs: Int, decay: Float): Boolean
    external fun createSLBufferQueueAudioPlayer(): Boolean
    external fun deleteSLBufferQueueAudioPlayer()

    external fun createAudioRecorder(): Boolean
    external fun deleteAudioRecorder()
    external fun startPlay()
    external fun stopPlay()
}