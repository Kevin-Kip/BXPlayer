package com.revosleap.bxplayer.AppUtils.Utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.revosleap.bxplayer.AppUtils.Models.Album;
import com.revosleap.bxplayer.AppUtils.Models.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class AlbumProvider {
    @NonNull
    static List<Album> retrieveAlbums(@Nullable final List<Song> songs) {
        final List<Album> albums = new ArrayList<>();
        if (songs != null) {
            for (Song song : songs) {
                getAlbum(albums, song.albumName).songs.add(song);
            }
        }
        if (albums.size() > 1) {
            sortAlbums(albums);
        }
        return albums;
    }

    private static void sortAlbums(@NonNull final List<Album> albums) {
        Collections.sort(albums, new Comparator<Album>() {
            public int compare(Album obj1, Album obj2) {
                return Integer.compare(obj1.getYear(), obj2.getYear());
            }
        });
    }

    private static Album getAlbum(@NonNull final List<Album> albums, final String albumName) {
        for (Album album : albums) {
            if (!album.songs.isEmpty() && album.songs.get(0).albumName.equals(albumName)) {
                return album;
            }
        }
        final Album album = new Album();
        albums.add(album);
        return album;
    }
}
