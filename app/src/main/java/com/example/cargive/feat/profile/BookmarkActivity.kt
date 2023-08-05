package com.example.cargive.feat.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cargive.R
import com.example.cargive.databinding.ActivityBookmarkBinding

class BookmarkActivity : AppCompatActivity(), BottomFragment.BottomFragmentListener {
    private val binding by lazy { ActivityBookmarkBinding.inflate(layoutInflater) }
    private var profileList= arrayListOf<BookmarkModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        profileList= arrayListOf(
            BookmarkModel("우리동네 주차장","6개"),
            BookmarkModel("직장 근처","2개")
        )
        binding.listBmark.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.listBmark.setHasFixedSize(true)

        binding.listBmark.adapter=ProfileAdapter(profileList)
        binding.back.setOnClickListener { finish() }

        binding.addBookmark.setOnClickListener {
            val bottomSheet = BottomFragment()
            bottomSheet.listener=this@BookmarkActivity
            bottomSheet.show(supportFragmentManager,bottomSheet.tag)
        }

    }

    override fun onInformationReceived(info: String) {
        val newBookmark = BookmarkModel(info, "0개")
        profileList.add(newBookmark)
        binding.listBmark.adapter?.notifyItemInserted(profileList.size-1)
    }
}