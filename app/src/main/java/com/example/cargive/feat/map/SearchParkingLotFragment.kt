package com.example.cargive.feat.map

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cargive.databinding.FragementChooseParkinglotBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchParkingLotFragment(private val name: String) : BottomSheetDialogFragment() {
    private lateinit var binding: FragementChooseParkinglotBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragementChooseParkinglotBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchPlaceName.text = name
        val adapter = ParkingLotListAdapter(arrayListOf("1","2","3","4","5","6","7","8"))
        binding.searchResult.adapter = adapter
        binding.searchResult.layoutManager = LinearLayoutManager(requireContext())
        val arr1 : MutableList<String> = mutableListOf("추천 순", "인기 순", "거리 순")
        binding.sortSpinner
        val spinnerAdapter = ArrayAdapter<String>(requireContext(), R.layout.simple_list_item_1, arr1)
        binding.sortSpinner.adapter =spinnerAdapter
        binding.sortSpinner.setSelection(0)
        binding.sortSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position) {
                    0 -> {

                    }
                    1-> {

                    }
                    2 -> {

                    }
                    else -> {

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


    }
}