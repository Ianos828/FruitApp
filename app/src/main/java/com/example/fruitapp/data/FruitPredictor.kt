package com.example.fruitapp.data

import android.content.Context
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.example.fruitapp.model.Esp32Measurement
import com.example.fruitapp.model.PressureMeasurement
import java.nio.FloatBuffer

/**
 * Utility class to handle fruit ripeness prediction using an ONNX model.
 */
class FruitPredictor(private val context: Context) {

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession

    init {
        // Load the model from the assets folder
        val modelBytes = context.assets.open("fruit_model.onnx").readBytes()
        session = env.createSession(modelBytes)
    }

    /**
     * Performs a prediction based on sensor data.
     * Arguments order: ["fluorescence", "nir_680", "nir_705", "nir_730", "nir_760", "nir_810", "nir_860", "nir_940", "distance_mm", "ethylene_ppb", "air_quality", "mq3", "mq4", "mq5", "forceSensorReading"]
     */
    fun predict(esp32: Esp32Measurement, pressure: PressureMeasurement): String {
        return try {
            val inputName = session.inputNames.iterator().next()
            
            // Prepare the input data array
            val inputData = floatArrayOf(
                esp32.fluorescenceReading,
                esp32.nir680Reading,
                esp32.nir705Reading,
                esp32.nir730Reading,
                esp32.nir760Reading,
                esp32.nir810Reading,
                esp32.nir860Reading,
                esp32.nir940Reading,
                esp32.ethyleneConcentration.toFloat(),
                esp32.airQuality.toFloat(),
                esp32.mq3Reading.toFloat(),
                esp32.mq4Reading.toFloat(),
                esp32.mq5Reading.toFloat(),
                pressure.forceSensorReading
            )

            // Create input tensor
            val inputTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputData), longArrayOf(1, 15))
            
            inputTensor.use {
                val output = session.run(mapOf(inputName to inputTensor))
                output.use {
                    val rawValue = output[0].value
                    if (rawValue is Array<*> && rawValue.isNotEmpty()) {
                        return rawValue[0].toString()
                    }
                    "Unknown Result"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error predicting"
        }
    }

    /**
     * Closes the ONNX session and environment.
     */
    fun close() {
        session.close()
        env.close()
    }
}
