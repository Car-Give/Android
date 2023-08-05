package com.example.cargive.feat.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.cargive.R
import com.example.cargive.databinding.ActivityLoginBinding
import com.example.cargive.databinding.BottomFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
class BottomFragment: BottomSheetDialogFragment(){
    private lateinit var binding: BottomFragmentBinding

    interface BottomFragmentListener {
        fun onInformationReceived(info: String)
    }
    var listener: BottomFragmentListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding=BottomFragmentBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeBtn.setOnClickListener{
            dismiss()
        }
        with(binding){
            plusName.addTextChangedListener(object :TextWatcher{
                var maxText = ""
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    maxText=p0.toString()
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(plusName.lineCount>1){
                        plusName.setText(maxText)
                        plusName.setSelection(plusName.length())
                        txtCnt.text = "${plusName.length()}/20"
                    } else if(plusName.length()>20){
                        plusName.setText(maxText)
                        plusName.setSelection(plusName.length())
                        txtCnt.text = "${plusName.length()}/20"
                    } else{
                        txtCnt.text = "${plusName.length()}/20"
                    }
                    if(plusName.length()==0){
                        plusPlace.setBackgroundResource(R.drawable.gray_plusmark)
                    }
                    else{
                        plusPlace.setBackgroundResource(R.drawable.plus_bookmark)

                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })

            plusPlace.setOnClickListener{
                var maxText=plusName.text.toString()
                if(maxText.isEmpty()){
                    Toast.makeText(requireContext(),"먼저 즐겨찾기 이름을 설정 해 주십시오",Toast.LENGTH_SHORT).show()
                }
                else{
                    if(listener!=null) {
                        listener!!.onInformationReceived(maxText)
                        dismiss()
                    }
                }
            }
        }
    }
}