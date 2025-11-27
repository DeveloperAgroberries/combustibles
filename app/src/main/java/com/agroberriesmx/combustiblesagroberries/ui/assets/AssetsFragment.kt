package com.agroberriesmx.combustiblesagroberries.ui.assets

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.agroberriesmx.combustiblesagroberries.databinding.FragmentAssetsBinding
import com.agroberriesmx.combustiblesagroberries.ui.SharedViewModel
import com.agroberriesmx.combustiblesagroberries.ui.assetdetail.AssetDetailActivity
import com.agroberriesmx.combustiblesagroberries.ui.assets.adapter.AssetsAdapter
import com.journeyapps.barcodescanner.CaptureActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssetsFragment : Fragment() {
    private var _binding: FragmentAssetsBinding? = null
    private val binding get() = _binding!!
    private val assetsViewModel by viewModels<AssetsViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var assetsAdapter: AssetsAdapter
    private lateinit var searchView: SearchView

    private val getAssetDetailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
//            assetsViewModel.refreshAssets()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initObservers()
        setupSearchView()
    }

    private fun initUI() {
        initList()
        initListeners()
    }

    private fun initList() {
        assetsAdapter = AssetsAdapter(onItemSelected = { asset ->
            val intent = Intent(requireContext(), AssetDetailActivity::class.java)
            intent.putExtra("cNumeconAfi", asset.numecon)
            getAssetDetailResult.launch(intent)
        })

        binding.rvAssets.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = assetsAdapter
        }
    }

    private fun initListeners() {
        binding.btnAsset.setOnClickListener {
            startScannerForAssets()
        }
    }

    private fun startScannerForAssets() {
        val scanIntent = Intent(requireContext(), CaptureActivity::class.java)
        scanIntent.putExtra("SCAN_MODE", "QR_CODE_MODE")
        barcodeLauncherForAsset.launch(scanIntent)
    }

    private fun initObservers() {
        assetsViewModel.filteredAssets.observe(viewLifecycleOwner) { assets ->
            if (assets != null) {
                assetsAdapter.updateList(assets)
            }
        }

        sharedViewModel.assetAdded.observe(viewLifecycleOwner) { isAdded ->
            if (isAdded) {
//                assetsViewModel.refreshAssets()
                sharedViewModel.resetAssetAdded()
            }
        }
    }

    private fun setupSearchView() {
        searchView = binding.svAssets

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.trim()?.uppercase().orEmpty()
                assetsViewModel.searchAssets(query)
                return true
            }
        })
    }

    private val barcodeLauncherForAsset =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val scannedData = result.data?.getStringExtra("SCAN_RESULT") ?: ""
                assetsViewModel.searchAssets(scannedData)
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}