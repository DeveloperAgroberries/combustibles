package com.agroberriesmx.combustiblesagroberries.ui.assetdetail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.agroberriesmx.combustiblesagroberries.R
import com.agroberriesmx.combustiblesagroberries.databinding.ActivityAssetDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssetDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAssetDetailBinding
    private val assetDetailViewModel: AssetDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAssetDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cNumeconAfi = intent.getStringExtra("cNumeconAfi")
        if (!cNumeconAfi.isNullOrEmpty()) {
            assetDetailViewModel.getAsset(cNumeconAfi.trim().uppercase())
        }

        observeViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeViewModel() {
        assetDetailViewModel.state.observe(this) { state ->
            when (state) {
                is AssetDetailState.Loading -> {

                }

                is AssetDetailState.Success -> {
                    /*binding.rdCodeAsset.text = state.cCodigoAfi
                    binding.rdNumberAsset.text = state.cNumeconAfi
                    binding.rdNameAsset.text = state.vNombreAfi
                    binding.rdSerialAsset.text = state.vNumserieAfi
                    binding.rdObservationsAsset.text = state.vObservacionAfi
                    binding.rdPlateAsset.text = state.vPlacasAfi
                    binding.rdKmAsset.text = state.nKmAfi*/
                    binding.rdNumberAsset.text = state.numecon
                    binding.rdNameAsset.text = state.nombreAfi
                    //binding.rdSerialAsset.text = state.vNumserieAfi
                    //binding.rdObservationsAsset.text = state.vObservacionAfi
                    binding.rdPlateAsset.text = state.placas
                    //binding.rdKmAsset.text = state.nKmAfi
                }

                is AssetDetailState.Error -> {
                    Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        }
    }
}