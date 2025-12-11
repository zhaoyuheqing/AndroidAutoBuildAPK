package com.example.whip
import android.media.MediaPlayer
import android.hardware.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var mediaPlayer: MediaPlayer? = null
    private var lastTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mediaPlayer = MediaPlayer.create(this, R.raw.whip)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME)
    }
    override fun onSensorChanged(event: SensorEvent) {
        val (x,y,z)=event.values
        val speed=x*x+y*y+z*z
        val now=System.currentTimeMillis()
        if(speed>120 && now-lastTime>800){
            mediaPlayer?.start()
            lastTime=now
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
