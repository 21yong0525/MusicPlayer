package com.yong.yongmusicplayer

import android.content.Context
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yong.yongmusicplayer.databinding.ItemRecyclerBinding
import java.text.SimpleDateFormat

class MusicRecyclerAdapter(val context: Context, val musicList: MutableList<Music>?) :
    RecyclerView.Adapter<MusicRecyclerAdapter.CustomViewHolder>() {
    companion object{
        var ALBUM_SIZE = 150
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = holder.binding
        var music = musicList?.get(position)
        binding.tvArtist.text = music?.artist
        binding.tvTitle.text = music?.title
        binding.tvTitle.isSingleLine = true
        binding.tvTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        binding.tvTitle.isSelected = true
        binding.tvDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)
        val bitmap = music?.getAlbumImage(context,MusicRecyclerAdapter.ALBUM_SIZE)
        if (bitmap != null){
            binding.ivAlbumArt.setImageBitmap(bitmap)
            binding.ivAlbumArt.clipToOutline = true
        }else{
            binding.ivAlbumArt.setImageResource(R.drawable.ic_music_24)
        }
        when(music?.likes){
            0 -> {binding.ivItemLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)}
            1 -> {binding.ivItemLike.setImageResource(R.drawable.ic_baseline_favorite_24)}
        }

        binding.root.setOnClickListener{
            val mainActivity = context as MainActivity
            val playList: ArrayList<Parcelable>? = musicList as ArrayList<Parcelable>
            music = playList?.get(position) as Music
            mainActivity?.changeFragment("플레이",music,playList,position)
        }

        binding.ivItemLike.setOnClickListener {
            if (music?.likes == 0){
                binding.ivItemLike.setImageResource(R.drawable.ic_baseline_favorite_24)
                music?.likes = 1
            }else{
                binding.ivItemLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                music?.likes = 0
                val mainActivity = context as MainActivity
                Toast.makeText(context,"${music?.title}의 좋아요를 취소합니다.",Toast.LENGTH_SHORT).show()
                mainActivity.like()
            }
            if (music != null){
                val dbHelper = DBHelper(context, FragmentHome.DB_NAME, FragmentHome.VERSION)
                val flag = dbHelper.updateLike(music!!)
                if (flag){
                    notifyDataSetChanged()
                } else {
                    Log.e("yongmusicplayer","좋아요 업데이트 실패")
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return musicList?.size?: 0
    }

    class CustomViewHolder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root)
}