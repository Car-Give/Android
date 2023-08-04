package com.example.cargive.feat.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cargive.databinding.ParkingLotListBinding

class ParkingLotListAdapter(private val arr: ArrayList<String>) : RecyclerView.Adapter<ParkingLotListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingLotListAdapter.ViewHolder {
        val binding = ParkingLotListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = arr.size

    inner class ViewHolder(private val binding: ParkingLotListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            binding.placeName.text = arr[position]
        }

    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ParkingLotListAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }
}