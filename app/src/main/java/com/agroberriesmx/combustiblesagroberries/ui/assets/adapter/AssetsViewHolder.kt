package com.agroberriesmx.combustiblesagroberries.ui.assets.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.agroberriesmx.combustiblesagroberries.databinding.ItemEntryAssetBinding
import com.agroberriesmx.combustiblesagroberries.domain.model.FixedAssetModel

class AssetsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val binding = ItemEntryAssetBinding.bind(itemView)

    fun bind(asset: FixedAssetModel, onItemSelected: (FixedAssetModel) -> Unit) {
        binding.tvAssetCode.text = asset.numecon
        binding.tvAssetName.text = asset.nombreAfi
        binding.tvAssetPlate.text = asset.placas
        binding.tvFechaAsset.text = asset.fecha
        binding.tvLitrosAsset.text = asset.litros
        binding.tvCampoAsset.text = asset.campo
        binding.tvZonaAsset.text = asset.zona
        binding.tvActividadAsset.text = asset.actividad
        binding.root.setOnClickListener {
            onItemSelected(asset)
        }
    }
}