package com.example.cargive.feat.etc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cargive.R
import com.example.cargive.data.etc.FAQModel
import com.example.cargive.databinding.FaqListBinding


class FAQAdapter: ListAdapter<FAQModel, RecyclerView.ViewHolder>(DiffCallBack) {

    companion object {
        private val DiffCallBack = object : DiffUtil.ItemCallback<FAQModel>() {
            override fun areItemsTheSame(oldItem: FAQModel, newItem: FAQModel): Boolean {

                return oldItem.title == newItem.title && oldItem.answer == newItem.answer
            }

            override fun areContentsTheSame(oldItem: FAQModel, newItem: FAQModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = FaqListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderShared(binding)
    }

    inner class ViewHolderShared(private val binding: FaqListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FAQModel){
            binding.faqTitle.text = item.title
            binding.faqAnswer.text = item.answer
            binding.faqDown.setOnClickListener {
                if(binding.faqDown.tag == "up") {
                    binding.faqDown.tag = "down"
                    binding.faqDown.setImageResource(R.drawable.arrow_down)
                    binding.faqAnswer.visibility = View.GONE
                } else {
                    binding.faqDown.tag = "up"
                    binding.faqDown.setImageResource(R.drawable.arrow_up)
                    binding.faqAnswer.visibility = View.VISIBLE
                }
            }
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