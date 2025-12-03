package com.agroberriesmx.combustiblesagroberries.ui.camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.agroberriesmx.combustiblesagroberries.R
import com.agroberriesmx.combustiblesagroberries.databinding.ActivityCameraXBinding
import com.agroberriesmx.combustiblesagroberries.ui.fuel.FuelFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraXBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraXBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ocultar la barra de estado y de navegación para una experiencia inmersiva
        supportActionBar?.hide()

        cameraExecutor = Executors.newSingleThreadExecutor()

        // 1. Iniciar la cámara (Vista previa y casos de uso)
        startCamera()

        // 2. Configurar el botón de captura
        binding.cameraCaptureButton.setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera() {
        // Obtiene el proveedor de cámara (singleton)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // 1. Configuración de la Vista Previa (Preview)
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // 2. Configuración de la Captura de Imagen (ImageCapture)
            imageCapture = ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO) // Flash automático
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY) // Máxima calidad
                .build()

            // 3. Seleccionar la cámara trasera por defecto
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Desligar casos de uso previos (si los hay)
                cameraProvider.unbindAll()

                // Ligar la cámara al lifecycle del Activity
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e("CameraX", "Fallo al ligar casos de uso", exc)
                showToast("Error al iniciar la cámara: ${exc.message}")
                setResult(Activity.RESULT_CANCELED)
                finish()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Asegurarse de que imageCapture esté inicializado
        val imageCapture = imageCapture ?: return

        // 1. Crear el archivo de salida
        val photoFile: File = try {
            createImageFile()
        } catch (ex: IOException) {
            showToast("Error al crear archivo de imagen: ${ex.message}")
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        // 2. Obtener la URI del archivo usando FileProvider (¡Clave!)
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider", // Asegúrate de que coincida con tu Manifest
            photoFile
        )

        // 3. Crear las opciones de salida de CameraX
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // 4. Iniciar la captura de imagen
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraX", "Fallo de captura: ${exc.message}", exc)
                    showToast("Fallo de captura: ${exc.message}")
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // La imagen se guardó exitosamente en el archivo temporal
                    val savedUri = output.savedUri ?: photoURI

                    // 5. Devolver el resultado al Fragmento
                    val resultIntent = Intent().apply {
                        putExtra("EXTRA_PHOTO_URI", savedUri.toString()) // Envía el URI como String
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish() // Cierra la actividad de la cámara
                }
            }
        )
    }

    // *** FUNCIÓN CLAVE DE TU FRAGMENTO, REUTILIZADA AQUÍ ***
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    // Función auxiliar para mostrar Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}