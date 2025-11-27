package com.agroberriesmx.combustiblesagroberries.data.network.request

import com.agroberriesmx.combustiblesagroberries.domain.model.FormattedRecordsModel

class SyncRequest(
    val token: String,
    val data: List<FormattedRecordsModel>
)