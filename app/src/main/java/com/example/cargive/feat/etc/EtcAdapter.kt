package com.example.cargive.feat.etc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cargive.R
import com.example.cargive.databinding.EtcListBinding
import com.example.cargive.model.etc.EtcModels
import com.example.cargive.model.etc.AnnounceModel
import com.example.cargive.model.etc.UsageHistoryModel


class EtcAdapter(): ListAdapter<EtcModels, RecyclerView.ViewHolder>(DiffCallBack) {

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<EtcModels>() {
            override fun areItemsTheSame(oldItem: EtcModels, newItem: EtcModels): Boolean {

                return oldItem.date == newItem.date && oldItem.content == newItem.content
            }

            override fun areContentsTheSame(oldItem: EtcModels, newItem: EtcModels): Boolean {
                return when {
                    oldItem is UsageHistoryModel && newItem is UsageHistoryModel -> oldItem == newItem
                    oldItem is AnnounceModel && newItem is AnnounceModel -> oldItem == newItem
                    // 다른 아이템 유형에 대한 비교 추가
                    else -> false
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = EtcListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderShared(binding)
    }

    inner class ViewHolderShared(private val binding: EtcListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EtcModels){
            when (item) {
                is UsageHistoryModel -> {
                    binding.etcImg.setImageResource(R.drawable.park_car)

                }
                is AnnounceModel -> {
                    binding.etcImg.setImageResource(R.drawable.announcement)
                }
                // 다른 아이템 유형에 대한 바인딩 처리 추가
            }
            binding.etcContent.text = item.content
            binding.etcDate.text = item.date
        }


    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ViewHolderShared -> {
                holder.bind(item)
            }
        }
    }
}