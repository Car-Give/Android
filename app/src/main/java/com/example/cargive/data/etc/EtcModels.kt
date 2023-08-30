package com.example.cargive.data.etc

sealed class EtcModels {
    abstract val type: String
    abstract val content: String
    abstract val date: String
}

data class UsageHistoryModel(
    override val type: String = "history",
    override val content: String,
    override val date: String
) : EtcModels()

data class AnnounceModel(
    override val type: String = "notice",
    override val content: String,
    override val date: String
) : EtcModels()