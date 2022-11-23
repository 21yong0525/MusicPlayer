package com.yong.yongmusicplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.yong.yongmusicplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
    lateinit var fragmentHome: FragmentHome
    lateinit var fragmentPlayMusic: FragmentPlayMusic
    lateinit var fragmentRecommend: FragmentRecommend
    lateinit var fragmentUserList: FragmentUserList
    lateinit var binding: ActivityMainBinding
    private var messengerJob: Job? = null
    private var mediaPlayer2: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentHome = FragmentHome()
        fragmentPlayMusic = FragmentPlayMusic()
        fragmentRecommend = FragmentRecommend()
        fragmentUserList = FragmentUserList()

        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragmentHome)
            .commit()
        val tab1: TabLayout.Tab = binding.tabLayout.newTab()
        tab1.text = "홈"
        val tab2: TabLayout.Tab = binding.tabLayout.newTab()
        tab2.text = "플레이"
        val tab3: TabLayout.Tab = binding.tabLayout.newTab()
        tab3.text = "추천"
        val tab4: TabLayout.Tab = binding.tabLayout.newTab()
        tab4.text = "내음악"

        binding.tabLayout.addTab(tab1)
        binding.tabLayout.addTab(tab2)
        binding.tabLayout.addTab(tab3)
        binding.tabLayout.addTab(tab4)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    "홈" -> changeFragment("홈", null, null, null)
                    "플레이" -> changeFragment("플레이", null, null, null)
                    "추천" -> changeFragment("추천", null, null, null)
                    "내음악" -> changeFragment("내음악", null, null, null)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    fun like() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragmentHome)
            .commit()
    }

    fun changeFragment(data: String, music: Music?, playList: ArrayList<Parcelable>?, position: Int?) {
        var tabIndex: TabLayout.Tab?
        val bundle = Bundle()
        val inputMusic = music
        bundle.putParcelable("music", inputMusic)
        bundle.putParcelableArrayList("playList", playList)
        if (position != null){
            bundle.putInt("position", position)
        }

        when (data) {
            "홈" -> {
                tabIndex = binding.tabLayout.getTabAt(0)
                binding.tabLayout.selectTab(tabIndex)
                fragmentHome.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, fragmentHome)
                    .commit()
            }
            "플레이" -> {
                tabIndex = binding.tabLayout.getTabAt(1)
                binding.tabLayout.selectTab(tabIndex)
                fragmentPlayMusic.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, fragmentPlayMusic)
                    .commit()
            }
            "추천" -> {
                tabIndex = binding.tabLayout.getTabAt(2)
                binding.tabLayout.selectTab(tabIndex)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, fragmentRecommend)
                    .commit()
            }
            "내음악" -> {
                tabIndex = binding.tabLayout.getTabAt(3)
                binding.tabLayout.selectTab(tabIndex)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, fragmentUserList)
                    .commit()
            }
        }
    }

    fun job(mediaPlayer: MediaPlayer?, music: Music?) {

        messengerJob?.cancel()
        mediaPlayer?.stop()
        mediaPlayer2 = MediaPlayer.create(this@MainActivity, music?.getMusicUri())
        mediaPlayer2 = mediaPlayer
        mediaPlayer2?.isLooping = true
        mediaPlayer2?.start()

        fragmentPlayMusic.binding.totalDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)
        fragmentPlayMusic.binding.seekBar2.max = mediaPlayer2!!.duration
        fragmentPlayMusic.binding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer2?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
        messengerJob = backgroundScope.launch {
            while (mediaPlayer2?.isPlaying == true) {
                runOnUiThread {
                    var currentPosition = mediaPlayer2?.currentPosition!!
                    fragmentPlayMusic.binding.seekBar2.progress = currentPosition
                    val currentDurateion =
                        SimpleDateFormat("mm:ss").format(mediaPlayer2!!.currentPosition)
                    fragmentPlayMusic.binding.playDuration.text = currentDurateion
                }
                try {
                    delay(1000)
                } catch (e: Exception) {
                    Log.d("yongmusicplayer", "${e.stackTrace}")
                }
            }//end of while
            runOnUiThread {
                if (mediaPlayer2!!.currentPosition >= (fragmentPlayMusic.binding.seekBar2.max - 1000)) {
                    fragmentPlayMusic.binding.seekBar2.progress = 0
                    fragmentPlayMusic.binding.playDuration.text = "00:00"
                }
            }
        }//end of messengerJob

        fragmentPlayMusic.binding.albumImage.setOnClickListener {
            play(backgroundScope)
        }
    }

    fun play(backgroundScope: CoroutineScope) {
        if (mediaPlayer2?.isPlaying == true) {
            mediaPlayer2?.pause()
        } else {
            mediaPlayer2?.isLooping = true
            mediaPlayer2?.start()

            messengerJob = backgroundScope.launch {
                while (mediaPlayer2?.isPlaying == true) {
                    runOnUiThread {
                        var currentPosition = mediaPlayer2?.currentPosition!!
                        fragmentPlayMusic.binding.seekBar2.progress = currentPosition
                        val currentDurateion =
                            SimpleDateFormat("mm:ss").format(mediaPlayer2!!.currentPosition)
                        fragmentPlayMusic.binding.playDuration.text = currentDurateion
                    }
                    try {
                        delay(1000)
                    } catch (e: Exception) {
                        Log.d("yongmusicplayer", "${e.stackTrace}")
                    }
                }
                runOnUiThread {
                    if (mediaPlayer2!!.currentPosition >= (fragmentPlayMusic.binding.seekBar2.max - 1000)) {
                        fragmentPlayMusic.binding.seekBar2.progress = 0
                        fragmentPlayMusic.binding.playDuration.text = "00:00"
                    }
                }
            }
        }
    }
}
