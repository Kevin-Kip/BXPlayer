package com.revosleap.bxplayer.utils.utils

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.revosleap.bxplayer.models.Album
import com.revosleap.bxplayer.models.Song
import java.util.*

object SongProvider {
    private val TITLE = 0
    private val TRACK = 1
    private val YEAR = 2
    private val DURATION = 3
    private val PATH = 4
    private val ALBUM = 5
    private val ARTIST_ID = 6
    private val ARTIST = 7

    private val BASE_PROJECTION = arrayOf(MediaStore.Audio.AudioColumns.TITLE, // 0
            MediaStore.Audio.AudioColumns.TRACK, // 1
            MediaStore.Audio.AudioColumns.YEAR, // 2
            MediaStore.Audio.AudioColumns.DURATION, // 3
            MediaStore.Audio.AudioColumns.DATA, // 4
            MediaStore.Audio.AudioColumns.ALBUM, // 5
            MediaStore.Audio.AudioColumns.ARTIST_ID, // 6
            MediaStore.Audio.AudioColumns.ARTIST)// 7

    private val mAllDeviceSongs = mutableListOf<Song>()

    fun getAllDeviceSongs(context: Context): MutableList<Song> {
        return getSongs(makeSongCursor(context, null))
    }

    fun getAllArtistSongs(albums: MutableList<Album>): MutableList<Song> {
        val songsList = mutableListOf<Song>()
        for (album in albums) {
            songsList.addAll(album.songs)
        }
        return songsList
    }

    internal fun getSongs(cursor: Cursor?): MutableList<Song> {
        val songs = mutableListOf<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val song = getSongFromCursorImpl(cursor)
                if (song.duration >= 5000) {
                    songs.add(song)
                    mAllDeviceSongs.add(song)
                }
            } while (cursor.moveToNext())
        }

        cursor?.close()
        if (songs.size > 1) {
            sortSongsByTrack(songs)
        }
        return songs
    }

    private fun sortSongsByTrack(songs: MutableList<Song>) {
        songs.sortWith(Comparator { obj1, obj2 -> java.lang.Long.compare(obj1.trackNumber.toLong(), obj2.trackNumber.toLong()) })
    }

    private fun getSongFromCursorImpl(cursor: Cursor): Song {
        val title = cursor.getString(TITLE)
        val trackNumber = cursor.getInt(TRACK)
        val year = cursor.getInt(YEAR)
        val duration = cursor.getInt(DURATION)
        val uri = cursor.getString(PATH)
        val albumName = cursor.getString(ALBUM)
        val artistId = cursor.getInt(ARTIST_ID)
        val artistName = cursor.getString(ARTIST)

        return Song(title, trackNumber, year, duration, uri, albumName, artistId, artistName)
    }

    internal fun makeSongCursor(context: Context, sortOrder: String?): Cursor? {
        try {
            return context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    BASE_PROJECTION, null, null, sortOrder)
        } catch (e: SecurityException) {
            return null
        }

    }
}
