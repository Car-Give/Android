package com.example.cargive.feat.etc

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cargive.data.etc.InquiryModel
import com.example.cargive.databinding.InquiryListBinding


class InquiryAdapter: ListAdapter<InquiryModel, RecyclerView.ViewHolder>(DiffCallBack) {

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<InquiryModel>() {
            override fun areItemsTheSame(oldItem: InquiryModel, newItem: InquiryModel): Boolean {

                return oldItem.date == newItem.date && oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: InquiryModel, newItem: InquiryModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = InquiryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderShared(binding)
    }

    inner class ViewHolderShared(private val binding: InquiryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: InquiryModel){
            binding.inquiryTitle.text = item.title
            binding.inquiryDate.text = item.date
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