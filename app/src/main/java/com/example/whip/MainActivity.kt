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
import android.view.MotionEvent
import android.view.View

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var lastPlayTime: Long = 0
    private val playInterval: Long = 500  // 0.5 秒间隔
    private val shakeThreshold = 12f      // 摇晃阈值

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.whip)

        // 根布局点击播放
        val rootLayout = findViewById<View>(R.id.root_layout)
        rootLayout.setOnTouchListener { _, _ ->
            tryPlay()
            true
        }

        // 传感器
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
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

    private fun tryPlay() {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastPlayTime >= playInterval) {
            mediaPlayer?.start()
            lastPlayTime = currentTime
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gForce = Math.sqrt((x*x + y*y + z*z).toDouble()) / SensorManager.GRAVITY_EARTH

        if (gForce > shakeThreshold) {
            tryPlay()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
