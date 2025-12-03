package com.agroberriesmx.combustiblesagroberries.ui.fuel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.agroberriesmx.combustiblesagroberries.R
import com.agroberriesmx.combustiblesagroberries.databinding.FragmentFuelBinding
import com.agroberriesmx.combustiblesagroberries.domain.RecordsRepository
import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import androidx.activity.result.ActivityResultLauncher
import android.graphics.Bitmap // Para Bitmap
import android.provider.MediaStore // Para MediaStore.ACTION_IMAGE_CAPTURE
import android.Manifest // Para Manifest.permission.CAMERA
import android.app.AlertDialog
import android.content.pm.PackageManager // Para PackageManager
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat // Para ContextCompat.checkSelfPermission

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

import com.journeyapps.barcodescanner.CaptureActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

// --- Importaciones necesarias para la API ---
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
// --- Fin de importaciones API ---

import android.net.Uri // Asegúrate de tener esta importación
import android.os.Environment
import androidx.core.content.FileProvider // Asegúrate de tener esta importación
import java.io.File // Asegúrate de tener esta importación
import java.io.IOException

@AndroidEntryPoint
class FuelFragment : Fragment() {
    private var _binding: FragmentFuelBinding? = null
    private val binding get() = _binding!!
    private val fuelViewModel: FuelViewModel by viewModels()
    private var isWorkerScanned = false
    private  var fuelValue: String = ""
    private  var fieldValue: String = ""
    private  var fieldZoneValue: String = ""
    private  var activityValue: String =  ""

    // Variables para la lógica de la foto
    private var photoCounter: Int = 0 // Contador de fotos subidas
    private var fixedAssetPhotoUploaded: Boolean = false // Indica si la foto del activo fijo ha sido subida
    private var pumpPhotoUploaded: Boolean = false // Indica si la foto de la bomba ha sido subida
    private var lastCapturedBitmap: Bitmap? = null // Para guardar temporalmente la última foto tomada
    private var currentPhotoTypeToUpload: PhotoType? = null // Para saber qué tipo de foto se está procesando

    private var currentPhotoUri: Uri? = null // <-- ¡AÑADE ESTA LÍNEA! Para guardar el URI de la foto de alta resolución


    enum class PhotoType {
        FIXED_ASSET, PUMP
    }

    private val fuelSelectorOptions = arrayOf("Gasolina", "Diesel")
    private val fuelSelectorMap = mapOf(
        "Gasolina" to "1",
        "Diesel" to "2"
    )

    private lateinit var fieldSelectorOptions: List<String>
    private lateinit var fieldSelectorMap: Map<String, String>
    private lateinit var fieldZoneSelectorMap: Map<String, String>

    private val activitySelectorOptions = arrayOf("Fumigacion", "Produccion", "Cosecha")
    private val activitySelectorMap = mapOf(
        "Fumigacion" to "1706",
        "Produccion" to "4457",
        "Cosecha" to "0160"
    )

    @Inject
    lateinit var recordsRepository: RecordsRepository
    private lateinit var sessionPrefs: SharedPreferences

    companion object {
        private const val SESSION_PREFERENCES_KEY = "session_prefs"
        private const val LOGGED_USER_KEY = "logged_user"

        private const val REQUEST_CODE_CAMERA_LITROS = 100 // Para el OCR de litros
        private const val REQUEST_CODE_CAMERA_FIXED_ASSET = 101 // Para la foto del activo fijo
        private const val REQUEST_CODE_CAMERA_PUMP = 102 // Para la foto de la bomba
    }

    // Instancia del reconocedor de texto de ML Kit
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // *** NUEVOS LAUNCHERS SEPARADOS ***
    private lateinit var takePictureForLitrosLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePictureForFixedAssetLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePictureForPumpLauncher: ActivityResultLauncher<Intent>

    // Launcher para solicitar el permiso de cámara
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private var pendingCameraAction: CameraAction? = null // Para guardar la acción pendiente de la cámara

    enum class CameraAction {
        LITROS, FIXED_ASSET, PUMP
    }

    ////////////////////////////////////////////////////

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        // ¡Asegúrate de que esta línea esté aquí!
        initActivityResultLaunchers()
        // updatePhotoButtonsState() // Asegurarse de que los botones estén deshabilitados al inicio
        // updateSaveButtonState() // Asegurarse de que el botón de guardar esté deshabilitado al inicio
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFuelBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUI() {
        initUIState()
        //initListeners()

        val currentDate = LocalDate.now()
        val weekOfYear = currentDate.get(
            java.time.temporal.WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()
        )
        val week = weekOfYear.toString().padStart(2, '0')
        binding.etFuelRegister.setText(currentDate.toString())
        binding.etWeek.setText(week)

        binding.etLiters.filters = arrayOf(DecimalDigitsInputFilter(9, 2))
        fuelViewModel.getFieldsData()

        // --- CAMBIOS AQUÍ: HACER VISIBLE PERO DESHABILITAR INICIALMENTE ---
        binding.btnFixedAssetPhoto.visibility = View.VISIBLE // Siempre visible
        binding.btnFixedAssetPhoto.isEnabled = false // Deshabilitado inicialmente

        binding.btnPumpPhoto.visibility = View.VISIBLE // Siempre visible
        binding.btnPumpPhoto.isEnabled = false // Deshabilitado inicialmente

        binding.ivPhotoPreview.visibility = View.VISIBLE // Siempre visible
        binding.ivPhotoPreview.setImageResource(R.drawable.ic_camera_fuel) // Placeholder inicial
        binding.ivPhotoPreview.alpha = 0.5f // Opcional: Atenuar la imagen para indicar que está "inactiva"

        binding.llSubfolderName.visibility = View.VISIBLE // Siempre visible
        binding.llSubfolderName.isEnabled = false // Deshabilitado inicialmente
        binding.etSubfolderName.text?.clear() // Asegurar que esté vacío

        binding.btnUploadPhoto.visibility = View.VISIBLE // Siempre visible
        binding.btnUploadPhoto.isEnabled = false // Deshabilitado inicialmente

        binding.btnScanSubfolder.visibility = View.VISIBLE // Siempre visible
        binding.btnScanSubfolder.isEnabled = false // Deshabilitado inicialmente

        binding.photoProgressBar.visibility = View.GONE // La barra de progreso sigue siendo GONE hasta que se use
        binding.tvPhotoUploadTitle.text = "Fotos requeridas: 2" // Estado inicial del texto

        updatePhotoButtonsColors() // <-- NUEVO: Establece el color inicial de los botones a verde

        initListeners()
        checkFormCompletion()
    }

    // ** NUEVA FUNCIÓN: Actualiza los colores de los botones de foto **
    private fun updatePhotoButtonsColors() {
        val greenColor = ContextCompat.getColor(requireContext(), R.color.fuel_button_green)
        val redColor = ContextCompat.getColor(requireContext(), R.color.fuel_button_red)
        val textColor = ContextCompat.getColor(requireContext(), R.color.fuel_button_text_color)
        val disabledGrey = ContextCompat.getColor(requireContext(), R.color.gray_button_disabled) // Si la definiste, úsala


        // Actualizar el botón de Activo Fijo
        if (fixedAssetPhotoUploaded) {
            binding.btnFixedAssetPhoto.setBackgroundColor(redColor)
            binding.btnFixedAssetPhoto.setTextColor(textColor)
        } else {
            binding.btnFixedAssetPhoto.setBackgroundColor(greenColor)
            binding.btnFixedAssetPhoto.setTextColor(textColor)
        }
        // Opcional: para un color distinto cuando está deshabilitado
        if (!binding.btnFixedAssetPhoto.isEnabled) {
            binding.btnFixedAssetPhoto.setBackgroundColor(disabledGrey) // Usar un gris si quieres.
            binding.btnFixedAssetPhoto.setTextColor(textColor) // O un color de texto más oscuro si es necesario
        }


        // Actualizar el botón de Bomba
        if (pumpPhotoUploaded) {
            binding.btnPumpPhoto.setBackgroundColor(redColor)
            binding.btnPumpPhoto.setTextColor(textColor)
        } else {
            binding.btnPumpPhoto.setBackgroundColor(greenColor)
            binding.btnPumpPhoto.setTextColor(textColor)
        }
        // Opcional: para un color distinto cuando está deshabilitado
        if (!binding.btnPumpPhoto.isEnabled) {
            binding.btnPumpPhoto.setBackgroundColor(disabledGrey) // Usar un gris si quieres.
            binding.btnPumpPhoto.setTextColor(textColor) // O un color de texto más oscuro si es necesario
        }
    }

    // Inicializar los ActivityResultLaunchers
    private fun initActivityResultLaunchers() {
        takePictureForLitrosLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // --- CAMBIO AQUÍ ---
                currentPhotoUri?.let { uri ->
                    val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
                    bitmap?.let { processImageForLitros(it) } ?: showToast("No se pudo cargar la imagen para litros.")
                    // Opcional: Elimina el archivo temporal después de usarlo
                    requireContext().contentResolver.delete(uri, null, null)
                } ?: showToast("URI de imagen no disponible para litros.")
                // --- FIN CAMBIO ---
            } else {
                showToast("Captura de imagen de litros cancelada.")
            }
            pendingCameraAction = null
            currentPhotoUri = null // Limpia el URI después de usarlo
        }

        takePictureForFixedAssetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                currentPhotoUri?.let { uri ->
                    try {
                        val originalInputStream = requireContext().contentResolver.openInputStream(uri)
                        val originalBitmap = BitmapFactory.decodeStream(originalInputStream)
                        originalInputStream?.close()

                        if (originalBitmap != null) {
                            // --- VALORES RECOMENDADOS PARA CLARIDAD SIN EXCESO DE RESOLUCIÓN ---
                            val MAX_WIDTH = 1200 // Por ejemplo, 1200 píxeles de ancho máximo
                            val MAX_HEIGHT = 1200 // Por ejemplo, 1200 píxeles de alto máximo
                            val QUALITY = 85 // Calidad JPEG (0-100), 85 es un buen equilibrio entre tamaño y calidad

                            val newBitmap: Bitmap
                            if (originalBitmap.width > MAX_WIDTH || originalBitmap.height > MAX_HEIGHT) {
                                val ratio = Math.min(
                                    MAX_WIDTH.toFloat() / originalBitmap.width,
                                    MAX_HEIGHT.toFloat() / originalBitmap.height
                                )
                                val newWidth = (originalBitmap.width * ratio).toInt()
                                val newHeight = (originalBitmap.height * ratio).toInt()
                                newBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                                originalBitmap.recycle() // Libera la memoria del bitmap original
                            } else {
                                newBitmap = originalBitmap // No necesita escalar si ya es más pequeña o igual
                            }

                            val outputStream = requireContext().contentResolver.openOutputStream(uri)
                            if (outputStream != null) {
                                newBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
                            } else {
                                // Manejar el caso donde no se pudo abrir el OutputStream
                                showToast("Error: No se pudo abrir el flujo de salida para guardar la imagen procesada.")
                            }
                            outputStream?.close()

                            lastCapturedBitmap = newBitmap
                            binding.ivPhotoPreview.setImageBitmap(newBitmap)
                            binding.ivPhotoPreview.alpha = 1.0f
                            binding.btnUploadPhoto.isEnabled = binding.etSubfolderName.text.toString().isNotEmpty()
                            binding.llSubfolderName.isEnabled = true
                            binding.btnScanSubfolder.isEnabled = true
                            binding.etSubfolderName.requestFocus()
                            currentPhotoTypeToUpload = PhotoType.FIXED_ASSET

                        } else {
                            showToast("No se pudo cargar el Bitmap original de la foto de Activo Fijo.")
                        }
                    } catch (ex: IOException) {
                        showToast("Error al procesar la imagen: ${ex.message}")
                    }
                } ?: showToast("URI de imagen no disponible para Activo Fijo.")
            } else {
                showToast("Captura de foto de Activo Fijo cancelada.")
                resetPhotoUIStateForCaptureFailure() // Asegúrate de que esta función limpie el URI si la captura se cancela
            }
            pendingCameraAction = null
            currentPhotoUri = null // Limpia el URI después de usarlo
            checkFormCompletion()
        }

        takePictureForPumpLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // --- CAMBIO AQUÍ ---
                currentPhotoUri?.let { uri ->
                    val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
                    if (bitmap != null) {
                        lastCapturedBitmap = bitmap
                        binding.ivPhotoPreview.setImageBitmap(bitmap)
                        binding.ivPhotoPreview.alpha = 1.0f
                        binding.btnUploadPhoto.isEnabled = binding.etSubfolderName.text.toString().isNotEmpty()
                        binding.llSubfolderName.isEnabled = true
                        binding.btnScanSubfolder.isEnabled = true
                        binding.etSubfolderName.requestFocus()
                        currentPhotoTypeToUpload = PhotoType.PUMP
                        // Opcional: Elimina el archivo temporal después de usarlo
                        requireContext().contentResolver.delete(uri, null, null)
                    } else {
                        showToast("No se pudo cargar la foto de la Bomba.")
                    }
                } ?: showToast("URI de imagen no disponible para Bomba.")
                // --- FIN CAMBIO ---
            } else {
                showToast("Captura de foto de Bomba cancelada.")
                resetPhotoUIStateForCaptureFailure() // Asegúrate de que esta función limpie el URI si la captura se cancela
            }
            pendingCameraAction = null
            currentPhotoUri = null // Limpia el URI después de usarlo
            checkFormCompletion()
        }

        requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pendingCameraAction?.let { action ->
                    dispatchTakePictureIntent(action)
                } ?: showToast("No hay acción de cámara pendiente después de conceder permiso.")
            } else {
                showToast("Permiso de cámara denegado. No se puede tomar fotos.")
            }
            pendingCameraAction = null
        }
    }

    private fun checkCameraPermissionAndDispatchIntent(action: CameraAction) {
        context?.let {
            pendingCameraAction = action // Guardar la acción pendiente
            if (ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent(action)
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        } ?: showToast("Error: Contexto no disponible para verificar permisos.")
    }

    // Función modificada para despachar la intención al launcher adecuado
    private fun dispatchTakePictureIntent(action: CameraAction) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // --- INICIO DEL CAMBIO CLAVE ---
            // Crea un archivo temporal para guardar la imagen de alta resolución
            val photoFile: File? = try {
                createImageFile() // Función auxiliar para crear el archivo
            } catch (ex: IOException) {
                showToast("Error al crear archivo de imagen: ${ex.message}")
                null
            }

            // Continúa solo si el archivo fue creado exitosamente
            photoFile?.also {
                // Obtiene un URI para el archivo usando FileProvider (seguro y requerido en Android 7+)
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider", // Debe coincidir con el 'authorities' en tu AndroidManifest.xml
                    it
                )
                currentPhotoUri = photoURI // Guarda el URI para usarlo después
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI) // <-- ¡ESTO ES CLAVE!

                // Lanza la intención con el launcher adecuado
                when (action) {
                    CameraAction.LITROS -> takePictureForLitrosLauncher.launch(takePictureIntent)
                    CameraAction.FIXED_ASSET -> takePictureForFixedAssetLauncher.launch(takePictureIntent)
                    CameraAction.PUMP -> takePictureForPumpLauncher.launch(takePictureIntent)
                }
            }
            // --- FIN DEL CAMBIO CLAVE ---
        } else {
            showToast("No se encontró una aplicación de cámara.")
            pendingCameraAction = null // Resetear en caso de error
        }
    }

    // --- NUEVA FUNCIÓN AUXILIAR: Para crear un archivo temporal para la imagen ---
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Crea un nombre de archivo único usando un timestamp
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefijo */
            ".jpg", /* sufijo */
            storageDir /* directorio */
        )
    }

    private fun processImageForLitros(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                //val extractedText = visionText.text
                var extractedText = visionText.text // Cambiado a 'var' para poder modificarlo

                // --- INICIO DE LAS MEJORAS: Sustituciones de caracteres ---
                // Puedes añadir más sustituciones aquí si identificas otros errores comunes
                extractedText = extractedText.replace("O", "0", ignoreCase = true) // 'O' mayúscula por '0'
                    .replace("I", "1") // 'I' mayúscula por '1'
                    .replace("l", "1") // 'l' minúscula por '1'
                    .replace("S", "5", ignoreCase = true) // 'S' por '5' (común en displays)
                    .replace("B", "8", ignoreCase = true) // 'B' por '8' (común en displays)
                    .replace("g", "9", ignoreCase = true) // 'g' por '9'
                    .replace("D", "0", ignoreCase = true) // 'D' por '0'
                    .replace("Z", "2", ignoreCase = true) // 'Z' por '2'
                // --- FIN DE LAS MEJORAS ---

                // Ahora, aplica el filtro de dígitos y puntos/comas al texto ya corregido
                val digitsAndDots = extractedText.filter { it.isDigit() || it == '.' || it == ',' }
                    .replace(',', '.') // Reemplaza comas por puntos si esperas decimales

                val validLitros = if (digitsAndDots.count { it == '.' } > 1) {
                    // Si hay múltiples puntos, toma solo la parte antes del último, o refina la lógica según tus datos
                    // Esto es una medida de seguridad, pero con las sustituciones, debería ser menos común.
                    digitsAndDots.substringBeforeLast('.')
                } else {
                    digitsAndDots
                }

                if (validLitros.isNotEmpty()) {
                    binding.etLiters.setText(validLitros)
                    showToast("Litros escaneados: $validLitros") // Corregido 'Listros' a 'Litros'
                } else {
                    binding.etLiters.setText("") // Limpia el campo si no hay números
                    showToast("No se detectaron números para registrar litros.") // Corregido 'listros' a 'litros'
                }
            }
            .addOnFailureListener { e ->
                showToast("Error al reconocer texto: ${e.message}")
                binding.etLiters.setText("") // Limpia el campo en caso de error
            }
    }
    //////////////////////////////////////////////////////////

    private fun initUIState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                fuelViewModel.state.collect {
                    when (it) {
                        FuelState.Loading -> loadingState()
                        is FuelState.SuccessFixedAsset -> fixedAssetsState(it)
                        is FuelState.SuccessWorker -> workerState(it)
                        is FuelState.SuccessField -> fieldState(it)
                        is FuelState.Error -> errorState(it)
                    }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListeners() {
        binding.btnSelectFuel.setOnClickListener {
            showFuelSelector()
            // No es necesario llamar checkFormCompletion() aquí si ya lo haces en showFuelSelector() al seleccionar el valor.
        }
        binding.etFuelRegister.setOnClickListener {
            showDatePicker()
            // No es necesario llamar checkFormCompletion() aquí si ya lo haces en showDatePicker() al seleccionar la fecha.
        }
        binding.btnFixedAsset.setOnClickListener { startScannerForFixedAsset() }

        binding.btnScanSubfolder.setOnClickListener { startScannerForFolder() }

        // Botón para activar el OCR de Litros
        /*binding.btnLitroCargado.setOnClickListener {
            checkCameraPermissionAndDispatchIntent(CameraAction.LITROS)
        }*/

        // Botón para tomar la foto del ACTIVO FIJO
        binding.btnFixedAssetPhoto.setOnClickListener {
            if (fixedAssetPhotoUploaded) {
                showToast("La foto del Activo Fijo ya fue subida.")
                return@setOnClickListener
            }
            checkCameraPermissionAndDispatchIntent(CameraAction.FIXED_ASSET)
        }

        // Botón para tomar la foto de la BOMBA
        binding.btnPumpPhoto.setOnClickListener {
            if (pumpPhotoUploaded) {
                showToast("La foto de la Bomba ya fue subida.")
                return@setOnClickListener
            }
            checkCameraPermissionAndDispatchIntent(CameraAction.PUMP)
        }

        binding.btnUploadPhoto.setOnClickListener {
            val subfolderName = binding.etSubfolderName.text.toString().trim()
            if (subfolderName.isEmpty()) {
                showToast("Por favor, ingresa el código de subcarpeta (ej. 'ASP-00658').")
                return@setOnClickListener
            }
            lastCapturedBitmap?.let { bitmap ->
                currentPhotoTypeToUpload?.let { photoType ->
                    uploadPhotoToServer(bitmap, subfolderName, photoType)
                } ?: showToast("Error: Tipo de foto no definido para subir.")
            } ?: showToast("No hay foto para subir. Toma una primero.")
        }

        // --- Creador de TextWatcher genérico para la validación del formulario ---
        // Este se usará para la mayoría de los campos de texto
        val formFieldTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                checkFormCompletion() // <-- ¡Se llama a checkFormCompletion aquí!
            }
        }

        // --- Aplicar el TextWatcher genérico a TODOS los campos relevantes del formulario ---
        // NO apliques a los campos que tienen una lógica de TextWatcher específica (como etCodAfi, etCodTra)
        // a menos que modifiques esos TextWatchers para incluir esta llamada.
        binding.etWeek.addTextChangedListener(formFieldTextWatcher)
        // binding.etCodAfi.addTextChangedListener(formFieldTextWatcher) - No aquí, tiene su propio listener
        binding.etAfiName.addTextChangedListener(formFieldTextWatcher)
        binding.etKm.addTextChangedListener(formFieldTextWatcher)
        // binding.etCodTra.addTextChangedListener(formFieldTextWatcher) - No aquí, tiene su propio listener
        binding.etNomOpe.addTextChangedListener(formFieldTextWatcher)
        binding.etFuelType.addTextChangedListener(formFieldTextWatcher) // Si etFuelType es editable manualmente
        binding.etLiters.addTextChangedListener(formFieldTextWatcher)
        binding.etField.addTextChangedListener(formFieldTextWatcher) // Si etField es editable manualmente
        binding.etActivity.addTextChangedListener(formFieldTextWatcher) // Si etActivity es editable manualmente
        binding.etPrecio.addTextChangedListener(formFieldTextWatcher)

        // *** IMPORTANTE: MODIFICAR TextWatcher EXISTENTES para etCodAfi y etCodTra ***
        binding.etCodAfi.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (binding.etCodAfi.text.toString().length >= 9) {
                    fuelViewModel.getFixedAssetData(binding.etCodAfi.text.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                checkFormCompletion() // <-- ¡AÑADIDO! Llama a checkFormCompletion aquí.
            }
        })

        binding.btnWorkerCode.setOnClickListener {
            startScannerForWorker()
        }

        binding.etCodTra.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (binding.etCodTra.text.toString().length >= 5) {
                    fuelViewModel.getWorkerData(binding.etCodTra.text.toString())
                    isWorkerScanned = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                checkFormCompletion() // <-- ¡AÑADIDO! Llama a checkFormCompletion aquí.
            }
        })

        // TextWatcher para etSubfolderName, ya que es crucial para habilitar btnUploadPhoto
        binding.etSubfolderName.addTextChangedListener(formFieldTextWatcher) // Ya estaba cubierta por formFieldTextWatcher

        binding.btnSelectField.setOnClickListener {
            showFieldSelector()
            // No es necesario llamar checkFormCompletion() aquí si ya lo haces en showFieldSelector() al seleccionar el valor.
        }

        binding.btnSelectActivity.setOnClickListener {
            showActivitySelector()
            // No es necesario llamar checkFormCompletion() aquí si ya lo haces en showActivitySelector() al seleccionar el valor.
        }

        binding.btnSave.setOnClickListener {
            if (validateFormForSave()) {
                var formattedDate = binding.etFuelRegister.text.toString()
                formattedDate += "T" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                if (fuelValue.isEmpty() || fieldValue.isEmpty() || fieldZoneValue.isEmpty() || activityValue.isEmpty()) {
                    showToast("Por favor, selecciona un valor para Combustible, Campo y Actividad.")
                    return@setOnClickListener // Salir de la función si hay campos sin seleccionar
                }

                val weekNumber = binding.etWeek.text.toString()
                val fixedAssetCode = binding.etCodAfi.text.toString()
                val fixedAssetName = binding.etAfiName.text.toString()
                val odometer = binding.etKm.text.toString()
                val workerCode = binding.etCodTra.text.toString()
                val workerName = binding.etNomOpe.text.toString()
                val automatic = if (!isWorkerScanned) 0 else 1
                val combustible = fuelValue
                val combustibleName = binding.etFuelType.text.toString()
                val liters = binding.etLiters.text.toString()
                val field = fieldValue
                val fieldName = binding.etField.text.toString()
                val activity = activityValue
                val activityName = binding.etActivity.text.toString()
                val zone = fieldZoneValue
                val nPrecioCom = binding.etPrecio.text.toString()


                if (
                    formattedDate.isEmpty() ||
                    weekNumber.isEmpty() ||
                    fixedAssetCode.isEmpty() ||
                    fixedAssetName.isEmpty() ||
                    odometer.isEmpty() ||
                    workerCode.isEmpty() ||
                    workerName.isEmpty() ||
                    combustible.isEmpty() ||
                    combustibleName.isEmpty() ||
                    liters.isEmpty() ||
                    field.isEmpty() ||
                    fieldName.isEmpty() ||
                    activity.isEmpty() ||
                    activityName.isEmpty() ||
                    zone.isEmpty() ||
                    nPrecioCom.isEmpty()
                ) {
                    showToast("Algun campo esta vacio, vuelve a intentarlo, por favor.")
                } else {
                    //Hacemos el guardado
                    saveData(
                        formattedDate,
                        weekNumber,
                        fixedAssetCode,
                        fixedAssetName,
                        odometer,
                        workerCode,
                        workerName,
                        automatic,
                        combustible,
                        combustibleName,
                        liters,
                        field,
                        fieldName,
                        activity,
                        activityName,
                        zone,
                        nPrecioCom
                    )
                }
            }
        }
    }

    private fun createCheckCompletionTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                checkFormCompletion() // Cada cambio de texto reevalúa el estado de los botones de foto
            }
        }
    }

    // **NUEVA Función auxiliar para resetear el estado de la UI de la foto cuando se cancela o falla una captura**
// Mantiene la visibilidad pero deshabilita y limpia el contenido
    private fun resetPhotoUIStateForCaptureFailure() {
        lastCapturedBitmap = null
        currentPhotoTypeToUpload = null
        currentPhotoUri = null // <-- ¡AÑADE ESTA LÍNEA AQUÍ TAMBIÉN!
        binding.ivPhotoPreview.setImageResource(R.drawable.ic_camera_fuel)
        binding.ivPhotoPreview.alpha = 0.5f
        binding.btnUploadPhoto.isEnabled = false
        binding.llSubfolderName.isEnabled = false
        binding.btnScanSubfolder.isEnabled = false // Corregido: estaba faltando el = false
        binding.etSubfolderName.text?.clear()
    }

    // Esta función maneja la habilitación general de los controles basándose en si los campos están llenos
    private fun checkFormCompletion() {

        val allFormFieldsFilled =
            binding.etFuelRegister.text.toString().isNotEmpty() &&
                    binding.etWeek.text.toString().isNotEmpty() &&
                    binding.etCodAfi.text.toString().isNotEmpty() &&
                    binding.etAfiName.text.toString().isNotEmpty() && // Asumo que se llena automáticamente
                    binding.etKm.text.toString().isNotEmpty() &&
                    binding.etCodTra.text.toString().isNotEmpty() &&
                    binding.etNomOpe.text.toString().isNotEmpty() && // Asumo que se llena automáticamente
                    binding.etFuelType.text.toString().isNotEmpty() && // Campo de selección (texto visible)
                    fuelValue.isNotEmpty() && // Campo de selección (valor interno)
                    binding.etLiters.text.toString().isNotEmpty() &&
                    binding.etField.text.toString().isNotEmpty() && // Campo de selección (texto visible)
                    fieldValue.isNotEmpty() && // Campo de selección (valor interno)
                    fieldZoneValue.isNotEmpty() && // Campo de selección (valor interno)
                    binding.etActivity.text.toString().isNotEmpty() && // Campo de selección (texto visible)
                    activityValue.isNotEmpty() && // Campo de selección (valor interno)
                    binding.etPrecio.text.toString().isNotEmpty()
        Log.d("FuelFragment", "All form fields filled: $allFormFieldsFilled")
        // Lógica de habilitación/visibilidad para los botones de cámara (Activo Fijo, Bomba)
        // Lógica de habilitación/deshabilitación para los botones de cámara (Activo Fijo, Bomba)
        if (allFormFieldsFilled) {
            binding.btnFixedAssetPhoto.isEnabled = !fixedAssetPhotoUploaded
            binding.btnPumpPhoto.isEnabled = fixedAssetPhotoUploaded && !pumpPhotoUploaded
            updatePhotoButtonsColors()
        } else {
            // Si el formulario no está completo, deshabilitar TODOS los botones de foto
            binding.btnFixedAssetPhoto.isEnabled = false
            binding.btnPumpPhoto.isEnabled = false

            // Además, si el formulario se "incompleta" después de haber tomado una foto,
            // resetear también el estado de la UI de subida de foto.
            resetPhotoUIStateForCaptureFailure()

            // También resetear el estado de las fotos (contadores y flags)
            photoCounter = 0
            fixedAssetPhotoUploaded = false
            pumpPhotoUploaded = false
        }

        // Control de habilitación del botón de subir foto y el campo de subcarpeta
        // Esto se activa solo si hay un bitmap capturado y el campo de subcarpeta tiene texto
        binding.llSubfolderName.isEnabled = (lastCapturedBitmap != null)
        binding.btnUploadPhoto.isEnabled = (lastCapturedBitmap != null) && binding.etSubfolderName.text.toString().isNotEmpty()


        updatePhotoButtonsState()
    }

    private fun updatePhotoButtonsState() {
        val photosNeeded = 2 - photoCounter
        if (photosNeeded > 0) {
            binding.tvPhotoUploadTitle.text = "Fotos requeridas: ${photosNeeded} restante(s)"
        } else {
            binding.tvPhotoUploadTitle.text = "Fotos requeridas: 2/2 completas"
        }

        // La habilitación inicial de los botones de foto ya se maneja en checkFormCompletion
        // Aquí solo aseguramos que si ya se subió una foto, su botón esté deshabilitado.
        binding.btnFixedAssetPhoto.isEnabled = binding.btnFixedAssetPhoto.isEnabled && !fixedAssetPhotoUploaded
        binding.btnPumpPhoto.isEnabled = binding.btnPumpPhoto.isEnabled && !pumpPhotoUploaded
    }

    private fun uploadPhotoToServer(bitmap: Bitmap, subfolderName: String, photoType: PhotoType) {
        binding.btnFixedAssetPhoto.isEnabled = false // Deshabilitar mientras se sube
        binding.btnPumpPhoto.isEnabled = false
        binding.btnUploadPhoto.isEnabled = false
        binding.llSubfolderName.isEnabled = false // También deshabilitar el campo de subcarpeta
        binding.btnScanSubfolder.isEnabled = false
        binding.photoProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // **PASO 1: Preparar la imagen para el envío**
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) // Comprime el Bitmap a JPEG con 80% de calidad.
                val byteArray = stream.toByteArray()
                stream.close() // ¡Importante! Cierra el stream para liberar recursos.

                val requestFile = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)

                // Define un nombre de archivo. Usar un timestamp y el tipo de foto ayuda a la unicidad.
                val fileName = "${System.currentTimeMillis()}_${photoType.name.lowercase(Locale.ROOT)}.jpeg"

                // Crea la parte MultipartBody.Part para el archivo.
                // "photo" debe coincidir EXACTAMENTE con el nombre del parámetro que tu API espera para el archivo.
                // Si tu API espera "image", "file", etc., cámbialo aquí.
                val photoPart = MultipartBody.Part.createFormData("file", fileName, requestFile)


                // **PASO 2: Realizar la llamada a la API a través del ViewModel**
                Log.d("UploadPhoto", "Iniciando subida para subcarpeta: $subfolderName, tipo: $photoType")

                // *** ¡LLAMADA AL VIEWMODEL AQUI! ***
                val response = fuelViewModel.uploadPhotoToApi(subfolderName, photoPart)

                // **PASO 3: Manejar la respuesta de la API**
                if (response.isSuccessful) {
                    val uploadResponse = response.body()
                    // Verifica que la respuesta no sea nula y que el mensaje de la API sea "ok"
                    if (uploadResponse != null && uploadResponse.message == "OK") {
                        when (photoType) {
                            PhotoType.FIXED_ASSET -> {
                                fixedAssetPhotoUploaded = true
                                showToast("Foto de Activo Fijo subida exitosamente!")
                                Log.d("UploadPhoto", "Activo Fijo subido exitosamente con mensaje: ${uploadResponse.message}")
                            }
                            PhotoType.PUMP -> {
                                pumpPhotoUploaded = true
                                showToast("Foto de Bomba subida exitosamente!")
                                Log.d("UploadPhoto", "Bomba subida exitosamente con mensaje: ${uploadResponse.message}")
                                // 🛑 SOLUCIÓN: Deshabilitar el botón inmediatamente después del éxito
                                binding.btnUploadPhoto.isEnabled = false

                                // 🧹 Limpiar el estado de la foto pendiente para evitar re-subidas
                                lastCapturedBitmap = null
                                currentPhotoTypeToUpload = null
                            }
                        }
                        // Actualiza el contador de fotos subidas
                        photoCounter = (if (fixedAssetPhotoUploaded) 1 else 0) + (if (pumpPhotoUploaded) 1 else 0)

                    } else {
                        // La API respondió con éxito HTTP, pero el "message" no es "ok" o la respuesta es nula
                        val errorMessage = uploadResponse?.message ?: "Respuesta de API inesperada (mensaje no 'ok')."
                        showToast("Error de API: $errorMessage")
                        //Log.e("UploadPhoto", "Error de API (código ${response.code()}): $errorMessage"
                                Log.e("UploadPhoto", "Error de subida HTTP: $errorMessage") // <-- ¡Este es el importante!

                    }
                } else {
                    // La solicitud HTTP no fue exitosa (ej. 404, 500, etc.)
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Error HTTP ${response.code()}: ${response.message()}. Cuerpo: $errorBody"
                    showToast("Error de subida: ${response.code()} - ${response.message()}")
                    Log.e("UploadPhoto", "Error de subida HTTP: $errorMessage")
                }

            } catch (e: Exception) {
                // Manejo de excepciones de red o de otro tipo (ej. sin conexión, JSON malformado)
                showToast("Error en la subida: ${e.message}")
                Log.e("UploadPhoto", "Excepción al subir foto", e)
            } finally {
                // Este bloque se ejecuta siempre, haya éxito o error.
                binding.photoProgressBar.visibility = View.GONE // Oculta la barra de progreso
                checkFormCompletion() // Re-evalúa el estado de habilitación/visibilidad de los botones de cámara
                updatePhotoButtonsColors() // Asegura que los colores de los botones se actualicen (ej. rojo si se subió)
            }
        }
    }

    // Función de validación centralizada
    private fun validateFormForSave(): Boolean {
        // Obtener los valores de los campos directamente para la validación
        val formattedDate = binding.etFuelRegister.text.toString()
        val weekNumber = binding.etWeek.text.toString()
        val fixedAssetCode = binding.etCodAfi.text.toString()
        val fixedAssetName = binding.etAfiName.text.toString()
        val odometer = binding.etKm.text.toString()
        val workerCode = binding.etCodTra.text.toString()
        val workerName = binding.etNomOpe.text.toString()
        // No necesitamos 'automatic' para la validación de vacío
        val combustible = fuelValue
        val combustibleName = binding.etFuelType.text.toString()
        val liters = binding.etLiters.text.toString()
        val field = fieldValue
        val fieldName = binding.etField.text.toString()
        val activity = activityValue
        val activityName = binding.etActivity.text.toString()
        val zone = fieldZoneValue
        val nPrecioCom = binding.etPrecio.text.toString()


        // **TU LÓGICA DE VALIDACIÓN DE CAMPOS VACÍOS**
        if (
            formattedDate.isEmpty() ||
            weekNumber.isEmpty() ||
            fixedAssetCode.isEmpty() ||
            fixedAssetName.isEmpty() ||
            odometer.isEmpty() ||
            workerCode.isEmpty() ||
            workerName.isEmpty() ||
            combustible.isEmpty() ||
            combustibleName.isEmpty() ||
            liters.isEmpty() ||
            field.isEmpty() ||
            fieldName.isEmpty() ||
            activity.isEmpty() ||
            activityName.isEmpty() ||
            zone.isEmpty() ||
            nPrecioCom.isEmpty()
        ) {
            showToast("Existen campos vacios, vuelve a intentarlo, por favor.")
            return false // Si algún campo está vacío, detenemos la validación aquí.
        }

        // **VALIDACIÓN DE FOTOS REQUERIDAS**
        if (!fixedAssetPhotoUploaded || !pumpPhotoUploaded) {
            val missingPhotos = mutableListOf<String>()
            if (!fixedAssetPhotoUploaded) missingPhotos.add("Foto de Activo Fijo")
            if (!pumpPhotoUploaded) missingPhotos.add("Foto de Bomba")

            showToast("Toma las siguientes fotos: ${missingPhotos.joinToString(", ")}.")
            return false // Si faltan fotos, detenemos la validación.
        }

        // Si todas las validaciones anteriores pasaron, el formulario es válido.
        return true
    }

    private fun saveData(
        date: String,
        weekNumber: String,
        fixedAssetCode: String,
        fixedAssetName: String,
        odometer: String,
        workerCode: String,
        workerName: String,
        automatic: Int,
        combustible: String,
        combustibleName: String,
        liters: String,
        field: String,
        fieldName: String,
        activity: String,
        activityName: String,
        zoneCode: String,
        nPrecioCom: String
    ) {
        sessionPrefs = requireActivity().getSharedPreferences(
            SESSION_PREFERENCES_KEY,
            AppCompatActivity.MODE_PRIVATE
        )
        val user = sessionPrefs.getString(LOGGED_USER_KEY, "FCASTELLANOS") ?: "usuario_desconocido"

        //Data for insert
        val cControlCom = 0L
        val cCodigoUsu = user.toString().trim().uppercase()
        val isSynced = 0

        //Data of record
        val fuelRegister = RecordModel(
            cControlCom,
            date,
            weekNumber,
            fixedAssetCode,
            fixedAssetName,
            odometer,
            workerCode,
            workerName,
            automatic,
            combustible,
            combustibleName,
            liters,
            field,
            fieldName,
            activity,
            activityName,
            zoneCode,
            cCodigoUsu,
            isSynced,
            nPrecioCom
        )
        Log.d("AppRun", "fuelRegister: $fuelRegister") // <--- ¡AÑADE ESTO!

        lifecycleScope.launch {
            try {
                recordsRepository.insertFuelRegister(fuelRegister)
                showToast("Registro guardado correctamente")
                //binding.dpFuelRegister.updateDate(2025, 0, 1)
                //binding.etWeek.text!!.clear()
                binding.etCodAfi.text!!.clear()
                binding.etAfiName.text!!.clear()
                binding.etKm.text!!.clear()
                binding.etCodTra.text!!.clear()
                binding.etNomOpe.text!!.clear()
                isWorkerScanned = false
                binding.etFuelType.text!!.clear()
                fuelValue = fuelValue.trim().takeIf { it.isNotEmpty() } ?: ""
                binding.etLiters.text!!.clear()
                binding.etField.text!!.clear()
                fieldValue = fieldValue.trim().takeIf { it.isNotEmpty() } ?: ""
                binding.etActivity.text!!.clear()
                fieldZoneValue = fieldZoneValue.trim().takeIf { it.isNotEmpty() } ?: ""
                activityValue = activityValue.trim().takeIf { it.isNotEmpty() } ?: ""
                binding.etPrecio.text!!.clear()

                // Resetear lógica de fotos
                photoCounter = 0
                fixedAssetPhotoUploaded = false
                pumpPhotoUploaded = false
                lastCapturedBitmap = null
                currentPhotoTypeToUpload = null
                binding.ivPhotoPreview.setImageResource(R.drawable.ic_camera_fuel)
                //binding.btnUploadPhoto.visibility = View.GONE
                binding.photoProgressBar.visibility = View.GONE
                binding.etSubfolderName.text?.clear()
                binding.llSubfolderName.isEnabled = false // Deshabilitar el campo de subcarpeta al resetear

                // Actualizar el estado de los botones después de resetear
                checkFormCompletion()
            } catch (e: Exception) {
                showToast("Error al guardar los datos: ${e.message}")
            }
        }
    }

    private fun startScannerForFixedAsset() {
        val scanIntent = Intent(requireContext(), CaptureActivity::class.java)
        scanIntent.putExtra("SCAN_MODE", "QR_CODE_MODE")
        barcodeLauncherForFixedAsset.launch(scanIntent)
    }

    private fun startScannerForFolder() {
        val scanIntent = Intent(requireContext(), CaptureActivity::class.java)
        scanIntent.putExtra("SCAN_MODE", "QR_CODE_MODE")
        qrScannerLauncher.launch(scanIntent)
    }

    private fun startScannerForWorker() {
        val scanIntent = Intent(requireContext(), CaptureActivity::class.java)
        scanIntent.putExtra("SCAN_MODE", "QR_CODE_MODE")
        barcodeLauncherForWorker.launch(scanIntent)
    }

    private val barcodeLauncherForFixedAsset =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val scannedData = result.data?.getStringExtra("SCAN_RESULT") ?: ""

                // 🛑 CAMBIO CLAVE 🛑
                // 1. Limpiar campos del trabajador en la UI.
                binding.etCodTra.text!!.clear()
                binding.etNomOpe.text!!.clear()
                isWorkerScanned = false

                binding.etCodAfi.setText(scannedData)
            }
        }

    private val barcodeLauncherForWorker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val scannedData = result.data?.getStringExtra("SCAN_RESULT") ?: ""
                binding.etCodTra.setText(scannedData)
                isWorkerScanned = true
            }
        }

    private val qrScannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedCode = result.data?.getStringExtra("SCAN_RESULT") // Asegúrate de que tu escáner devuelva el resultado en esta clave
            scannedCode?.let {
                binding.etSubfolderName.setText(it) // <--- ¡Esto es lo que inserta el código en el EditText!
            }
        } else {
            // Manejar caso de escaneo cancelado o fallido
            Toast.makeText(requireContext(), "Escaneo de QR cancelado o fallido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadingState() {

    }

    private fun fixedAssetsState(state: FuelState.SuccessFixedAsset) {
        val fixedAsset = state.successFixedAsset
        binding.etAfiName.setText(fixedAsset.nombreAfi)
        //binding.tilSubfolderName.setText(fixedAsset.vNombreAfi)
    }

    private fun workerState(state: FuelState.SuccessWorker) {
        val worker = state.successWorker
        val fullName =
            worker.vNombreTra + " " + worker.vApellidopatTra + " " + worker.vApellidomatTra
        binding.etNomOpe.setText(fullName)
    }

    private fun fieldState(state: FuelState.SuccessField) {
        val fields = state.successField
        fieldSelectorOptions = fields.map { it.vNombreCam }
        fieldSelectorMap = fields.associate { it.vNombreCam to it.cCodigoCam }
        fieldZoneSelectorMap = fields.associate { it.vNombreCam to it.cCodigoZon }
    }

    private fun errorState(state: FuelState.Error) {
        binding.pbView.visibility = View.GONE
        showToast(state.error)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialog,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.etFuelRegister.setText(dateFormat.format(selectedDate.time))

                val weekOfYear = selectedDate.get(Calendar.WEEK_OF_YEAR)
                val week = weekOfYear.toString().padStart(2, '0')
                binding.etWeek.setText(week)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    @SuppressLint("ResourceType")
    /*private fun showFuelSelector() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view =
            LayoutInflater.from(requireContext()).inflate(R.drawable.bottom_sheet_layout, null)
        val listView = view.findViewById<ListView>(R.id.fuelType)
        if (listView == null) {
            Log.e("FuelFragment", "Error: listViewFuel es null")
            return
        }

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, fuelSelectorOptions)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedLabel = fuelSelectorOptions[position]
            val selectedValue = fuelSelectorMap[selectedLabel]
            binding.etFuelType.setText(selectedLabel)
            //aqui se pasara el valor del campo seleccionado, por el momento se muestra un Toast
            fuelValue = selectedValue.toString()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }*/

    private fun showFuelSelector() {
        val options = fuelSelectorOptions
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Selecciona Tipo de Combustible")
            .setItems(options) { _, which ->
                val selectedFuel = options[which]
                binding.etFuelType.setText(selectedFuel)
                fuelValue = fuelSelectorMap[selectedFuel] ?: ""
                checkFormCompletion()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    @SuppressLint("ResourceType", "InflateParams")
    /*private fun showFieldSelector() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view =
            LayoutInflater.from(requireContext()).inflate(R.drawable.bottom_sheet_layout, null)
        val listView = view.findViewById<ListView>(R.id.fields)
        if (listView == null) {
            Log.e("FuelFragment", "Error: listViewFuel es null")
            return
        }

        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                fieldSelectorOptions
            )
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedLabel = fieldSelectorOptions[position]
            val selectedValue = fieldSelectorMap[selectedLabel]
            val zoneSelectedValue = fieldZoneSelectorMap[selectedLabel]
            binding.etField.setText(selectedLabel)
            fieldValue = selectedValue.toString()
            fieldZoneValue = zoneSelectedValue.toString()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }*/
    private fun showFieldSelector() {
        if (!::fieldSelectorOptions.isInitialized || fieldSelectorOptions.isEmpty()) {
            showToast("No hay ranchos (campos) disponibles. Intenta sincronizar datos.")
            return
        }
        val options = fieldSelectorOptions.toTypedArray()
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Selecciona el Rancho (Campo)")
            .setItems(options) { _, which ->
                val selectedField = options[which]
                binding.etField.setText(selectedField)
                fieldValue = fieldSelectorMap[selectedField] ?: ""
                fieldZoneValue = fieldZoneSelectorMap[selectedField] ?: ""
                checkFormCompletion()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    @SuppressLint("ResourceType", "InflateParams")
    /*private fun showActivitySelector() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view =
            LayoutInflater.from(requireContext()).inflate(R.drawable.bottom_sheet_layout, null)
        val listView = view.findViewById<ListView>(R.id.activity)
        if (listView == null) {
            Log.e("FuelFragment", "Error: listViewFuel es null")
            return
        }

        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                activitySelectorOptions
            )
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedLabel = activitySelectorOptions[position]
            val selectedValue = activitySelectorMap[selectedLabel]
            binding.etActivity.setText(selectedLabel)
            activityValue = selectedValue.toString()
            bottomSheetDialog.dismiss()
            checkFormCompletion() // <-- ¡AÑADE ESTA LÍNEA AQUÍ PARA QUE SE ACTIVEN LOS BOTONES!
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }*/
    private fun showActivitySelector() {
        val options = activitySelectorOptions
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Selecciona la Actividad")
            .setItems(options) { _, which ->
                val selectedActivity = options[which]
                binding.etActivity.setText(selectedActivity)
                activityValue = activitySelectorMap[selectedActivity] ?: ""
                checkFormCompletion()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun showToast(message: String) {
        if (isAdded) {
            try {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}