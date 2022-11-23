package com.yong.yongmusicplayer

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context, dbName: String, version: Int): SQLiteOpenHelper(context, dbName, null, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            create table musicTBL(
                id text primary key,
                title text,
                artist text,
                albumId text,
                duration integer,
                likes integer
            )
        """.trimIndent()
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, newVersion: Int, oldVersion: Int) {
        val query = """
            drop table musicTBL
        """.trimIndent()
        db?.execSQL(query)
        this.onCreate(db)
    }

    fun selectMusicAll(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val query = """
            select * from musicTBL
        """.trimIndent()
        val db = this.readableDatabase

        try{
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            }else{
                musicList = null
            }
        }catch (e: Exception){
            Log.d("yongmusicplayer", "${e.printStackTrace()}")
            musicList = null
        }finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun insertMusic(music: Music): Boolean {
        var flag = false
        val query = """
            insert into musicTBL(id, title, artist, albumId, duration, likes)
            values('${music.id}', '${music.title}', '${music.artist}', '${music.albumId}', '${music.duration}', '${music.likes}')
        """.trimIndent()
        val db = this.writableDatabase

        try{
            db.execSQL(query)
            flag = true
        }catch (e: Exception){
            Log.d("yongmusicplayer", "${e.printStackTrace()}")
            flag = false
        }finally {
            db.close()
        }
        return flag
    }

    fun updateLike(music: Music): Boolean {
        var flag = false
        val query = """
            update musicTBL set likes = ${music.likes} where id = '${music.id}'
        """.trimIndent()
        val db = this.writableDatabase

        try{
            db.execSQL(query)
            flag = true
        }catch (e: Exception){
            Log.d("yongmusicplayer", "${e.printStackTrace()}")
            flag = false
        }finally {
            db.close()
        }
        return flag
    }

    fun searchMusic(query: String?): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val query = """
            select * from musicTBL where title like '%${query}%' or artist like '%${query}%'
        """.trimIndent()
        val db = this.readableDatabase
        try{
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            }else{
                musicList = null
            }
        }catch (e: Exception){
            Log.d("yongmusicplayer", "${e.printStackTrace()}")
            musicList = null
        }finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun selectMusicLike(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val query = """
            select * from musicTBL where likes = 1
        """.trimIndent()
        val db = this.readableDatabase

        try{
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            }else{
                musicList = null
            }
        }catch (e: Exception){
            Log.d("yongmusicplayer", "${e.printStackTrace()}")
            musicList = null
        }finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun countArtist(): MutableList<String>? {
        var cursor: Cursor? = null
        var artistList: MutableList<String>? = mutableListOf<String>()
        val query = """
            select distinct artist from musicTBL
        """.trimIndent()
        val db = this.readableDatabase
        try{
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0) {
                while(cursor.moveToNext()){
                    artistList?.add(cursor.getString(0))
                }
            }
        }catch (e: Exception) {
            Log.d("yongmusicplayer", "${e.printStackTrace()}")
            artistList = null
        }finally {
            cursor?.close()
            db.close()
        }

        return artistList
    }

    fun selectRecommend(artist: String): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val query = """
            select * from musicTBL where artist = '$artist'
        """.trimIndent()
        val db = this.readableDatabase

        try{
            cursor = db.rawQuery(query, null)
            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            }else{
                musicList = null
            }
        }catch (e: Exception){
            Log.d("yongmusicplayer", "${e.printStackTrace()}")
            musicList = null
        }finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }
}