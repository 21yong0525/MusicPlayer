package com.yong.yongmusicplayer

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yong.yongmusicplayer.databinding.FragmentPlayMusicBinding

class FragmentPlayMusic : Fragment() {
    lateinit var binding : FragmentPlayMusicBinding
    private var music: Music? = null
    lateinit var mainActivity: MainActivity
    private var mediaPlayer: MediaPlayer? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayMusicBinding.inflate(layoutInflater, container, false)

        if(this.arguments?.getParcelable<Music>("music") != null){
            music = this.arguments?.getParcelable<Music>("music")
            binding.albumArtist.text = music?.artist
            binding.albumTitle.text = music?.title
            binding.albumTitle.isSingleLine = true
            binding.albumTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
            binding.albumTitle.isSelected = true
            val bitmap = music?.getAlbumImage(mainActivity,MusicRecyclerAdapter.ALBUM_SIZE)
            if (bitmap != null){
                binding.albumImage.setImageBitmap(bitmap)
            }else{
                binding.albumImage.setImageResource(R.drawable.ic_music_24)
            }
            mediaPlayer = MediaPlayer.create(mainActivity, music?.getMusicUri())
        }

        binding.albumImage.setOnClickListener {
            mainActivity.job(mediaPlayer,music)
        }

        var position = this.arguments?.getInt("position")

        binding.tvPrevious.setOnClickListener {
            val playList = this.arguments?.getParcelableArrayList<Music>("playList")
            if (position != null) {
                position--
                music = playList?.get(position) as Music
            }
            binding.albumArtist.text = music?.artist
            binding.albumTitle.text = music?.title
            binding.albumTitle.isSingleLine = true
            binding.albumTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
            binding.albumTitle.isSelected = true
            val bitmap = music?.getAlbumImage(mainActivity,MusicRecyclerAdapter.ALBUM_SIZE)
            if (bitmap != null){
                binding.albumImage.setImageBitmap(bitmap)
            }else{
                binding.albumImage.setImageResource(R.drawable.ic_music_24)
            }
            mediaPlayer = MediaPlayer.create(mainActivity, music?.getMusicUri())
            mainActivity.job(mediaPlayer,music)
        }

        binding.tvNext.setOnClickListener {
            val playList = this.arguments?.getParcelableArrayList<Music>("playList")
            if (position != null) {
                position++
                music = playList?.get(position) as Music
            }
            binding.albumArtist.text = music?.artist
            binding.albumTitle.text = music?.title
            binding.albumTitle.isSingleLine = true
            binding.albumTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
            binding.albumTitle.isSelected = true
            val bitmap = music?.getAlbumImage(mainActivity,MusicRecyclerAdapter.ALBUM_SIZE)
            if (bitmap != null){
                binding.albumImage.setImageBitmap(bitmap)
            }else{
                binding.albumImage.setImageResource(R.drawable.ic_music_24)
            }
            mediaPlayer = MediaPlayer.create(mainActivity, music?.getMusicUri())
            mainActivity.job(mediaPlayer,music)
        }

        return binding.root
    }
}