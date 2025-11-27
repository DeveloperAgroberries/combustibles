package com.agroberriesmx.combustiblesagroberries.ui.list.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.agroberriesmx.combustiblesagroberries.databinding.ItemEntryRecordBinding
import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel

class ListRecordsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ItemEntryRecordBinding.bind(itemView)

    fun bind(record: RecordModel, onItemSelected: (RecordModel) -> Unit) {
        binding.tvDate.text = record.date
        binding.tvFixedAssetCode.text = record.fixedAssetCode
        binding.tvOdometer.text = record.odometer
        binding.tvLitersLoaded.text = record.liters
        binding.root.setOnClickListener {
            onItemSelected(record)
        }
    }
}