package com.example.cargive.feat.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.cargive.R
import org.w3c.dom.Text

class ProfileAdapter(val profileList: ArrayList<BookmarkModel>) :RecyclerView.Adapter<ProfileAdapter.CustomViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.CustomViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark,parent,false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileAdapter.CustomViewHolder, position: Int) {
        holder.mark_name.text= profileList.get(position).bookmark_name
        holder.mark_cnt.text=profileList.get(position).bookmark_count

    }

    override fun getItemCount(): Int {
        return profileList.size
    }
    class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mark_name =itemView.findViewById<TextView>(R.id.mark_name)//즐겨찾기 이름
        val mark_cnt=itemView.findViewById<TextView>(R.id.mark_count)//즐겨찾기 내부 개수
    }
}