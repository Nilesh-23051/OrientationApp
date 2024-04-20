package com.hello.orientationapp

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class OrientationViewModel(application: Application) : AndroidViewModel(application) {
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val database = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "orientation_database"
    ).build()

    private val orientationDao = database.orientationDao()

    private val _orientationData = MutableLiveData<List<OrientationData>>()
    val orientationData: LiveData<List<OrientationData>> = _orientationData

    private val _azimuth = MutableLiveData<Float>()
    val azimuth: LiveData<Float> = _azimuth

    private val _pitch = MutableLiveData<Float>()
    val pitch: LiveData<Float> = _pitch

    private val _roll = MutableLiveData<Float>()
    val roll: LiveData<Float> = _roll

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val azimuth = it.values[0]
                val pitch = it.values[1]
                val roll = it.values[2]

                _azimuth.postValue(azimuth)
                _pitch.postValue(pitch)
                _roll.postValue(roll)

                viewModelScope.launch(Dispatchers.IO) {
                    orientationDao.insert(
                        OrientationData(
                            timestamp = System.currentTimeMillis(),
                            azimuth = azimuth,
                            pitch = pitch,
                            roll = roll
                        )
                    )
                    _orientationData.postValue(orientationDao.getAll())
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    init {
        sensorManager.registerListener(
            sensorListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun exportDataToFile(): File {
        val orientationData = orientationDao.getAll()
        val fileName = "orientation_data.txt"
        val file = File(getApplication<Application>().filesDir, fileName)

        file.writeText(orientationData.joinToString("\n") { data ->
            "${data.timestamp},${data.azimuth},${data.pitch},${data.roll}"
        })

        return file
    }

    suspend fun getPredictions(): List<Pair<OrientationData, OrientationData>> = viewModelScope.async {
        val orientationData = orientationDao.getAll()
        val predictions = mutableListOf<Pair<OrientationData, OrientationData>>()

        for (i in orientationData.indices) {
            if (i + 10 < orientationData.size) {
                val actual = orientationData[i]
                val predicted = OrientationData(
                    timestamp = actual.timestamp + 10000, // Assuming 10 seconds prediction
                    azimuth = actual.azimuth + 10f, // Dummy prediction logic
                    pitch = actual.pitch + 10f,
                    roll = actual.roll + 10f
                )
                predictions.add(Pair(actual, predicted))
            }
        }

        predictions
    }.await()

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
