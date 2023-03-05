package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
            User user = new User();
            user.setName(name);
            user.setMobile(mobile);
            users.add(user);

            return user;
    }

    public Artist createArtist(String name) {
            Artist artist = new Artist();
            artist.setName(name);
            artist.setLikes(0);
            artists.add(artist);

            return artist;
    }

    public Album createAlbum(String title, String artistName) {
//            so we have some cases here
//   1. if an artist exists we add album to his name
//        2. if artist doesn't exist in our arraylist we create a new artist with the name '
            Artist artistdum = null;
            for(Artist artist : artists){
                if(artist.getName() == artistName){
                    artistdum = artist;
                    break;
                }
            }
            // if this is a new artist
        if(artistdum == null){
            artistdum = createArtist(artistName);
//            create album for this artist
        Album album = new Album();
        album.setTitle(title);
        album.setReleaseDate(new Date());

        albums.add(album);

//        put the albums against the artist name in hashmap
//        an artist can have multiple albums so we need a list
        List<Album> albumlist = new ArrayList<>();
        albumlist.add(album);

        artistAlbumMap.put(artistdum,albumlist);
        return album;
        }else{
//            the artist already exists so fetch it's album name from the map'
        Album album = new Album();
        album.setTitle(title);
        album.setReleaseDate(new Date());

        List<Album> albumname = artistAlbumMap.get(artistdum);
        if(albumname == null){
            albumname = new ArrayList<>();
        }
        albumname.add(album);
        artistAlbumMap.put(artistdum,albumname);

        return album;
        }

    }


    public Song createSong(String title, String albumName, int length) throws Exception {
        Album album = null;

        for (Album album1 : albums) {
            if(album.getTitle() == albumName){
                album = album1;
                break;
            }
        }
        if(album == null){
            throw new Exception("Album does not exist");
        }else{
            // we add new song to the album
            Song song = new Song();
            song.setTitle(title);
            song.setLength(length);
            song.setLikes(0);

            songs.add(song);

            if(albumSongMap.containsKey(album)){
                List<Song> l = albumSongMap.get(album);
                l.add(song);
                albumSongMap.put(album,l);

            }else {
                List<Song> songList = albumSongMap.get(album);
                songList.add(song);
                albumSongMap.put(album,songList);
            }
            return song;
        }
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
//1. create playlist with the title given in constructor
        // user creates playlist so if user doesn't exist then throw exception
        User user = null;
        for(User user1 : users){
            if(user1.getMobile() == mobile){
                user = user1;
                break;
            }
        }
        if(user == null) {
            throw new Exception("User doesn't exist");
        }else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> l = new ArrayList<>();
            for(Song song : songs){
                if(song.getLength() == length){
                    l.add(song);
                }
            }

            playlistSongMap.put(playlist,l);
            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(playlist,list);

            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(user,plays);
            }

            return playlist;
        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {

        User user = null;
        for (User user1 : users) {
            if (user1.getMobile() == mobile) {
                user = user1;
                break;
            }
        }
        if (user == null)
            throw new Exception("User does not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> l = new ArrayList<>();
            for (Song song : songs) {
                if (songTitles.contains(song.getTitle())) {
                    l.add(song);
                }
            }
            playlistSongMap.put(playlist, l);

            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(playlist, list);

            creatorPlaylistMap.put(user, playlist);

            if (userPlaylistMap.containsKey(user)) {
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user, userPlayList);
            } else {
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(user, plays);
            }

            return playlist;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
            User user = null;
            for(User user1:users){
                if(user1.getMobile()==mobile){
                    user=user1;
                    break;
                }
            }
            if(user==null)
                throw new Exception("User does not exist");

            Playlist playlist = null;
            for(Playlist playlist1:playlists){
                if(playlist1.getTitle()==playlistTitle){
                    playlist=playlist1;
                    break;
                }
            }
            if(playlist==null)
                throw new Exception("Playlist does not exist");

            if(creatorPlaylistMap.containsKey(user))
                return playlist;

            List<User> listener = playlistListenerMap.get(playlist);
            for(User user1:listener){
                if(user1==user)
                    return playlist;
            }

            listener.add(user);
            playlistListenerMap.put(playlist,listener);

            List<Playlist> playlists1 = userPlaylistMap.get(user);
            if(playlists1 == null){
                playlists1 = new ArrayList<>();
            }
            playlists1.add(playlist);
            userPlaylistMap.put(user,playlists1);

            return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        for (User user1 : users) {
            if (user1.getMobile() == mobile) {
                user = user1;
                break;
            }
        }
        if (user == null)
            throw new Exception("User does not exist");

        Song song = null;
        for (Song song1 : songs) {
            if (song1.getTitle() == songTitle) {
                song = song1;
                break;
            }
        }
        if (song == null)
            throw new Exception("Song does not exist");

        if (songLikeMap.containsKey(song)) {
            List<User> list = songLikeMap.get(song);
            if (list.contains(user)) {
                return song;
            } else {
                int likes = song.getLikes() + 1;
                song.setLikes(likes);
                list.add(user);
                songLikeMap.put(song, list);

                Album album = null;
                for (Album album1 : albumSongMap.keySet()) {
                    List<Song> songList = albumSongMap.get(album1);
                    if (songList.contains(song)) {
                        album = album1;
                        break;
                    }
                }
                Artist artist = null;
                for (Artist artist1 : artistAlbumMap.keySet()) {
                    List<Album> albumList = artistAlbumMap.get(artist1);
                    if (albumList.contains(album)) {
                        artist = artist1;
                        break;
                    }
                }
                int likes1 = artist.getLikes() + 1;
                artist.setLikes(likes1);
                artists.add(artist);
                return song;
            }
        } else {
            int likes = song.getLikes() + 1;
            song.setLikes(likes);
            List<User> list = new ArrayList<>();
            list.add(user);
            songLikeMap.put(song, list);

            Album album = null;
            for (Album album1 : albumSongMap.keySet()) {
                List<Song> songList = albumSongMap.get(album1);
                if (songList.contains(song)) {
                    album = album1;
                    break;
                }
            }
            Artist artist = null;
            for (Artist artist1 : artistAlbumMap.keySet()) {
                List<Album> albumList = artistAlbumMap.get(artist1);
                if (albumList.contains(album)) {
                    artist = artist1;
                    break;
                }
            }
            int likes1 = artist.getLikes() + 1;
            artist.setLikes(likes1);
            artists.add(artist);

            return song;
        }
    }
            public String mostPopularArtist() {
                //
                int max = 0;
                Artist artist1=null;

                for(Artist artist:artists){
                    if(artist.getLikes()>=max){
                        artist1=artist;
                        max = artist.getLikes();
                    }
                }
                if(artist1==null)
                    return null;
                else
                    return artist1.getName();
            }

            public String mostPopularSong() {
                int max=0;
                Song song = null;

                for(Song song1:songLikeMap.keySet()){
                    if(song1.getLikes()>=max){
                        song=song1;
                        max = song1.getLikes();
                    }
                }
                if(song==null)
                    return null;
                else
                    return song.getTitle();
            }
        }