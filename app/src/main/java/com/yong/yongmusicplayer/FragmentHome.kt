package com.yong.yongmusicplayer

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.yong.yongmusicplayer.databinding.FragmentHomeBinding

class FragmentHome : Fragment() {
    companion object {
        val REQ_READ = 99
        val DB_NAME = "musicDB"
        var VERSION = 1
    }

    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: MusicRecyclerAdapter
    private var musicList: MutableList<Music>? = mutableListOf<Music>()
    val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(mainActivity, permission, REQ_READ)
        }
        val dbHelper = DBHelper(mainActivity, DB_NAME, VERSION)

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,

            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return false }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.isNullOrBlank()){
                    musicList?.clear()
                    dbHelper.searchMusic(query)?.let {musicList?.addAll(it)}
                    adapter.notifyDataSetChanged()
                } else {
                    musicList?.clear()
                    dbHelper.selectMusicAll()?.let{musicList?.addAll(it)}
                    adapter.notifyDataSetChanged()
                }
                return false
            }
        })

        return binding.root
    }

    fun startProcess() {
        val dbHelper = DBHelper(mainActivity, DB_NAME, VERSION)
        musicList = dbHelper.selectMusicAll()

        if (musicList == null) {
            val playMusicList = getMusicList()
            if (playMusicList != null) {
                for(i in 0..playMusicList.size -1){
                    val music = playMusicList.get(i)
                    dbHelper.insertMusic(music)
                }
                musicList = playMusicList
            } else {
                Log.d("yongmusicplayer", "외장메모리 음원 파일이 없음")
            }
        }

        adapter = MusicRecyclerAdapter(mainActivity, musicList)
        binding.ftHomeRecycler.adapter = adapter
        binding.ftHomeRecycler.layoutManager = LinearLayoutManager(context)
    }

    fun getMusicList(): MutableList<Music>? {
        var getMusicList: MutableList<Music>? = mutableListOf<Music>()
        val musicURL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        val cursor = context?.contentResolver?.query(musicURL, projection, null, null, null)
        if (cursor?.count!! > 0) {
            while (cursor!!.moveToNext()) {
                val id = cursor.getString(0)
                val title = cursor.getString(1).replace("'", "")
                val artist = cursor.getString(2).replace("'", "")
                val albumId = cursor.getString(3)
                val duration = cursor.getInt(4)
                val music = Music(id, title, artist, albumId, duration, 0)
                getMusicList?.add(music)
            }
        } else {
            getMusicList = null
        }
        return getMusicList
    }

    fun isPermitted(): Boolean {
        return ContextCompat.checkSelfPermission(mainActivity, permission[0]) == PackageManager.PERMISSION_GRANTED
    }
}