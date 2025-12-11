package com.example.whip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.media.MediaPlayer
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import android.os.SystemClock
import android.widget.Button

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var lastShakeTime: Long = 0
    private val shakeInterval: Long = 1000   // 1 秒
    private val shakeThreshold = 1.8f        // ← 修复后的正确阈值

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.whip)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // 点击按钮播放（可选）
        val playButton = findViewById<Button>(R.id.playButton)
        playButton?.setOnClickListener {
            mediaPlayer?.start()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // 正确计算 gForce
        val gForce = Math.sqrt((x*x + y*y + z*z).toDouble()) / SensorManager.GRAVITY_EARTH

        // 低阈值才能检测到挥动
        if (gForce > shakeThreshold) {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastShakeTime >= shakeInterval) {
                mediaPlayer?.start()
                lastShakeTime = currentTime
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
