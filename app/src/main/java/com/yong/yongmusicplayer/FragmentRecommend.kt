package com.yong.yongmusicplayer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yong.yongmusicplayer.databinding.FragmentRecommendBinding

class FragmentRecommend : Fragment() {
    lateinit var adapter: MusicRecyclerAdapter
    private var musicList: MutableList<Music>? = mutableListOf<Music>()
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecommendBinding.inflate(inflater, container, false)

        val dbHelper = DBHelper(mainActivity, FragmentHome.DB_NAME, FragmentHome.VERSION)
        var count = dbHelper.countArtist()

        binding.textView.text = count?.get((0 until count.size).random())

        adapter = MusicRecyclerAdapter(mainActivity, musicList)
        musicList?.clear()
        dbHelper.selectRecommend(binding.textView.text.toString())?.let{musicList?.addAll(it)}
        binding.ftRecommendRecycler.adapter = adapter
        binding.ftRecommendRecycler.layoutManager = LinearLayoutManager(context)

        adapter.notifyDataSetChanged()

        return binding.root
    }
}