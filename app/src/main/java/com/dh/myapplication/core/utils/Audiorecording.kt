package com.dh.myapplication.core.utils

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.media.AudioFormat
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AudioRecorder(private val context: Context) {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val audioFile: File

    init {
        val audioDirectory = File(
            context.getExternalFilesDir(null), // Speicherort im privaten Dateiverzeichnis Ihrer App
            "AudioRecordings"
        )

        if (!audioDirectory.exists()) {
            audioDirectory.mkdirs()
        }

        audioFile = File(audioDirectory, "audio.wav")
    }

    fun startRecording() {
        if (!isRecording) {
            // Überprüfen, ob die Berechtigung zur Aufnahme verfügbar ist
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                val audioSource = MediaRecorder.AudioSource.MIC
                val sampleRate = 44100
                val channelConfig = AudioFormat.CHANNEL_IN_MONO
                val audioFormat = AudioFormat.ENCODING_PCM_16BIT

                val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
                audioRecord = AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, minBufferSize)

                val buffer = ByteArray(minBufferSize)

                try {
                    val fileStream = FileOutputStream(audioFile)
                    audioRecord?.startRecording()
                    isRecording = true

                    while (isRecording) {
                        val bytesRead = audioRecord?.read(buffer, 0, minBufferSize)
                        if (bytesRead != AudioRecord.ERROR_INVALID_OPERATION) {
                            fileStream.write(buffer, 0, bytesRead ?: 0)
                        }
                    }

                    fileStream.close()
                    audioRecord?.stop()
                    audioRecord?.release()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                // Hier können Sie den Benutzer auffordern, die Berechtigung zur Aufnahme zu erteilen
                // Zum Beispiel, eine Meldung anzeigen oder den Benutzer zur Einstellungsseite weiterleiten
            }
        }
    }

    fun stopRecording() {
        isRecording = false
    }

    fun getAudioFile(): File {
        return audioFile
    }
}
