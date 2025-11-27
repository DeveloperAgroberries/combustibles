package com.agroberriesmx.combustiblesagroberries.ui.assets.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.agroberriesmx.combustiblesagroberries.R
import com.agroberriesmx.combustiblesagroberries.domain.model.FixedAssetModel
import com.agroberriesmx.combustiblesagroberries.domain.model.RecordModel

class AssetsAdapter(
    private var assets: List<FixedAssetModel> = emptyList(),
    private val onItemSelected: (FixedAssetModel) -> Unit
):RecyclerView.Adapter<AssetsViewHolder>() {
    fun updateList(newList: List<FixedAssetModel>) {
        if(newList == assets) return
        val diffResult = DiffUtil.calculateDiff(AssetDiffCallback(assets,newList))
        assets = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class AssetDiffCallback(
        private val oldList: List<FixedAssetModel>,
        private val newList: List<FixedAssetModel>
    ): DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            //oldList[oldItemPosition].cNumeconAfi == newList[newItemPosition].cNumeconAfi
        oldList[oldItemPosition].numecon == newList[newItemPosition].numecon

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetsViewHolder {
        return AssetsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_entry_asset, parent, false)
        )
    }

    override fun getItemCount() = assets.size

    override fun onBindViewHolder(holder: AssetsViewHolder, position: Int) {
        val asset = assets[position]
        holder.bind(asset, onItemSelected)
    }
}