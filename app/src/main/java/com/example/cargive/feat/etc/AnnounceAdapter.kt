package com.example.cargive.feat.etc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cargive.R
import com.example.cargive.databinding.EtcListBinding
import com.example.cargive.data.etc.EtcModels
import com.example.cargive.data.etc.AnnounceModel


class AnnounceAdapter: ListAdapter<AnnounceModel, RecyclerView.ViewHolder>(DiffCallBack) {

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<AnnounceModel>() {
            override fun areItemsTheSame(oldItem: AnnounceModel, newItem: AnnounceModel): Boolean {

                return oldItem.date == newItem.date && oldItem.content == newItem.content
            }

            override fun areContentsTheSame(oldItem: AnnounceModel, newItem: AnnounceModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = EtcListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderShared(binding)
    }

    inner class ViewHolderShared(private val binding: EtcListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EtcModels){
            binding.etcImg.setImageResource(R.drawable.announcement)
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