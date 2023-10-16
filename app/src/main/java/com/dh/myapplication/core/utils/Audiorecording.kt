package com.dh.myapplication.core.utils

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioRecord
import android.media.AudioFormat
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException




class AudioRecorder(private val context: Context) {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val audioWavFile: File
    private val audioRawFile: File

    init {
        val audioDirectory = File(
            context.getExternalFilesDir(null), // Speicherort im privaten Dateiverzeichnis Ihrer App
            "AudioRecordings"
        )

        if (!audioDirectory.exists()) {
            audioDirectory.mkdirs()
        }

        audioWavFile = File(audioDirectory, "audio.wav")
        audioRawFile = File(audioDirectory, "audio.raw")
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
                    val wavFileStream = FileOutputStream(audioWavFile)
                    val rawFileStream = FileOutputStream(audioRawFile)

                    audioRecord?.startRecording()
                    isRecording = true

                    while (isRecording) {
                        val bytesRead = audioRecord?.read(buffer, 0, minBufferSize)
                        if (bytesRead != AudioRecord.ERROR_INVALID_OPERATION) {
                            // Schreibe die Daten sowohl in die WAV-Datei als auch in die RAW-Datei
                            wavFileStream.write(buffer, 0, bytesRead ?: 0)
                            rawFileStream.write(buffer, 0, bytesRead ?: 0)
                        }
                    }

                    wavFileStream.close()
                    rawFileStream.close()
                    audioRecord?.stop()
                    audioRecord?.release()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                // Zeige eine Nachricht an, dass die Berechtigung fehlt
                Toast.makeText(context, "Die Berechtigung zur Aufnahme fehlt.", Toast.LENGTH_SHORT).show()
                // Zeige dann das Dialogfenster zur Berechtigungsanfrage
                showPermissionRequestDialog()
            }
        }
    }

    fun stopRecording() {
        isRecording = false
    }

    fun getAudioWavFile(): File {
        return audioWavFile
    }

    fun getAudioRawFile(): File {
        return audioRawFile
    }
}
