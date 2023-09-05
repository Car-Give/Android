package com.example.cargive.feat.etc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cargive.R


class TypeSpinnerAdapter(private val list: List<String>, context: Context): BaseAdapter() {
    private lateinit var inflater: LayoutInflater
    private var text = ""
    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            text = list[position]
            val newView = inflater.inflate(R.layout.spinner_outer, parent, false)
            (newView?.findViewById(R.id.spinner_inner_text) as TextView).setText(text)
            return newView
        }


        return convertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            text = list[position]
            val newView = inflater.inflate(R.layout.spinner_inner, parent, false)
            (newView?.findViewById(R.id.spinner_inner_text) as TextView).setText(text)
            return newView
        }


        return convertView
    }
}