package com.agroberriesmx.combustiblesagroberries.ui.recordsdetail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.agroberriesmx.combustiblesagroberries.R
import com.agroberriesmx.combustiblesagroberries.databinding.ActivityListRecordDetailBinding
import com.agroberriesmx.combustiblesagroberries.domain.RecordsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListRecordDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListRecordDetailBinding
    private val listRecordDetailViewModel: ListRecordDetailViewModel by viewModels()

    @Inject
    lateinit var recordsRepository: RecordsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListRecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cControlCom = intent.getLongExtra("cControlCom", -1L)
        if (cControlCom != -1L) {
            listRecordDetailViewModel.getRecord(cControlCom)
        }

        observeViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeViewModel() {
        listRecordDetailViewModel.state.observe(this) { state ->
            when (state) {
                is ListRecordDetailState.Loading -> {

                }

                is ListRecordDetailState.Success -> {
                    binding.rdRecordNumber.text = state.cControlCom.toString()
                    binding.rdRecordDate.text = state.date
                    binding.rdWeekNumber.text = state.weekNumber
                    binding.rdFixedAssetCode.text = state.fixedAssetCode
                    binding.rdFixedAssetName.text = state.fixedAssetName
                    binding.rdOdometer.text = state.odometer
                    binding.rdWorkerCode.text = state.workerCode
                    binding.rdWorkerName.text = state.workerName
                    binding.rdCombustible.text = state.combustible
                    binding.rdCombustibleName.text = state.combustibleName
                    binding.rdLiters.text = state.liters
                    binding.precioCom.text = state.precioCom
                    binding.rdField.text = state.field
                    binding.rdFieldName.text = state.fieldName
                    binding.rdActivity.text = state.activity
                    binding.rdActivityName.text = state.activityName
                    binding.rdCCodigoUsu.text = state.cCodigoUsu
                    binding.rdIsSynced.text = if (state.isSynced == 0) "No" else "Si"
                }

                is ListRecordDetailState.Error -> {
                    Toast.makeText(this, state.error, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
}