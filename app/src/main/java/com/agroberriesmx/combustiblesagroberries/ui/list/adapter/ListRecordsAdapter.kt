package com.agroberriesmx.combustiblesagroberries.ui.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.agroberriesmx.combustiblesagroberries.R
import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel

class ListRecordsAdapter(
    private var records: List<RecordModel> = emptyList(),
    private val onItemSelected: (RecordModel) -> Unit
) : RecyclerView.Adapter<ListRecordsViewHolder>() {

    fun updateList(newList: List<RecordModel>) {
        val diffResult = DiffUtil.calculateDiff(RecordDiffCallback(records, newList))
        records = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class RecordDiffCallback(
        private val oldList: List<RecordModel>,
        private val newList: List<RecordModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].date == newList[newItemPosition].date

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListRecordsViewHolder {
        return ListRecordsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_entry_record, parent, false)
        )
    }

    override fun getItemCount() = records.size

    override fun onBindViewHolder(holder: ListRecordsViewHolder, position: Int) {
        holder.bind(records[position], onItemSelected)
    }
}