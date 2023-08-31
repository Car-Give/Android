package com.example.cargive.feat.etc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cargive.R
import com.example.cargive.data.etc.UsageHistoryModel
import com.example.cargive.databinding.UsageHistoryListBinding


class UsageHistoryAdapter: ListAdapter<UsageHistoryModel, RecyclerView.ViewHolder>(DiffCallBack) {

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<UsageHistoryModel>() {
            override fun areItemsTheSame(oldItem: UsageHistoryModel, newItem: UsageHistoryModel): Boolean {

                return oldItem.date == newItem.date && oldItem.content == newItem.content
            }

            override fun areContentsTheSame(oldItem: UsageHistoryModel, newItem: UsageHistoryModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = UsageHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderShared(binding)
    }

    inner class ViewHolderShared(private val binding: UsageHistoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UsageHistoryModel){
            binding.etcImg.setImageResource(R.drawable.park_car)
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