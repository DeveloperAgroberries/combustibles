package com.agroberriesmx.combustiblesagroberries.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.agroberriesmx.combustiblesagroberries.databinding.FragmentListRecordsBinding
import com.agroberriesmx.combustiblesagroberries.ui.SharedViewModel
import com.agroberriesmx.combustiblesagroberries.ui.list.adapter.ListRecordsAdapter
import com.agroberriesmx.combustiblesagroberries.ui.recordsdetail.ListRecordDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListRecordsFragment : Fragment() {
    private var _binding: FragmentListRecordsBinding? = null
    private val binding get() = _binding!!
    private val listRecordsViewModel by viewModels<ListRecordsViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var recordAdapter: ListRecordsAdapter
    private lateinit var searchView: SearchView

    private val getRecordDetailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            listRecordsViewModel.loadTodayRecords()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListRecordsBinding.inflate(inflater, container, false)
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
        initUIState()
    }

    private fun initList() {
        recordAdapter = ListRecordsAdapter(onItemSelected = {record ->
            val intent = Intent(requireContext(), ListRecordDetailActivity::class.java)
            intent.putExtra("cControlCom", record.cControlCom)
            getRecordDetailResult.launch(intent)
        })

        binding.rvRecords.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = recordAdapter
        }
    }

    private fun initUIState() {

    }

    private fun initObservers() {
        listRecordsViewModel.filteredRecords.observe(viewLifecycleOwner) {records ->
            if (records != null){
                recordAdapter.updateList(records)
            }
        }

        sharedViewModel.recordAdded.observe(viewLifecycleOwner) { isAdded ->
            if(isAdded) {
                listRecordsViewModel.loadTodayRecords()
                listRecordsViewModel.loadAllRecords()
                sharedViewModel.resetRecordAdded()
            }
        }
    }

    private fun setupSearchView() {
        searchView = binding.svRecords

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty()){
                    listRecordsViewModel.loadTodayRecords()
                } else {
                    listRecordsViewModel.searchRecords(newText.trim().uppercase())
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}