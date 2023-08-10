package com.example.cargive.feat.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cargive.R

class MycarAdapter(val carList:ArrayList<MycarModel>):RecyclerView.Adapter<MycarAdapter.CarViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MycarAdapter.CarViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_car,parent,false)
        return MycarAdapter.CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: MycarAdapter.CarViewHolder, position: Int) {
        holder.mycar_num.text= carList.get(position).carNum
        holder.mycar_repair.text=carList.get(position).carRepair
        holder.mycar_distance.text=carList.get(position).carDistance
    }

    override fun getItemCount(): Int {
        return carList.size
    }
    class CarViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val mycar_img =itemView.findViewById<ImageView>(R.id.mycar_img)
        val mycar_num=itemView.findViewById<TextView>(R.id.mycar_num)
        val mycar_repair=itemView.findViewById<TextView>(R.id.repair_day)
        val mycar_distance=itemView.findViewById<TextView>(R.id.distance)
    }

}